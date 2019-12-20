package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClientHandler(val nettyClient: NettyClient, val connectionHandler: IConnectionHandler) : SimpleChannelInboundHandler<WrappedPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket) {
        if (wrappedPacket.packetData.isResponse()) {
            nettyClient.packetResponseManager.incomingPacket(wrappedPacket)
        } else {
            GlobalScope.launch {
                val responseResult = runCatching {
                    wrappedPacket.packet.handle(nettyClient)
                }
                val packetToSend = when {
                    responseResult.isFailure -> PacketOutErrorResponse(responseResult.exceptionOrNull()!!)
                    else -> ObjectPacket.getNewObjectPacketWithContent(responseResult.getOrNull())
                }
                val responseData = PacketData(wrappedPacket.packetData.uniqueId, -1, packetToSend::class.java.simpleName)
                nettyClient.sendPacket(WrappedPacket(responseData, packetToSend))
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        connectionHandler.onConnectionActive(nettyClient)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        connectionHandler.onConnectionInactive(nettyClient)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        connectionHandler.onFailure(nettyClient, cause)
    }


}
