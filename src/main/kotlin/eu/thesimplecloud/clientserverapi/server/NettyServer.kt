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

import eu.thesimplecloud.clientserverapi.lib.bootstrap.AbstractCommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOConnectionWillClose
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultServerHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IServerHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.PacketEncoder
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.ClientManager
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.IClientManager
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.util.concurrent.DefaultEventExecutorGroup
import io.netty.util.concurrent.EventExecutorGroup


class NettyServer<T : IConnectedClientValue>(
        host: String,
        port: Int,
        private val connectionHandler: IConnectionHandler = DefaultConnectionHandler(),
        private val serverHandler: IServerHandler<T> = DefaultServerHandler()
) : AbstractCommunicationBootstrap(host, port), INettyServer<T> {

    private var bossGroup: NioEventLoopGroup? = null
    private var workerGroup: NioEventLoopGroup? = null
    private var eventExecutorGroup: EventExecutorGroup? = null
    private var active = false
    private var listening = false
    private val clientManager = ClientManager<T>(this)

    override fun start(): ICommunicationPromise<Unit> {
        addPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.defaultpackets")
        check(!this.active) { "Can't start server multiple times." }
        this.active = true
        val startPromise = CommunicationPromise<Unit>(enableTimeout = false)
        this.bossGroup = NioEventLoopGroup()
        this.workerGroup = NioEventLoopGroup()
        val bootstrap = ServerBootstrap()
        bootstrap.group(this.bossGroup, this.workerGroup)
        bootstrap.channel(NioServerSocketChannel::class.java)
        val instance = this
        this.eventExecutorGroup = DefaultEventExecutorGroup(20)
        bootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
            @Throws(Exception::class)
            override fun initChannel(ch: SocketChannel) {
                val pipeline = ch.pipeline()
                pipeline.addLast("frameDecoder", LengthFieldBasedFrameDecoder(Int.MAX_VALUE, 0, 4, 0, 4))
                pipeline.addLast(PacketDecoder(instance, getPacketManager()))
                pipeline.addLast("frameEncoder", LengthFieldPrepender(4))
                pipeline.addLast(PacketEncoder(instance))
                //pipeline.addLast("streamer", ChunkedWriteHandler())
                pipeline.addLast(eventExecutorGroup, "serverHandler", NettyServerHandler(instance, connectionHandler))
                pipeline.addLast(LoggingHandler(LogLevel.DEBUG))

            }
        })
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true)
        val channelFuture = bootstrap.bind(getHost(), getPort())
        channelFuture.addListener { future ->
            if (future.isSuccess) {
                this.listening = true
                serverHandler.onServerStarted(this)
                startPromise.trySuccess(Unit)
            } else {
                shutdown()
                serverHandler.onServerStartException(this, future.cause())
                startPromise.tryFailure(future.cause())
            }
        }
        return startPromise
    }

    override fun isActive(): Boolean = this.active

    override fun isListening(): Boolean = this.listening

    override fun shutdown(): ICommunicationPromise<Unit> {
        if (!listening) return CommunicationPromise.of(Unit)
        getClientManager().sendPacketToAllClients(PacketIOConnectionWillClose())
        val shutdownPromise = CommunicationPromise<Unit>()
        this.listening = false
        this.bossGroup?.shutdownGracefully()
        this.workerGroup?.shutdownGracefully()
        this.eventExecutorGroup?.shutdownGracefully()?.addListener {
            if (it.isSuccess) {
                shutdownPromise.trySuccess(Unit)
            } else {
                shutdownPromise.tryFailure(it.cause())
            }
        }
        this.serverHandler.onServerShutdown(this)
        this.active = false
        return shutdownPromise
    }

    override fun getClientManager(): IClientManager<T> {
        return this.clientManager
    }

}