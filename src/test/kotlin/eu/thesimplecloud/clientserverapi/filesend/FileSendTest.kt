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

package eu.thesimplecloud.clientserverapi.filesend

class FileSendTest {

    /*

    var nettyServer = NettyServer<TestConnectedClientValue>("127.0.0.1", 1920)
    var nettyClient = NettyClient("127.0.0.1", 1920)


    @Test(timeout = 6000)
    fun test() {
        nettyServer.registerPacketsByPackage("eu.thesimplecloud.clientserverapi.lib.filetransfer.packets")
        GlobalScope.launch {
            nettyServer.start()
        }
        while (!nettyServer.isListening()) {
            Thread.sleep(10)
        }
        nettyClient.addPacketsPackage("eu.thesimplecloud.clientserverapi.lib.filetransfer.packets")
        GlobalScope.launch {
            nettyClient.start()
        }

        while (!nettyClient.isOpen()) {
            Thread.sleep(10)
        }
        JsonData().append("test", "Hallo123").saveAsFile(File("test.json"))
        val promise = nettyClient.sendFile(File("test.json"), "test123.json")
        promise.syncUninterruptibly()
        val file = File("test123.json")
        Assert.assertTrue(file.exists())
        Assert.assertEquals("Hallo123", JsonData.fromJsonFile(file).getString("test"))
    }

     */


}