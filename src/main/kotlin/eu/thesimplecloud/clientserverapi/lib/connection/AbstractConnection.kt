package eu.thesimplecloud.clientserverapi.lib.connection

import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOFileTransfer
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOFileTransferComplete
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOCreateFileTransfer
import eu.thesimplecloud.clientserverapi.lib.filetransfer.util.QueuedFile
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.WrappedResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

abstract class AbstractConnection(val packetManager: PacketManager, val packetResponseManager: PacketResponseManager) : IConnection {

    val BYTES_PER_PACKET = 50000

    @Volatile
    var sendingFile = false
    val queue = LinkedBlockingQueue<QueuedFile>()

    @Synchronized
    override fun <T : Any> sendQuery(packet: IPacket, expectedResponseClass: Class<T>, timeout: Long): ICommunicationPromise<T> {
        val packetResponseHandler = ObjectPacketResponseHandler(expectedResponseClass)
        val uniqueId = UUID.randomUUID()
        val packetPromise = CommunicationPromise<T>(timeout)
        packetResponseManager.registerResponseHandler(uniqueId, WrappedResponseHandler(packetResponseHandler, packetPromise))
        val idFromPacket = packetManager.getIdFromPacket(packet)
        if (idFromPacket == null) {
            GlobalScope.launch {
                val packetIdCompletableFuture = packetManager.getPacketIdCompletableFuture(packet::class.java)
                try {
                    val id = packetIdCompletableFuture.get(1, TimeUnit.SECONDS)
                    sendPacket(WrappedPacket(PacketData(uniqueId, id, packet::class.java.simpleName), packet))
                } catch (ex: TimeoutException) {
                    throw PacketException("No id for packet ${packet::class.java.simpleName} was available after one second. It looks like this packet was not registered.")
                }
            }
        } else {
            this.sendPacket(WrappedPacket(PacketData(uniqueId, idFromPacket, packet::class.java.simpleName), packet))
        }
        return packetPromise
    }

    /**
     * Sends a packet
     * Can be overridden to prevent packet sending when the connection is not open yet.
     */
    @Synchronized
    open fun sendPacket(wrappedPacket: WrappedPacket) {
        if (!isOpen()) throw IOException("Connection is not open.")
        getChannel()?.eventLoop()?.execute { getChannel()?.writeAndFlush(wrappedPacket) }
    }

    @Synchronized
    override fun sendFile(file: File, savePath: String, timeout: Long): ICommunicationPromise<Unit> {
        val unitPromise = CommunicationPromise<Unit>(timeout)
        val queuedFile = QueuedFile(file, savePath, unitPromise)
        sendQueuedFile(queuedFile)
        return unitPromise
    }

    @Synchronized
    private fun sendQueuedFile(queuedFile: QueuedFile) {
        if (sendingFile) {
            this.queue.add(queuedFile)
            return
        }
        this.sendingFile = true
        val transferUuid = UUID.randomUUID()
        val fileBytes = Files.readAllBytes(queuedFile.file.toPath())
        var bytes = fileBytes.size
        GlobalScope.launch {
            sendUnitQuery(PacketIOCreateFileTransfer(transferUuid, queuedFile.savePath)).syncUninterruptibly()
            while (bytes != 0) {
                when {
                    bytes > BYTES_PER_PACKET -> {
                        val sendBytes = Arrays.copyOfRange(fileBytes, fileBytes.size - bytes, (fileBytes.size - bytes) + BYTES_PER_PACKET)
                        bytes -= BYTES_PER_PACKET
                        sendUnitQuery(PacketIOFileTransfer(transferUuid, sendBytes)).syncUninterruptibly()
                    }
                    else -> {
                        val sendBytes = Arrays.copyOfRange(fileBytes, fileBytes.size - bytes, fileBytes.size)
                        bytes = 0
                        sendUnitQuery(PacketIOFileTransfer(transferUuid, sendBytes)).syncUninterruptibly()
                    }
                }
            }
            sendUnitQuery(PacketIOFileTransferComplete(transferUuid)).syncUninterruptibly()
            queuedFile.promise.trySuccess(Unit)

            //check for next file
            sendingFile = false
            if (queue.isNotEmpty()) {
                sendQueuedFile(queue.poll())
            }
        }
    }

}