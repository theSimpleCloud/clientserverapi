/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.lib.connection

import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOCreateFileTransfer
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOFileTransfer
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOFileTransferComplete
import eu.thesimplecloud.clientserverapi.lib.filetransfer.util.QueuedFile
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.IPacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.WrappedResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

abstract class AbstractConnection(val packetManager: IPacketManager, val packetResponseManager: IPacketResponseManager) : IConnection {

    private val BYTES_PER_FILEPACKET = 50000

    @Volatile
    private var wasCloseIntended: Boolean = false
    @Volatile
    private var sendingFile = false
    private val fileQueue = LinkedBlockingQueue<QueuedFile>()

    @Synchronized
    override fun <T : Any> sendQuery(packet: IPacket, expectedResponseClass: Class<T>, timeout: Long): ICommunicationPromise<T> {
        val packetResponseHandler = ObjectPacketResponseHandler(expectedResponseClass)
        val uniqueId = UUID.randomUUID()
        val packetPromise = CommunicationPromise<T>(timeout)
        packetResponseManager.registerResponseHandler(uniqueId, WrappedResponseHandler(packetResponseHandler, packetPromise))
        this.sendPacket(WrappedPacket(PacketData(uniqueId, packet::class.java.simpleName, false), packet), packetPromise)
        return packetPromise
    }

    /**
     * Sends a packet
     * Can be overridden to prevent packet sending when the connection is not open yet.
     */
    @Synchronized
    open fun sendPacket(wrappedPacket: WrappedPacket, promise: ICommunicationPromise<out Any>) {
        if (!isOpen()) {
            val exception = IOException("Connection is closed. Packet to send was ${wrappedPacket.packetData.sentPacketName}.")
            promise.tryFailure(exception)
            throw exception
        }
        val channel = getChannel()!!
        channel.eventLoop().execute {
            channel.writeAndFlush(wrappedPacket).addListener {
                if (!it.isSuccess)
                    promise.tryFailure(it.cause())
            }
        }
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
            this.fileQueue.add(queuedFile)
            return
        }
        this.sendingFile = true
        val transferUuid = UUID.randomUUID()
        val fileBytes = Files.readAllBytes(queuedFile.file.toPath())
        var bytes = fileBytes.size
        GlobalScope.launch {
            sendUnitQuery(PacketIOCreateFileTransfer(transferUuid, queuedFile.savePath, queuedFile.file.lastModified())).syncUninterruptibly()
            while (bytes != 0) {
                when {
                    bytes > BYTES_PER_FILEPACKET -> {
                        val sendBytes = Arrays.copyOfRange(fileBytes, fileBytes.size - bytes, (fileBytes.size - bytes) + BYTES_PER_FILEPACKET)
                        bytes -= BYTES_PER_FILEPACKET
                        sendUnitQuery(PacketIOFileTransfer(transferUuid, sendBytes), 3000).syncUninterruptibly()
                    }
                    else -> {
                        val sendBytes = Arrays.copyOfRange(fileBytes, fileBytes.size - bytes, fileBytes.size)
                        bytes = 0
                        sendUnitQuery(PacketIOFileTransfer(transferUuid, sendBytes), 3000).syncUninterruptibly()
                    }
                }
            }
            sendUnitQuery(PacketIOFileTransferComplete(transferUuid), 3000).syncUninterruptibly()
            queuedFile.promise.trySuccess(Unit)

            //check for next file
            sendingFile = false
            if (fileQueue.isNotEmpty()) {
                sendQueuedFile(fileQueue.poll())
            }
        }
    }

    fun setConnectionCloseIntended() {
        this.wasCloseIntended = true
    }

    override fun wasConnectionCloseIntended(): Boolean {
        return !isOpen() && wasCloseIntended
    }

}