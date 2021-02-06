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

package eu.thesimplecloud.clientserverapi.cluster.connect

import eu.thesimplecloud.clientserverapi.cluster.auth.impl.SecretAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.factory.DefaultClusterFactory
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.testing.ClusterAssert
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 22:16
 * @author Frederick Baier
 */
class NodeConnectTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            BootstrapFactoryGetter.setEnvironment(BootstrapFactoryGetter.ApplicationEnvironment.TEST)
        }
    }


    @Test
    fun newCluster_IsNotConnected() {
        val clusterFactory = DefaultClusterFactory()
        val cluster = clusterFactory.createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1600))

        Assert.assertEquals(0, cluster.getRemoteNodes().size)
        Assert.assertEquals(1, cluster.getNodes().size)

        cluster.shutdown()
    }

    @Test
    fun afterTwoInstancesConnected_RemoteNoesIs1() {
        val clusterFactory = DefaultClusterFactory()
        val cluster = clusterFactory.createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1600))

        val otherCluster = clusterFactory.joinCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 1602),
            Address("127.0.0.1", 1600)
        )

        ClusterAssert.assertRemoteNodeCount(1, cluster)
        ClusterAssert.assertRemoteNodeCount(1, otherCluster)

        cluster.shutdown()
        otherCluster.shutdown()

    }

    @Test
    fun after4InstancesConnected_RemoteNoesIs3() {
        val clusterFactory = DefaultClusterFactory()
        val cluster = clusterFactory.createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1600))

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

        ClusterAssert.assertRemoteNodeCount(3, cluster)
        ClusterAssert.assertRemoteNodeCount(3, otherCluster)
        ClusterAssert.assertRemoteNodeCount(3, otherCluster2)
        ClusterAssert.assertRemoteNodeCount(3, otherCluster3)

        cluster.shutdown()
        otherCluster.shutdown()
        otherCluster2.shutdown()
        otherCluster3.shutdown()

    }
}