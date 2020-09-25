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

package eu.thesimplecloud.clientserverapi.server

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractNettyConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.packet.AbstractChannelInboundHandlerImpl
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.ClientManager
import io.netty.channel.ChannelHandlerContext

class NettyServerHandler(nettyServer: INettyServer<*>, private val connectionHandler: IConnectionHandler) : AbstractChannelInboundHandlerImpl() {

    private val clientManager = nettyServer.getClientManager() as ClientManager<*>

    override fun getConnection(ctx: ChannelHandlerContext): AbstractNettyConnection = this.clientManager.getClient(ctx)!! as AbstractNettyConnection


    override fun channelActive(ctx: ChannelHandlerContext) {
        this.clientManager.addClient(ctx).let { connectionHandler.onConnectionActive(it) }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        this.clientManager.removeClient(ctx)?.let {
            connectionHandler.onConnectionInactive(it)
        }
    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        //super.exceptionCaught(ctx, cause)
        this.clientManager.getClient(ctx)?.let {
            if (!it.wasConnectionCloseIntended())
                connectionHandler.onFailure(it, cause)
        }
    }


}
