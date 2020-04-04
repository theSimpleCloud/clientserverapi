package eu.thesimplecloud.clientserverapi.server

import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.handler.AbstractChannelInboundHandlerImpl
import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ServerHandler(private val nettyServer: NettyServer<*>, private val connectionHandler: IConnectionHandler) : AbstractChannelInboundHandlerImpl() {


    override fun getConnection(ctx: ChannelHandlerContext): AbstractConnection = nettyServer.clientManager.getClient(ctx)!! as AbstractConnection


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
        //super.exceptionCaught(ctx, cause)
        nettyServer.clientManager.getClient(ctx)?.let { connectionHandler.onFailure(it, cause) }
    }


}
