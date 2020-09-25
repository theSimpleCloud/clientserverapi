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

package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractNettyConnection
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.channel.Channel
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 27.06.2020
 * Time: 13:49
 * @author Frederick Baier
 */
class ClientConnection(
        private val bootstrap: INettyClient
) : AbstractNettyConnection() {

    private var channel: Channel? = null

    private val queuedPackets = CopyOnWriteArrayList<Pair<WrappedPacket, ICommunicationPromise<*>>>()

    fun setChannel(channel: Channel) {
        this.channel = channel
    }

    override fun getChannel(): Channel? {
        return this.channel
    }

    override fun getCommunicationBootstrap(): INettyClient {
        return bootstrap
    }

    override fun sendPacket(wrappedPacket: WrappedPacket, promise: ICommunicationPromise<out Any>) {
        if (!isOpen())
            this.queuedPackets.add(wrappedPacket to promise)
        else
            super.sendPacket(wrappedPacket, promise)
    }

    fun sendAllQueuedPackets() {
        this.queuedPackets.forEach { sendPacket(it.first, it.second) }
        this.queuedPackets.clear()
    }


}