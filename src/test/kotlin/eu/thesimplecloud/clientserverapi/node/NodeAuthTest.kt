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
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.PacketIOAuthentication
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import org.junit.After
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 11:51
 * @author Frederick Baier
 */
class NodeAuthTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            BootstrapFactoryGetter.setEnvironment(BootstrapFactoryGetter.ApplicationEnvironment.TEST)
        }
    }

    private lateinit var clusterOne: ICluster
    private lateinit var clusterTwo: ICluster

    @After
    fun after() {
        if (this::clusterOne.isInitialized)
            clusterOne.shutdown().syncUninterruptibly()
        if (this::clusterTwo.isInitialized)
            clusterTwo.shutdown().syncUninterruptibly()
    }

    @Test(expected = PacketIOAuthentication.NotTheSameVersionException::class)
    fun clusterWithDifferentVersion_ConnectionWillFail() {
        clusterOne =
            DefaultClusterFactory().createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1504))
        clusterTwo = DefaultClusterFactory().joinCluster(
            "2.0", SecretAuthProvider("123"),
            Address("127.0.0.1", 1505),
            Address("127.0.0.1", 1504)
        )
    }

    @Test(expected = PacketIOAuthentication.AuthFailedException::class)
    fun clusterWithDifferentAuthKey_ConnectionWillFail() {
        clusterOne =
            DefaultClusterFactory().createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1504))
        clusterTwo = DefaultClusterFactory().joinCluster(
            "1.0", SecretAuthProvider("124"),
            Address("127.0.0.1", 1505),
            Address("127.0.0.1", 1504)
        )
    }

    @Test
    fun clusterWithMatchingVersionAndAuthKey_WillNotFail() {
        clusterOne =
            DefaultClusterFactory().createNewCluster("1.0", SecretAuthProvider("123"), Address("127.0.0.1", 1504))
        clusterTwo = DefaultClusterFactory().joinCluster(
            "1.0", SecretAuthProvider("123"),
            Address("127.0.0.1", 1505),
            Address("127.0.0.1", 1504)
        )
    }

}