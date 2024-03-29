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
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.packet.AbstractChannelInboundHandlerImpl
import io.netty.channel.ChannelHandlerContext
import java.io.IOException

class NettyClientHandler(private val nettyClient: NettyClient, private val connectionHandler: IConnectionHandler) : AbstractChannelInboundHandlerImpl() {


    override fun getConnection(ctx: ChannelHandlerContext): AbstractNettyConnection {
        return nettyClient.getConnection() as AbstractNettyConnection
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        (nettyClient.getConnection() as ClientConnection).sendAllQueuedPackets()
        connectionHandler.onConnectionActive(nettyClient.getConnection())
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        connectionHandler.onConnectionInactive(nettyClient.getConnection())
    }

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        //ignore because the error does not have any affect
        if (cause is IOException && cause.message?.contains("reset by peer") == true) {
            return
        }

        if (!nettyClient.getConnection().wasConnectionCloseIntended())
            connectionHandler.onFailure(nettyClient.getConnection(), cause)
    }


}
