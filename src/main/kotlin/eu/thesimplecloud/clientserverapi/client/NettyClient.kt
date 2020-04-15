package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.client.packets.PacketOutGetPacketId
import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.debug.IDebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.DirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.ITransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.TransferFileManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.IDirectorySyncManager
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.PacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQueryAsync
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.resource.ResourceFinder
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.timeout.IdleStateHandler
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.concurrent.thread

class NettyClient(private val host: String, val port: Int, private val connectionHandler: IConnectionHandler = DefaultConnectionHandler()) : AbstractConnection(PacketManager(), PacketResponseManager()), INettyClient {

    private val debugMessageManager = DebugMessageManager()
    private var channel: Channel? = null
    private var workerGroup: NioEventLoopGroup? = null

    private var lastStartPromise: ICommunicationPromise<Unit> = CommunicationPromise(enableTimeout = false)
    private var packetPackages: MutableList<String> = CopyOnWriteArrayList()
    private var running = false
    private val transferFileManager = TransferFileManager()
    private val directoryWatchManager = DirectoryWatchManager()
    private val directorySyncManager = DirectorySyncManager(directoryWatchManager)
    private val classLoaders = CopyOnWriteArrayList<ClassLoader>()
    private var packetClassConverter: (Class<out IPacket>) -> Class<out IPacket> = { it }

    init {
        reloadPackets()
    }

    override fun start(): ICommunicationPromise<Unit> {
        addPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.defaultpackets")
        check(!this.running) { "Can't start client multiple times." }
        this.running = true
        this.lastStartPromise = CommunicationPromise(enableTimeout = false)
        this.directoryWatchManager.startThread()
        val instance = this
        this.workerGroup = NioEventLoopGroup()
        val bootstrap = Bootstrap()
        bootstrap.group(this.workerGroup)
        bootstrap.channel(NioSocketChannel::class.java)
        bootstrap.handler(object : ChannelInitializer<Channel>() {
            override fun initChannel(channel: Channel) {
                val pipeline = channel.pipeline()
                pipeline.addLast("idleStateHandler", IdleStateHandler(0, 0, 5)) // add with name
                pipeline.addLast("frameDecoder", LengthFieldBasedFrameDecoder(1048576000, 0, 4, 0, 4))
                pipeline.addLast(PacketDecoder(instance, packetManager, packetResponseManager)) // add without name, name auto generated
                pipeline.addLast("frameEncoder", LengthFieldPrepender(4))
                pipeline.addLast(PacketEncoder(instance)) // add without name, name auto generated
                pipeline.addLast(ClientHandler(instance, connectionHandler))
                pipeline.addLast(LoggingHandler(LogLevel.DEBUG))
            }

        })
        this.channel = bootstrap.connect(host, port).addListener { future ->
            if (future.isSuccess) {
                thread { registerPacketsByPackage(packetPackages.toTypedArray()) }
            } else {
                this.lastStartPromise.tryFailure(future.cause())
                this.shutdown()
            }
        }.channel()
        return lastStartPromise
    }

    fun reloadPackets() {
        this.packetManager.clearPackets()
        this.packetManager.registerPacket(0, PacketOutGetPacketId::class.java)
    }

    override fun shutdown(): ICommunicationPromise<Unit> {
        val shutdownPromise = CommunicationPromise<Unit>()
        if (this.channel == null) shutdownPromise.trySuccess(Unit)
        this.directoryWatchManager.stopThread()
        this.workerGroup?.shutdownGracefully()
        this.running = false
        this.channel?.closeFuture()?.addListener {
            this.reloadPackets()
            if (it.isSuccess) {
                shutdownPromise.trySuccess(Unit)
            } else {
                shutdownPromise.tryFailure(it.cause())
            }
        }
        return shutdownPromise
    }

    override fun getChannel(): Channel? = this.channel

    override fun isActive(): Boolean = this.running

    override fun getTransferFileManager(): ITransferFileManager = this.transferFileManager

    override fun getDirectorySyncManager(): IDirectorySyncManager = this.directorySyncManager

    override fun getDirectoryWatchManager(): IDirectoryWatchManager = this.directoryWatchManager

    override fun getDebugMessageManager(): IDebugMessageManager = this.debugMessageManager

    override fun setPacketClassConverter(function: (Class<out IPacket>) -> Class<out IPacket>) {
        this.packetClassConverter = function
    }

    override fun getCommunicationBootstrap() = this

    override fun getHost(): String = host


    override fun addPacketsByPackage(vararg packages: String) {
        if (this.running) {
            this.registerPacketsByPackage(packages as Array<String>)
        } else {
            this.packetPackages.addAll(packages)
        }
    }

    override fun addClassLoader(vararg classLoaders: ClassLoader) {
        this.classLoaders.addAll(classLoaders)
    }

    private fun registerPacketsByPackage(array: Array<String>) {
        if (this.classLoaders.isEmpty()) this.classLoaders.add(ResourceFinder.getSystemClassLoader())
        val allPacketClasses = ArrayList<Class<out IPacket>>()
        array.forEach { packageName ->
            val reflections = Reflections(packageName, SubTypesScanner(), this.classLoaders.toTypedArray())
            val packageClasses = reflections.getSubTypesOf(IPacket::class.java)
                    .union(reflections.getSubTypesOf(JsonPacket::class.java))
                    .union(reflections.getSubTypesOf(ObjectPacket::class.java))
                    .union(reflections.getSubTypesOf(BytePacket::class.java))
                    .filter { it != JsonPacket::class.java && it != BytePacket::class.java && it != ObjectPacket::class.java }

            allPacketClasses.addAll(packageClasses)
        }
        val packetPromise = sendQueryAsync<Array<Int>>(PacketOutGetPacketId(ArrayList(allPacketClasses.map { it.simpleName })), timeout = 1000)
        packetPromise.addResultListener { list ->
            list.forEachIndexed { index, id ->
                val packetClass = allPacketClasses[index]
                if (id != -1) {
                    if (this.getDebugMessageManager().isActive(DebugMessage.REGISTER_PACKET)) println("Registered packet: ${packetClass.simpleName} id: $id")
                    this.packetManager.registerPacket(id, this.packetClassConverter(packetClass))
                } else {
                    throw PacketException("Can't register packet ${packetClass.simpleName}: No Server-Packet found")
                }
            }
            this.lastStartPromise.trySuccess(Unit)
        }.addFailureListener {
            throw it
        }
    }

}