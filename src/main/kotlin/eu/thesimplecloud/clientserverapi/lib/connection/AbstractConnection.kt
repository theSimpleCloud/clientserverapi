package eu.thesimplecloud.clientserverapi.lib.connection

import eu.thesimplecloud.clientserverapi.lib.filetransfer.packets.PacketIOFileTransfer
import eu.thesimplecloud.clientserverapi.lib.filetransfer.packets.PacketIOFileTransferComplete
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.WrappedResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

abstract class AbstractConnection(val packetManager: PacketManager, val packetResponseManager: PacketResponseManager) : IConnection {

    override fun <T : Any> sendQuery(packet: IPacket, packetResponseHandler: IPacketResponseHandler<T>): ICommunicationPromise<T> {
        val uniqueId = UUID.randomUUID()
        val packetPromise = getCommunicationBootstrap().newPromise<T>()
        packetResponseManager.registerResponseHandler(uniqueId, WrappedResponseHandler(packetResponseHandler, packetPromise))
        val idFromPacket = packetManager.getIdFromPacket(packet)
        if (idFromPacket == null) {
            GlobalScope.launch {
                val packetIdCompletableFuture = packetManager.getPacketIdCompletableFuture(packet::class.java)
                try {
                    val id = packetIdCompletableFuture.get(1, TimeUnit.SECONDS)
                    sendPacket(WrappedPacket(PacketData(uniqueId, id), packet))
                } catch (ex: TimeoutException) {
                    throw PacketException("No id for packet ${packet::class.java.simpleName} was available after one second. It looks like this packet was not registered.")
                }
            }
        } else {
            this.sendPacket(WrappedPacket(PacketData(uniqueId, idFromPacket), packet))
        }
        return packetPromise
    }

    /**
     * Sends a packet
     * Can be overridden to prevent packet sending when the connection is not open yet.
     */
    open fun sendPacket(wrappedPacket: WrappedPacket) {
        if (!isOpen()) throw IOException("Connection is not open.")
        getChannel()?.writeAndFlush(wrappedPacket)?.syncUninterruptibly()
    }

    override fun sendFile(file: File, savePath: String): ICommunicationPromise<Unit> {
        val transferUuid = UUID.randomUUID()
        val fileBytes = Files.readAllBytes(file.toPath())
        var bytes = fileBytes.size
        val packetPromise = getCommunicationBootstrap().newPromise<Unit>()
        GlobalScope.launch {
            while (bytes != 0) {
                when {
                    bytes > 5000 -> {
                        val sendBytes = fileBytes.copyOfRange(fileBytes.size - bytes, (fileBytes.size - bytes) + 5000)
                        bytes -= 50000
                        sendQuery(PacketIOFileTransfer(transferUuid, sendBytes)).syncUninterruptibly()
                    }
                    else -> {
                        val sendBytes = fileBytes.copyOfRange(fileBytes.size - bytes, fileBytes.size)
                        bytes = 0
                        sendQuery(PacketIOFileTransfer(transferUuid, sendBytes)).syncUninterruptibly()
                    }
                }
            }
            sendQuery(PacketIOFileTransferComplete(transferUuid, savePath)).syncUninterruptibly()
            packetPromise.trySuccess(Unit)
        }
        return packetPromise
    }

}