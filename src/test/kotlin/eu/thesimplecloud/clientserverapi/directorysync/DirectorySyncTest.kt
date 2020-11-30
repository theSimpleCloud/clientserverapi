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

import eu.thesimplecloud.clientserverapi.communication.testclasses.TestConnectedClientValue
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.jsonlib.JsonLib
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

class DirectorySyncTest {

    val factory = BootstrapFactoryGetter.setEnvironment(BootstrapFactoryGetter.ApplicationEnvironment.TEST).getFactory()

    var nettyServer = factory.createServer<TestConnectedClientValue>("127.0.0.1", 1919)
    var nettyClient = factory.createClient("127.0.0.1", 1919)

    @Test
    fun test(){
        println(nettyClient::class.java.name)
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

        val file = File("syncFolder/")
        val otherSideDir = File("syncFolderOtherSide/")
        val file1 = File(file, "json1.json")
        val file2 = File(file, "json2.json")
        file.mkdirs()
        JsonLib.empty().append("first", "test1").saveAsFile(file1)
        JsonLib.empty().append("second", "test2").saveAsFile(file2)

        val directorySync = nettyClient.getDirectorySyncManager().createDirectorySync(file, "syncFolderOtherSide/")
        val promise = directorySync.syncDirectory(nettyClient.getConnection())
        val ms = System.currentTimeMillis()
        promise.syncUninterruptibly()
        println("took ${System.currentTimeMillis() - ms}ms")
        Thread.sleep(1000)
        //test1
        Assert.assertEquals("test1", JsonLib.fromJsonFile(File(otherSideDir, "json1.json"))?.getString("first"))
        Assert.assertEquals("test2", JsonLib.fromJsonFile(File(otherSideDir, "json2.json"))?.getString("second"))

        JsonLib.empty().append("first", "test5").saveAsFile(file1)
        file2.delete()
        Thread.sleep(3600)
        //test2
        Assert.assertEquals("test5", JsonLib.fromJsonFile(File(otherSideDir, "json1.json"))?.getString("first"))
        Assert.assertFalse(File(otherSideDir, "json2.json").exists())
        Thread.sleep(300)
    }

    @After
    fun after(){
        FileUtils.deleteDirectory(File("syncFolder/"))
        FileUtils.deleteDirectory(File("syncFolderOtherSide/"))
    }







}