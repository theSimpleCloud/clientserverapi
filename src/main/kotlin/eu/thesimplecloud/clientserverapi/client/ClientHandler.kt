package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.runBlocking
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.lang.IllegalArgumentException

class ClientHandler(val nettyClient: NettyClient, val connectionHandler: IConnectionHandler) : SimpleChannelInboundHandler<WrappedPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket) {
        if (wrappedPacket.packetData.isResponse()) {
            nettyClient.packetResponseManager.incomingPacket(wrappedPacket)
        } else {
            runBlocking {
                val packet = wrappedPacket.packet.handle(nettyClient)
                val id = when (packet) {
                    null -> -1
                    is ObjectPacket<*> -> -2
                    is JsonPacket -> -1
                    is BytePacket -> -3
                    else -> throw IllegalArgumentException("Returned packet was not null, ObjectPacket, JsonPacket or BytePacket. It looks like a custom packet.")
                }
                val responseData = PacketData(wrappedPacket.packetData.uniqueId, id)
                nettyClient.sendPacket(WrappedPacket(responseData, packet
                        ?: JsonPacket.getNewEmptyJsonPacket()))
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
