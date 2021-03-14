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

package eu.thesimplecloud.clientserverapi.lib.list.manager.impl

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.synclist.PacketIOAddElementToClientServerList
import eu.thesimplecloud.clientserverapi.lib.list.ISyncList
import eu.thesimplecloud.clientserverapi.lib.list.impl.ClientServerSyncList
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
import eu.thesimplecloud.clientserverapi.lib.util.Identifiable

/**
 * Created by IntelliJ IDEA.
 * Date: 01/02/2021
 * Time: 12:06
 * @author Frederick Baier
 */
class ClientServerSyncListManager(
    val communicationBootstrap: ICommunicationBootstrap
) : AbstractSyncListManager() {

    override fun <T : Identifiable> createNewSyncList(name: String): ISyncList<T> {
        return ClientServerSyncList<T>(communicationBootstrap, name)
    }

    override fun synchronizeAllWithPacketSender(packetSender: IPacketSender): ICommunicationPromise<Unit> {
        return this.nameToSyncList.map { (name, list) ->
            list.getAllElements().map {
                packetSender.sendUnitQuery(PacketIOAddElementToClientServerList(name, it))
            }.combineAllPromises()
        }.combineAllPromises()
    }

}