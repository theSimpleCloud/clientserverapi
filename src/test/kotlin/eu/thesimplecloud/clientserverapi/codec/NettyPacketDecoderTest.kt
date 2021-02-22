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

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessageManager
import eu.thesimplecloud.clientserverapi.lib.packet.codec.netty.NettyPacketDecoder
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 09:17
 * @author Frederick Baier
 */
class NettyPacketDecoderTest {

    private val packetManager = PacketManager()
    private val bootstrap = mockCommunicationBootstrap()
    private var packetDecoder = NettyPacketDecoder(bootstrap)

    private fun mockCommunicationBootstrap(): ICommunicationBootstrap {
        val bootstrap = mock(ICommunicationBootstrap::class.java)
        `when`(bootstrap.getDebugMessageManager()).thenReturn(DebugMessageManager())
        `when`(bootstrap.getClassLoaderToSearchObjectPacketsClasses()).thenReturn(ClassLoader.getSystemClassLoader())
        return bootstrap
    }

    @Before
    fun before() {
        this.packetDecoder = NettyPacketDecoder(bootstrap)
    }

    @Test
    fun deserialize_test() {
        //this.packetDecoder.decode
    }


}