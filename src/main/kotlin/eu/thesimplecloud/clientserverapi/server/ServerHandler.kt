package eu.thesimplecloud.clientserverapi.server

import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServerHandler(private val nettyServer: NettyServer<*>, private val connectionHandler: IConnectionHandler) : SimpleChannelInboundHandler<WrappedPacket>() {


    override fun channelRead0(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket) {
        nettyServer.clientManager.getClient(ctx)?.let {
            it as AbstractConnection
            if (wrappedPacket.packetData.isResponse()) {
                nettyServer.packetResponseManager.incomingPacket(wrappedPacket)
            } else {
                GlobalScope.launch {
                    val responseResult = runCatching {
                        wrappedPacket.packet.handle(it)
                    }
                    val packetToSend = when {
                        responseResult.isFailure -> PacketOutErrorResponse(responseResult.exceptionOrNull()!!)
                        else -> ObjectPacket.getNewObjectPacketWithContent(responseResult.getOrNull())
                    }
                    val responseData = PacketData(wrappedPacket.packetData.uniqueId, -1, packetToSend::class.java.simpleName)
                    it.sendPacket(WrappedPacket(responseData, packetToSend))
                }
            }
        }
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        nettyServer.clientManager.addClient(ctx).let { connectionHandler.onConnectionActive(it) }
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        nettyServer.clientManager.removeClient(ctx)?.let {
            connectionHandler.onConnectionInactive(it)
            val directorySyncManager = nettyServer.getDirectorySyncManager()
            directorySyncManager as DirectorySyncManager
            directorySyncManager.removeFromDirectorySyncs(it)
        }
    }


    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        nettyServer.clientManager.getClient(ctx)?.let { connectionHandler.onFailure(it, cause) }
    }


}
