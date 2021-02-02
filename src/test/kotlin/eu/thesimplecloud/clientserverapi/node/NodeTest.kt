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

package eu.thesimplecloud.clientserverapi.node

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.auth.impl.SecretAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.factory.DefaultClusterFactory
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 22:16
 * @author Frederick Baier
 */
class NodeTest {

    @Test
    fun test() {
        BootstrapFactoryGetter.setEnvironment(BootstrapFactoryGetter.ApplicationEnvironment.NORMAL)
        val clusterFactory = DefaultClusterFactory()
        val cluster = clusterFactory.createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1600))
        val list = cluster.getClusterListManager().getClusterListByNameOrCreate<TestListObj>("test", Array<TestListObj>::class.java)
        list.addElement(TestListObj("ddd", 4))
        list.addElement(TestListObj("2222", 2))
        val otherCluster = clusterFactory.joinCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 1601),
            Address("127.0.0.1", 1600)
        )

        val otherCluster2 = clusterFactory.joinCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 1602),
            Address("127.0.0.1", 1600)
        )

        val otherCluster3 = clusterFactory.joinCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 1603),
            Address("127.0.0.1", 1600)
        )

        val otherCluster4 = clusterFactory.joinCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 1604),
            Address("127.0.0.1", 1600)
        )



        printClusterInfo(cluster)
        printClusterInfo(otherCluster)
        printClusterInfo(otherCluster2)
        printClusterInfo(otherCluster3)
        printClusterInfo(otherCluster4)
        list.removeElement("ddd")
        printClusterInfo(cluster)
        printClusterInfo(otherCluster)
        printClusterInfo(otherCluster2)
        printClusterInfo(otherCluster3)
        printClusterInfo(otherCluster4)
        val test = TestListObj("ddda333", 54)
        list.addElement(test)
        printClusterInfo(cluster)
        printClusterInfo(otherCluster)
        printClusterInfo(otherCluster2)
        printClusterInfo(otherCluster3)
        printClusterInfo(otherCluster4)
        test.number = 4343
        list.updateElement(test)
        printClusterInfo(cluster)
        printClusterInfo(otherCluster)
        printClusterInfo(otherCluster2)
        printClusterInfo(otherCluster3)
    }

    private fun printClusterInfo(cluster: ICluster) {
        println("me: " + cluster.getSelfNode().getServerAddress())
        println(cluster.getClusterListManager().getClusterListByName<TestListObj>("test")!!.getAllElements().map { it.getIdentifier() })
        println(cluster.getClusterListManager().getClusterListByName<TestListObj>("test")!!.getAllElements().map { it.number })
    }
}