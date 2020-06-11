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

package eu.thesimplecloud.clientserverapi.response

import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.WrappedResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 11:23
 * @author Frederick Baier
 */
class PacketResponseManagerTest {

    private lateinit var packetResponseManager: PacketResponseManager

    @Before
    fun before() {
        packetResponseManager = PacketResponseManager()
    }

    @Test
    fun test_register() {
        val uniqueId = UUID.randomUUID()
        assertNull(this.packetResponseManager.getResponseHandler(uniqueId))
        val responseHandler = ObjectPacketResponseHandler(String::class.java)
        val promise = CommunicationPromise<String>()
        val wrappedResponseHandler = WrappedResponseHandler(responseHandler, promise)
        this.packetResponseManager.registerResponseHandler(uniqueId, wrappedResponseHandler)

        assertNotNull(this.packetResponseManager.getResponseHandler(uniqueId))
    }

    @Test
    fun test_packet_receive() {
        val uniqueId = UUID.randomUUID()
        val responseHandler = ObjectPacketResponseHandler(String::class.java)
        val promise = CommunicationPromise<String>()
        val wrappedResponseHandler = WrappedResponseHandler(responseHandler, promise)
        this.packetResponseManager.registerResponseHandler(uniqueId, wrappedResponseHandler)

        val incomingPacket = WrappedPacket(
                PacketData(uniqueId, "ObjectPacket", true),
                ObjectPacket.getNewObjectPacketWithContent("test")
        )

        assertNotNull(this.packetResponseManager.getResponseHandler(uniqueId))
        this.packetResponseManager.incomingPacket(incomingPacket)

        assertNull(this.packetResponseManager.getResponseHandler(uniqueId))
    }

}