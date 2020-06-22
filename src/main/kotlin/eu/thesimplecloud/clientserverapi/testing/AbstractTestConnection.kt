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

package eu.thesimplecloud.clientserverapi.testing

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packetresponse.IPacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.WrappedResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.io.File
import java.io.IOException
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 12.06.2020
 * Time: 09:20
 * @author Frederick Baier
 */
abstract class AbstractTestConnection(val packetResponseManager: IPacketResponseManager) : IConnection {


    @Volatile
    var otherSideConnection: IConnection? = null

    override fun isOpen(): Boolean {
        return this.otherSideConnection != null
    }

    override fun wasConnectionCloseIntended(): Boolean {
        return false
    }

    override fun sendFile(file: File, savePath: String, timeout: Long): ICommunicationPromise<Unit> {
        file.copyTo(File(savePath), true)
        return CommunicationPromise.of(Unit)
    }

    override fun closeConnection(): ICommunicationPromise<Unit> {
        this.otherSideConnection = null
        return CommunicationPromise.of(Unit)
    }

    override fun getHost(): String {
        return "127.0.0.1"
    }

    override fun <T : Any> sendQuery(packet: IPacket, expectedResponseClass: Class<T>, timeout: Long): ICommunicationPromise<T> {
        if (!isOpen()) throw IOException("Connection is closed")
        val packetResponseHandler = ObjectPacketResponseHandler(expectedResponseClass)
        val uniqueId = UUID.randomUUID()
        val packetPromise = CommunicationPromise<T>(timeout)
        this.packetResponseManager.registerResponseHandler(uniqueId, WrappedResponseHandler(packetResponseHandler, packetPromise))
        val packetToSend = WrappedPacket(PacketData(uniqueId, packet::class.java.simpleName, false), packet)
        this.sendPacket(packetToSend, packetPromise)
        return packetPromise
    }

    fun sendPacket(wrappedPacket: WrappedPacket, promise: ICommunicationPromise<out Any>) {
        if (!isOpen()) {
            val exception = IOException("Connection is closed. Packet to send was ${wrappedPacket.packetData.sentPacketName}.")
            promise.tryFailure(exception)
            throw exception
        }
        (this.otherSideConnection as AbstractTestConnection).packetReceived(wrappedPacket)
    }

    /**
     * Handles the specified [wrappedPacket] and returns a response to the packet
     */
    abstract fun packetReceived(wrappedPacket: WrappedPacket)
}