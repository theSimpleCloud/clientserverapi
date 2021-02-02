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

package eu.thesimplecloud.clientserverapi.cluster.list.manager

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.list.ClusterList
import eu.thesimplecloud.clientserverapi.cluster.list.IClusterList
import eu.thesimplecloud.clientserverapi.cluster.list.IClusterListItem
import eu.thesimplecloud.clientserverapi.cluster.node.IRemoteNode
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOSyncClusterList

/**
 * Created by IntelliJ IDEA.
 * Date: 01/02/2021
 * Time: 12:06
 * @author Frederick Baier
 */
class ClusterListManager(
    private val cluster: ICluster
) : IClusterListManager {

    private val nameToClusterList = Maps.newConcurrentMap<String, IClusterList<out IClusterListItem>>()

    override fun registerClusterList(name: String, clusterList: IClusterList<out IClusterListItem>) {
        this.nameToClusterList[name] = clusterList
    }

    override fun <T : IClusterListItem> getClusterListByName(name: String): IClusterList<T>? {
        return this.nameToClusterList[name] as IClusterList<T>?
    }

    override fun <T : IClusterListItem> getClusterListByNameOrCreate(name: String, arrayClass: Class<*>): IClusterList<T> {
        val existingList = getClusterListByName<T>(name)
        if (existingList == null) {
            val newClusterList = ClusterList<T>(cluster, name, arrayClass as Class<Array<Any>>)
            registerClusterList(name, newClusterList)
            return newClusterList
        }
        return existingList
    }

    override fun synchronizeAllWithNode(node: IRemoteNode) {
        this.nameToClusterList.forEach { name, list ->
            node.getConnection().sendUnitQuery(PacketIOSyncClusterList(name, list)).awaitUninterruptibly()
        }
    }
}