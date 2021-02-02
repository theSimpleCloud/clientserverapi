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

package eu.thesimplecloud.clientserverapi.codec

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.PacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packet.PacketEncoder
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.clientserverapi.server.NettyServerHandler
import eu.thesimplecloud.clientserverapi.server.client.clientmanager.ClientManager
import eu.thesimplecloud.clientserverapi.testobject.client.PacketIOWork
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder
import io.netty.handler.codec.LengthFieldPrepender
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 09:24
 * @author Frederick Baier
 */
class PacketEncoderTest {

    private val packetManager = PacketManager()
    private val bootstrap = mockNettyServer()
    private lateinit var channel: EmbeddedChannel

    private fun mockNettyServer(): INettyServer {
        val bootstrap = mock(INettyServer::class.java)
        val debugMessageManager = DebugMessageManager()
        debugMessageManager.enable(DebugMessage.PACKET_RECEIVED)
        debugMessageManager.enable(DebugMessage.PACKET_SENT)
        debugMessageManager.enable(DebugMessage.REGISTER_PACKET)
        `when`(bootstrap.getDebugMessageManager()).thenReturn(debugMessageManager)
        `when`(bootstrap.getClassLoaderToSearchObjectPacketsClasses()).thenReturn(ClassLoader.getSystemClassLoader())
        val clientManager = ClientManager(bootstrap)
        `when`(bootstrap.getClientManager()).thenReturn(clientManager)
        return bootstrap
    }

    @Before
    fun before() {
        channel = EmbeddedChannel(
                LengthFieldBasedFrameDecoder(Int.MAX_VALUE, 0, 4, 0, 4),
                PacketDecoder(bootstrap, packetManager),
                LengthFieldPrepender(4),
                PacketEncoder(bootstrap),
                NettyServerHandler(bootstrap, object: IConnectionHandler {
                    override fun onConnectionActive(connection: IConnection) {
                        println("active $connection")
                    }

                    override fun onFailure(connection: IConnection, ex: Throwable) {
                        throw ex
                    }

                    override fun onConnectionInactive(connection: IConnection) {
                    }

                })
        )

    }

    @Test
    fun test_packet_pipeline() {
        val future = channel.writeAndFlush(
                WrappedPacket(PacketData(UUID.randomUUID(), "PacketIOWork", false), PacketIOWork())
        )
        assertTrue(future.awaitUninterruptibly().isSuccess)
        //channel.writeInbound("test")
    }

    @Test
    fun test_invalid_packet() {
        val packet = spy(PacketIOWork())
        channel.writeInbound(
                WrappedPacket(PacketData(UUID.randomUUID(), "PacketIOWork", false), packet)
        )
        Thread.sleep(2 * 1000)
        //channel.writeInbound("test")
    }

}