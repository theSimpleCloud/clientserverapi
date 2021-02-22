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

package eu.thesimplecloud.clientserverapi.cluster.client

import eu.thesimplecloud.clientserverapi.cluster.auth.impl.SecretAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.factory.DefaultClusterFactory
import eu.thesimplecloud.clientserverapi.lib.factory.CommunicationBootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * Date: 20/02/2021
 * Time: 13:19
 * @author Frederick Baier
 */
class ClusterClientTest {

    @Before
    fun setUp() {
        CommunicationBootstrapFactoryGetter.setEnvironment(CommunicationBootstrapFactoryGetter.ApplicationEnvironment.NORMAL)
    }

    @Test
    fun test() {
        val clusterNode = DefaultClusterFactory().createNewCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 4903)
        )
        val clusterClient = DefaultClusterFactory().joinClusterAsClient(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 4903)
        )
    }
}