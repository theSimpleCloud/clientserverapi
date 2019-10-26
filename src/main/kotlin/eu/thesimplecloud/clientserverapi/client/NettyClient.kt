package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.client.packets.PacketOutGetPacketId
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.TransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.timeout.IdleStateHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.PacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import org.reflections.Reflections
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.collections.ArrayList

class NettyClient(private val host: String, val port: Int, private val connectionHandler: IConnectionHandler = DefaultConnectionHandler()) : AbstractConnection(PacketManager(), PacketResponseManager()), INettyClient {

    private var channel: Channel? = null
    private var workerGroup: NioEventLoopGroup? = null

    private var packetIdsSynchronized: Boolean = false
    private var packetIdsSyncPromise: ICommunicationPromise<Unit> = newPromise()
    private var packetPackages: MutableList<String> = ArrayList()
    private var running = false
    private val transferFileManager = TransferFileManager()
    private val directorySyncManager = DirectorySyncManager()

    init {
        packetManager.registerPacket(0, PacketOutGetPacketId::class.java)
        addPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.filetransfer.packets")
    }

    override fun start() {
        check(!this.running) { "Can't start server multiple times." }
        running = true
        directorySyncManager.startSyncThread()
        val instance = this
        workerGroup = NioEventLoopGroup()
        val bootstrap = Bootstrap()
        bootstrap.group(workerGroup)
        bootstrap.channel(NioSocketChannel::class.java)
        bootstrap.handler(object : ChannelInitializer<Channel>() {
            override fun initChannel(channel: Channel) {
                val pipeline = channel.pipeline()
                pipeline.addLast("idleStateHandler", IdleStateHandler(0, 0, 5)) // add with name
                pipeline.addLast("frameDecoder", LengthFieldBasedFrameDecoder(1048576000, 0, 4, 0, 4))
                pipeline.addLast(PacketDecoder(packetManager, packetResponseManager)) // add without name, name auto generated
                pipeline.addLast("frameEncoder", LengthFieldPrepender(4))
                pipeline.addLast(PacketEncoder()) // add without name, name auto generated

                pipeline.addLast(ClientHandler(instance, connectionHandler))
            }

        })
        channel = bootstrap.connect(host, port).addListener { future ->
            if (future.isSuccess) {
                GlobalScope.launch { registerPacketsByPackage() }
            } else {
                this.shutdown()
            }
        }.channel()
    }

    override fun shutdown() {
        workerGroup?.shutdownGracefully()
        running = false
    }

    override fun getChannel(): Channel? = channel

    override fun isActive(): Boolean = running

    override fun getTransferFileManager(): ITransferFileManager = this.transferFileManager

    override fun getDirectorySyncManager(): IDirectorySyncManager = this.directorySyncManager

    override fun getCommunicationBootstrap() = this

    override fun getHost(): String = host


    override fun addPacketsByPackage(vararg packages: String) {
        check(!running) { "Can't register packets when client is started." }
        packetPackages.addAll(packages)
    }

    fun registerPacketsByPackage() {
        packetPackages.forEach { packageName ->
            val reflections = Reflections(packageName)
            val allClasses = reflections.getSubTypesOf(IPacket::class.java)
                    .union(reflections.getSubTypesOf(JsonPacket::class.java))
                    .union(reflections.getSubTypesOf(ObjectPacket::class.java))
                    .union(reflections.getSubTypesOf(BytePacket::class.java))
                    .filter { it != JsonPacket::class.java && it != BytePacket::class.java && it != ObjectPacket::class.java }
            val promises = ArrayList<ICommunicationPromise<Int>>()
            allClasses.forEach { packetClass ->
                val packetName = packetClass.simpleName
                val packetPromise = sendQuery(PacketOutGetPacketId(packetName), ObjectPacketResponseHandler(Int::class.java))
                promises.add(packetPromise)
                packetPromise.addResultListener { id ->
                    if (id != null) {
                        packetManager.registerPacket(id, packetClass)
                    } else {
                        throw PacketException("Can't register packet ${packetClass.simpleName} : No Server-Packet found")
                    }
                }
            }
            GlobalScope.launch {
                promises.forEach { it.syncUninterruptibly() }
                packetIdsSynchronized = true
                packetIdsSyncPromise.trySuccess(Unit)
            }
        }
    }

    override fun getPacketIdsSyncPromise(): ICommunicationPromise<Unit> = this.packetIdsSyncPromise

}