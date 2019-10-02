package eu.thesimplecloud.clientserverapi.server

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.runBlocking
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.lang.IllegalArgumentException

class ServerHandler(private val nettyServer: NettyServer<*>, private val connectionHandler: IConnectionHandler) : SimpleChannelInboundHandler<WrappedPacket>() {

    override fun channelRead0(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket) {
        nettyServer.clientManager.getClient(ctx)?.let {
            it as AbstractConnection
            if (wrappedPacket.packetData.isResponse()) {
                nettyServer.packetResponseManager.incomingPacket(wrappedPacket)
            } else {
                runBlocking {
                    val packet = wrappedPacket.packet.handle(it)
                    val id = when (packet){
                        null -> -1
                        is ObjectPacket<*> -> -2
                        is JsonPacket -> -1
                        is BytePacket -> -3
                        else -> throw IllegalArgumentException("Returned packet was not null, ObjectPacket, JsonPacket or BytePacket. It looks like a custom packet.")
                    }
                    val responseData = PacketData(wrappedPacket.packetData.uniqueId, id)
                    it.sendPacket(WrappedPacket(responseData, packet
                            ?: JsonPacket.getNewEmptyJsonPacket()))
                }
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        nettyServer.clientManager.addClient(ctx).let { connectionHandler.onConnectionActive(it) }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        nettyServer.clientManager.removeClient(ctx)?.let { connectionHandler.onConnectionInactive(it) }
    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        nettyServer.clientManager.getClient(ctx)?.let { connectionHandler.onFailure(it, cause) }
    }


}
