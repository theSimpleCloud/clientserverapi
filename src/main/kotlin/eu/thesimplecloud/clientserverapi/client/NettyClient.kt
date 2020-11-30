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

package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.bootstrap.AbstractCommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.codec.ProtobufVarint32FrameDecoder
import eu.thesimplecloud.clientserverapi.lib.codec.ProtobufVarint32LengthFieldPrepender
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.PacketIOConnectionWillClose
import eu.thesimplecloud.clientserverapi.lib.handler.DefaultConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.PacketEncoder
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.logging.LogLevel
import io.netty.handler.logging.LoggingHandler
import io.netty.handler.timeout.IdleStateHandler

class NettyClient(
        host: String,
        port: Int,
        connectionHandler: IConnectionHandler = DefaultConnectionHandler()
) : AbstractCommunicationBootstrap(host, port, connectionHandler), INettyClient {

    private var workerGroup: NioEventLoopGroup? = null
    private val clientConnection = ClientConnection(this)

    private var running = false

    override fun start(): ICommunicationPromise<Unit> {
        check(!this.running) { "Cannot start client while it is running" }
        this.running = true
        addPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.defaultpackets")
        val startedPromise = CommunicationPromise<Unit>(enableTimeout = false)
        val instance = this
        this.workerGroup = NioEventLoopGroup()
        val bootstrap = Bootstrap()
        bootstrap.group(this.workerGroup)
        bootstrap.channel(NioSocketChannel::class.java)
        bootstrap.handler(object : ChannelInitializer<Channel>() {
            override fun initChannel(channel: Channel) {
                val pipeline = channel.pipeline()
                pipeline.addLast("idleStateHandler", IdleStateHandler(0, 0, 5)) // add with name
                pipeline.addLast("frameDecoder", ProtobufVarint32FrameDecoder())
                pipeline.addLast(PacketDecoder(instance, getPacketManager())) // add without name, name auto generated
                pipeline.addLast("frameEncoder", ProtobufVarint32LengthFieldPrepender())
                pipeline.addLast(PacketEncoder(instance)) // add without name, name auto generated
                pipeline.addLast(NettyClientHandler(instance, getConnectionHandler()))
                pipeline.addLast(LoggingHandler(LogLevel.DEBUG))
            }

        })
        val channel = bootstrap.connect(getHost(), getPort()).addListener { future ->
            if (future.isSuccess) {
                startedPromise.trySuccess(Unit)
            } else {
                startedPromise.tryFailure(future.cause())
                this.shutdown()
            }
        }.channel()
        this.clientConnection.setChannel(channel)
        return startedPromise
    }

    override fun shutdown(): ICommunicationPromise<Unit> {
        val shutdownPromise = CommunicationPromise<Unit>()
        if (this.clientConnection.getChannel() == null) shutdownPromise.trySuccess(Unit)
        this.clientConnection.sendUnitQuery(PacketIOConnectionWillClose())
        this.workerGroup?.shutdownGracefully()
        this.running = false
        this.clientConnection.getChannel()?.closeFuture()?.addListener {
            if (it.isSuccess) {
                shutdownPromise.trySuccess(Unit)
            } else {
                shutdownPromise.tryFailure(it.cause())
            }
        }
        return shutdownPromise
    }

    override fun isActive(): Boolean {
        return this.running
    }

    override fun getConnection(): IConnection {
        return this.clientConnection
    }


}