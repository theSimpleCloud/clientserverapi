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

package eu.thesimplecloud.clientserverapi.cluster.component.handler

import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.type.AbstractCluster
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.handler.IConnectionHandler

/**
 * Created by IntelliJ IDEA.
 * Date: 30/01/2021
 * Time: 22:55
 * @author Frederick Baier
 */
class ClusterConnectionHandler : IConnectionHandler {

    override fun onConnectionActive(connection: IConnection) {

    }

    override fun onConnectionAuthenticated(connection: IConnection) {
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        throw ex
    }

    override fun onConnectionInactive(connection: IConnection) {
        val cluster = connection.getCommunicationBootstrap().getCluster()!!
        val remoteComponent = connection.getProperty<IRemoteClusterComponent>("component")!!
        cluster as AbstractCluster
        cluster.onComponentLeave(remoteComponent, cluster.getSelfComponent())
    }
}