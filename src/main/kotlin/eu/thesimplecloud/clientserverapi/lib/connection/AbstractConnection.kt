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
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.AbstractPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

abstract class AbstractConnection() : AbstractPacketSender(), IConnection {

    private val BYTES_PER_FILEPACKET = 50000

    @Volatile
    private var wasCloseIntended: Boolean = false
    @Volatile
    private var sendingFile = false
    @Volatile
    private var authenticated = false
    private val fileQueue = LinkedBlockingQueue<QueuedFile>()

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
            sendUnitQuery(PacketIOCreateFileTransfer(transferUuid, queuedFile.savePath, queuedFile.file.lastModified())).awaitCoroutine()
            while (bytes != 0) {
                when {
                    bytes > BYTES_PER_FILEPACKET -> {
                        val sendBytes = Arrays.copyOfRange(fileBytes, fileBytes.size - bytes, (fileBytes.size - bytes) + BYTES_PER_FILEPACKET)
                        bytes -= BYTES_PER_FILEPACKET
                        sendUnitQuery(PacketIOFileTransfer(transferUuid, sendBytes), 3000).awaitCoroutine()
                    }
                    else -> {
                        val sendBytes = Arrays.copyOfRange(fileBytes, fileBytes.size - bytes, fileBytes.size)
                        bytes = 0
                        sendUnitQuery(PacketIOFileTransfer(transferUuid, sendBytes), 3000).awaitCoroutine()
                    }
                }
            }
            sendUnitQuery(PacketIOFileTransferComplete(transferUuid), 3000).awaitCoroutine()
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

    override fun setAuthenticated(authenticated: Boolean) {
        super.setAuthenticated(authenticated)
        this.authenticated = authenticated
    }

    override fun isAuthenticated(): Boolean {
        return this.authenticated
    }

}