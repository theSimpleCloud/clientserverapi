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

package eu.thesimplecloud.clientserverapi.lib.list.impl

import eu.thesimplecloud.clientserverapi.client.INettyClient
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.synclist.PacketIOAddElementToClientServerList
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.synclist.PacketIORemoveElementFromClientServerList
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.synclist.PacketIOUpdateElementFromClientServerList
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Identifiable
import eu.thesimplecloud.clientserverapi.server.INettyServer
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Created by IntelliJ IDEA.
 * Date: 03/02/2021
 * Time: 18:45
 * @author Frederick Baier
 */
class ClientServerSyncList<T : Identifiable>(
    private val bootstrap: ICommunicationBootstrap,
    private val name: String
) : AbstractSyncList<T>() {

    override fun addElement(element: T, fromPacket: Boolean): ICommunicationPromise<Unit> {
        super.addElement(element, fromPacket)
        return forwardPacketToClientServerSystem(PacketIOAddElementToClientServerList(name, element), fromPacket)
    }

    override fun removeElement(identifier: Any, fromPacket: Boolean): ICommunicationPromise<Unit> {
        super.removeElement(identifier, fromPacket)
        return forwardPacketToClientServerSystem(PacketIORemoveElementFromClientServerList(name, identifier), fromPacket)
    }


    override fun updateElement(cachedValue: T): ICommunicationPromise<Unit> {
        val changesJson = updateElement0(cachedValue)
        return forwardPacketToClientServerSystem(PacketIOUpdateElementFromClientServerList(name, changesJson), false)
    }

    override fun applyUpdate(json: JsonLib): ICommunicationPromise<Unit> {
        super.applyUpdate(json)
        return forwardPacketToClientServerSystem(PacketIOUpdateElementFromClientServerList(name, json), true)
    }


    private fun forwardPacketToClientServerSystem(packet: IPacket, fromPacket: Boolean): ICommunicationPromise<Unit> {
        if (fromPacket && !bootstrap.isServer()) return CommunicationPromise.UNIT_PROMISE

        return if (bootstrap.isServer()) {
            bootstrap as INettyServer
            bootstrap.getClientManager().sendPacketToAllClients(packet)
        } else {
            bootstrap as INettyClient
            bootstrap.getConnection().sendUnitQuery(packet)
        }
    }


}