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

package eu.thesimplecloud.clientserverapi.testing

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 12:56
 * @author Frederick Baier
 */
object ClusterAssert {

    fun assertRemoteNodeCount(expected: Int, cluster: ICluster) {
        val actual = cluster.getRemoteNodes().size
        if (actual != expected) {
            throw AssertionError("Expected $expected remote nodes but was $actual")
        }
    }

    fun assertPacketReceived(packetClass: Class<out IPacket>, cluster: ICluster) {
        val remoteNodes = cluster.getRemoteNodes()
        val allConnections = remoteNodes.map { it.getConnection() }
        if (!allConnections.any { hasConnectionReceivedPacket(packetClass, it) }) {
            throw AssertionError("Cluster has not received packet ${packetClass.name}")
        }
    }

    fun assertPacketReceived(packet: IPacket, cluster: ICluster) {
        val remoteNodes = cluster.getRemoteNodes()
        val allConnections = remoteNodes.map { it.getConnection() }
        if (!allConnections.any { hasConnectionReceivedPacket(packet, it) }) {
            throw AssertionError("Cluster has not received packet $packet (${packet::class.java.name})")
        }
    }

    private fun hasConnectionReceivedPacket(packetClass: Class<out IPacket>, connection: IConnection): Boolean {
        ConnectionAssert.checkForTestConnection(connection)
        return try {
            ConnectionAssert.assertPacketReceived(packetClass, connection)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun hasConnectionReceivedPacket(packet: IPacket, connection: IConnection): Boolean {
        ConnectionAssert.checkForTestConnection(connection)
        return try {
            ConnectionAssert.assertPacketReceived(packet, connection)
            true
        } catch (e: Exception) {
            false
        }
    }

}