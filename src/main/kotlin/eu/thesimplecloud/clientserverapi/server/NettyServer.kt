package eu.thesimplecloud.clientserverapi.server

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.util.concurrent.DefaultEventExecutorGroup
import io.netty.util.concurrent.EventExecutorGroup
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultServerHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IServerHandler
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.PacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.IPacketManager
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.ClientManager
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import eu.thesimplecloud.clientserverapi.server.packets.PacketInGetPacketId
import org.reflections.Reflections
import java.lang.IllegalStateException


class NettyServer<T: IConnectedClientValue>(private val host: String, private val port: Int, private val connectionHandler: IConnectionHandler = DefaultConnectionHandler(), private val serverHandler: IServerHandler<T> = DefaultServerHandler()) : INettyServer<T> {


    private var bossGroup: NioEventLoopGroup? = null
    private var workerGroup: NioEventLoopGroup? = null
    private var eventExecutorGroup: EventExecutorGroup? = null
    val packetManager = PacketManager()
    val packetResponseManager = PacketResponseManager()
    private var active = false
    val clientManager = ClientManager(this)
    private var started = false

    init {
        packetManager.registerPacket(0, PacketInGetPacketId::class.java)
    }

    override fun start(){
        if (started) throw IllegalStateException("Can't start server multiple times.")
        started = true
        bossGroup = NioEventLoopGroup()
        workerGroup = NioEventLoopGroup()
        val bootstrap = ServerBootstrap()
        bootstrap.group(bossGroup, workerGroup)
        bootstrap.channel(NioServerSocketChannel::class.java)
        val instance = this
        eventExecutorGroup = DefaultEventExecutorGroup(20)
        bootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: SocketChannel) {
                val pipeline = ch.pipeline()
                pipeline.addLast("frameDecoder", LengthFieldBasedFrameDecoder(Int.MAX_VALUE, 0, 4, 0, 4))
                pipeline.addLast(PacketDecoder(packetManager, packetResponseManager))
                pipeline.addLast("frameEncoder", LengthFieldPrepender(4))
                pipeline.addLast(PacketEncoder())

                //pipeline.addLast("streamer", ChunkedWriteHandler())
                pipeline.addLast(eventExecutorGroup, "serverHandler", ServerHandler(instance, connectionHandler))

            }
        })
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)
        bootstrap.bind(host, port).addListener { future ->
            if (future.isSuccess) {
                active = true
                serverHandler.onServerStarted(this)
            } else {
                shutdown()
                serverHandler.onServerStartException(this, future.cause())
            }
        }.sync().channel().closeFuture().syncUninterruptibly()
    }

    override fun registerPacketsByPackage(vararg packages: String){
        packages.forEach {packageName ->
            val reflections = Reflections(packageName)
            val allClasses = reflections.getSubTypesOf(IPacket::class.java).filter { it != JsonPacket::class.java && it != BytePacket::class.java }
            allClasses.forEach { packet ->
                packetManager.registerPacket(packetManager.getUnusedId(), packet)
            }
        }
    }

    override fun getClientManager(): IClientManager<T> = clientManager

    override fun getPacketManager(): IPacketManager = packetManager

    override fun isActive(): Boolean = active

    override fun shutdown() {
        bossGroup?.shutdownGracefully()
        workerGroup?.shutdownGracefully()
        eventExecutorGroup?.shutdownGracefully()
        serverHandler.onServerShutdown(this)
    }


}