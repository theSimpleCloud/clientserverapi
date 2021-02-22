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

package eu.thesimplecloud.clientserverapi.cluster.component.client.impl

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.component.client.ISelfClusterClient
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.cluster.component.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * Date: 20/02/2021
 * Time: 10:09
 * @author Frederick Baier
 */
class DefaultSelfClusterClient(
    private val cluster: ICluster,
    private val uniqueId: UUID,
    private val nodeConnectedTo: IRemoteNode
) : ISelfClusterClient {


    override fun getConnection(): IConnection {
        return this.nodeConnectedTo.getPacketSender() as IConnection
    }

    override fun getNodeConnectedTo(): INode {
        return this.nodeConnectedTo
    }

    override fun getCluster(): ICluster {
        return this.cluster
    }

    override fun getUniqueId(): UUID {
        return this.uniqueId
    }
}