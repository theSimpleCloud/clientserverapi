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

package eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Identifiable
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Created by IntelliJ IDEA.
 * Date: 01/02/2021
 * Time: 11:55
 * @author Frederick Baier
 */
class PacketIOUpdateElementFromClusterList() : JsonPacket() {

    constructor(name: String, updateJson: JsonLib) : this() {
        this.jsonLib.append("name", name)
            .append("updateJson", updateJson)
    }

    override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
        val name = this.jsonLib.getString("name") ?: return contentException("name")
        val updateJson = this.jsonLib.getProperty("updateJson") ?: return contentException("updateJson")
        val cluster = connection.getCommunicationBootstrap().getCluster()!!
        val clusterList = cluster.getClusterListManager().getSyncListByName<Identifiable>(name) ?: return failure(NoSuchElementException("List not found"))
        return clusterList.applyUpdate(updateJson)
    }
}