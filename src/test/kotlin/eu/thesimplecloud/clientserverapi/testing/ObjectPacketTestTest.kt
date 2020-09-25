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

package eu.thesimplecloud.clientserverapi.testing

import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.testobject.ITestObj
import eu.thesimplecloud.clientserverapi.testobject.client.PacketIOWork
import eu.thesimplecloud.clientserverapi.testobject.client.PacketOutMessage
import org.junit.Test
import kotlin.concurrent.thread

/**
 * Created by IntelliJ IDEA.
 * Date: 25.09.2020
 * Time: 18:36
 * @author Frederick Baier
 */
class ObjectPacketTestTest {


    @Test
    fun test() {
        BootstrapFactoryGetter.setEnvironment(BootstrapFactoryGetter.ApplicationEnvironment.TEST)
        val factory = BootstrapFactoryGetter.getFactory()
        val nettyServer = factory.createServer<TestConnectedClientValue>("127.0.0.1", 1921)


        nettyServer.getPacketManager().registerPacket(PacketIOWork::class.java)
        nettyServer.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject.server")
        thread {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        val nettyClient = factory.createClient("127.0.0.1", 1921)
        nettyClient.addPacketsByPackage("eu.thesimplecloud.clientserverapi.testobject.client")
        thread {
            nettyClient.start()
        }

        //nettyClient.sendQuery<JsonData>(PacketIOMessage("hi"))
        //        .thenNonNull { println(it.getString("test")) }
        //        .addFailureListener { println(it.message) }
        Thread.sleep(1000)
        val time = System.currentTimeMillis()
        nettyClient.getConnection().sendUnitQuery(PacketOutMessage("hi"), 1500).addResultListener { println("result: $it time: ${System.currentTimeMillis() - time}") }
                .addFailureListener { println("failure ${it::class.java.simpleName} ${it.message}") }
        nettyClient.getConnection().sendQuery<ITestObj>(PacketIOWork(), 1500).addResultListener { println("result: $it time: ${System.currentTimeMillis() - time}") }
                .addFailureListener { println("failure ${it::class.java.simpleName} ${it.message}") }
        nettyServer.getDebugMessageManager().enable(DebugMessage.PACKET_RECEIVED)
        Thread.sleep(1000)
        nettyClient.shutdown()
    }

}