package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.handler.AbstractChannelInboundHandlerImpl
import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClientHandler(private val nettyClient: NettyClient, private val connectionHandler: IConnectionHandler) : AbstractChannelInboundHandlerImpl() {



    override fun getConnection(ctx: ChannelHandlerContext): AbstractConnection = nettyClient

    override fun channelActive(ctx: ChannelHandlerContext) {
        connectionHandler.onConnectionActive(nettyClient)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        connectionHandler.onConnectionInactive(nettyClient)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        //super.exceptionCaught(ctx, cause)
        connectionHandler.onFailure(nettyClient, cause)
    }


}
