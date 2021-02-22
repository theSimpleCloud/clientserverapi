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
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Address
import io.netty.channel.Channel
import java.io.IOException
import java.net.InetSocketAddress

abstract class AbstractNettyConnection() : AbstractConnection() {

    @Synchronized
    override fun sendPacket(wrappedPacket: WrappedPacket, promise: ICommunicationPromise<Any>) {
        if (!isOpen()) {
            val exception = IOException("Connection is closed. Packet to send was ${wrappedPacket.packetHeader.sentPacketName}.")
            promise.tryFailure(exception)
            return
        }
        val channel = getChannel()!!
        channel.eventLoop().execute {
            channel.writeAndFlush(wrappedPacket).addListener {
                if (!it.isSuccess)
                    promise.tryFailure(it.cause())
            }
        }
    }

    /**
     * Returns the channel of this connection or null if the connection is not connected.
     * @return the channel or null if the connection is not connected.
     */
    abstract fun getChannel(): Channel?

    override fun isOpen(): Boolean {
        return getChannel() != null && getChannel()?.isActive ?: false
    }

    override fun closeConnection(): ICommunicationPromise<Unit> {
        if (getChannel() == null || !getChannel()!!.isOpen) throw IllegalStateException("Connection already closed.")
        val connectionPromise = CommunicationPromise<Unit>(2000)
        getChannel()?.close()?.addListener { connectionPromise.trySuccess(Unit) }
        return connectionPromise
    }

    override fun getAddress(): Address {
        if (!isOpen()) throw IllegalStateException("Connection is not open")
        val address = (getChannel()!!.remoteAddress() as InetSocketAddress)
        return Address(address.address.hostAddress, address.port)
    }

}