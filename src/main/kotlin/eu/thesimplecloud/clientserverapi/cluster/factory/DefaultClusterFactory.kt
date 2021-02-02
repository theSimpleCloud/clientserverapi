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

package eu.thesimplecloud.clientserverapi.cluster.factory

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.cluster.DefaultCluster
import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.auth.IClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.packets.PacketIOGetOtherNodes
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.NodeInfo
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.util.Address

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 18:17
 * @author Frederick Baier
 */
class DefaultClusterFactory : IClusterFactory {

    override fun createNewCluster(
        version: String,
        authProvider: IClusterAuthProvider,
        bindAddress: Address,
    ): ICluster {
        return DefaultCluster(version, authProvider, bindAddress)
    }

    override fun joinCluster(
        version: String,
        authProvider: IClusterAuthProvider,
        bindAddress: Address,
        connectAddress: Address
    ): ICluster {
        val client = createIPsClient(connectAddress)
        val promise = client.getConnection().sendQuery<Array<NodeInfo>>(PacketIOGetOtherNodes(), 4000)
        val allNodeAddresses = promise.getBlocking().toList()
        return DefaultCluster(version, authProvider, bindAddress, allNodeAddresses)
    }

    private fun createIPsClient(address: Address): INettyClient {
        val factory = BootstrapFactoryGetter.getFactory()
        val client = factory.createClient(address)
        client.addPacketsByPackage("eu.thesimplecloud.clientserverapi.cluster.packets")
        client.start().syncUninterruptibly()
        println("connected IPs")
        return client
    }

}