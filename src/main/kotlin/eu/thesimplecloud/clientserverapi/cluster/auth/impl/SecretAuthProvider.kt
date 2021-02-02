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

package eu.thesimplecloud.clientserverapi.cluster.auth.impl

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.auth.ISecretClusterAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.secret.PacketIOSecretAuth
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.secret.SecretAuthNodeInfo
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 12:52
 * @author Frederick Baier
 */
class SecretAuthProvider(
    private val secret: String
) : ISecretClusterAuthProvider {

    override fun authenticate(connection: IConnection, sentSecret: String): Boolean {
        return sentSecret == secret
    }

    override fun authenticateOnRemoteNodes(cluster: ICluster, remoteNodes: List<IRemoteNode>) {
        val ownServerAddress = cluster.getSelfNode().getServer().getAddress()
        val startupTime = cluster.getSelfNode().getStartupTime()
        val version = cluster.getVersion()
        remoteNodes.forEach {
            it.getConnection().setAuthenticated(true)
            val promise = it.getConnection().sendUnitQuery(PacketIOSecretAuth(SecretAuthNodeInfo(version, ownServerAddress, startupTime, secret)), 5000)
                    .awaitUninterruptibly()
        }
    }
}