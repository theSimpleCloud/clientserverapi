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

package eu.thesimplecloud.clientserverapi.cluster.packets.auth

import eu.thesimplecloud.clientserverapi.cluster.component.factory.ClusterComponentFactory
import eu.thesimplecloud.clientserverapi.cluster.component.type.ClusterComponentType
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ClientComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.ComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.packets.auth.dto.NodeComponentDTO
import eu.thesimplecloud.clientserverapi.cluster.type.AbstractCluster
import eu.thesimplecloud.clientserverapi.cluster.type.INodeCluster
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 15:37
 * @author Frederick Baier
 */
abstract class PacketIOAuthentication : JsonPacket {

    constructor() : super()

    constructor(authDTO: IAuthDTO, componentDTO: ComponentDTO) : super() {
        this.jsonLib.append("type", componentDTO.type)
            .append("authDTO", authDTO)
            .append("authDTOClassName", authDTO::class.java.name)
            .append("componentDTO", componentDTO)
    }

    override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Any> {
        if (sender !is IConnection)
            throw IllegalStateException("Cannot handle authentication for just a packet sender")
        val clusterComponentType = this.jsonLib.getObject("type", ClusterComponentType::class.java)
            ?: return contentException("type")
        val componentDTO = when (clusterComponentType) {
            ClusterComponentType.NODE -> this.jsonLib.getObject("componentDTO", NodeComponentDTO::class.java)!!
            ClusterComponentType.CLIENT -> this.jsonLib.getObject("componentDTO", ClientComponentDTO::class.java)!!
        }
        val cluster = sender.getCommunicationBootstrap().getCluster()!!
        cluster as AbstractCluster
        cluster as INodeCluster
        if (componentDTO.version != cluster.getVersion()) {
            return failure(NotTheSameVersionException())
        }
        val authDTOClass = Class.forName(this.jsonLib.getString("authDTOClassName"))
        val authDTO = this.jsonLib.getObject("authDTO", authDTOClass) as IAuthDTO
        val authSuccess = handleAuth(sender, authDTO)
        if (!authSuccess) {
            return failure(AuthFailedException())
        }

        sender.setAuthenticated(true)
        val remoteComponent = ClusterComponentFactory.createComponent(sender, componentDTO)
        sender.setProperty("type", clusterComponentType)
        sender.setProperty("component", remoteComponent)
        cluster.onComponentJoin(remoteComponent, cluster.getSelfComponent())
        return unit()
    }



    abstract fun handleAuth(connection: IConnection, authDTO: IAuthDTO): Boolean

    class NotTheSameVersionException() : Exception()

    class AuthFailedException() : Exception()

    override fun isAuthRequired(): Boolean {
        return false
    }
}