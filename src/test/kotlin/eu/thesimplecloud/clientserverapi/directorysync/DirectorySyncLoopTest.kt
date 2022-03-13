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

package eu.thesimplecloud.clientserverapi.directorysync

import eu.thesimplecloud.clientserverapi.client.NettyClient
import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.server.NettyServer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.junit.Test
import java.io.File

class DirectorySyncLoopTest {

    /*

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1919)
    var nettyClient = NettyClient("127.0.0.1", 1919)

    @Test
    fun test() {
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        GlobalScope.launch {
            nettyClient.start()
        }

        while (!nettyClient.getConnection().isOpen()) {
            Thread.sleep(10)
        }
        Thread.sleep(1000)
        val dir = File("templates/")
        dir.mkdirs()
        nettyServer.getDirectorySyncManager().setTmpZipDirectory(File("storage/zippedTemplates/"))
        val directorySync = nettyServer.getDirectorySyncManager().createDirectorySync(dir, "templatesOtherSide/")
        val clientOnServerSide = this.nettyServer.getClientManager().getClients().firstOrNull()!!
        val promise = directorySync.syncDirectory(clientOnServerSide)
        promise.awaitUninterruptibly()
        promise.cause()?.let { throw it }
        println("finished")

        while (true) {
            Thread.sleep(200)
        }

        Thread.sleep(3000)
    }

    */



}