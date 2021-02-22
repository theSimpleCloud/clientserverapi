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

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 12:13
 * @author Frederick Baier
 */
object ConnectionAssert {

    fun assertPacketReceived(packetClass: Class<out IPacket>, connection: IConnection) {
        checkForTestConnection(connection)
        connection as AbstractTestConnection
        val receivedPacket =  connection.receivedPackets.firstOrNull { it.packet::class.java == packetClass }
        if (receivedPacket == null) {
            throw AssertionError("Packet ${packetClass.name} was not received")
        }
    }

    fun assertPacketReceived(packet: IPacket, connection: IConnection) {
        checkForTestConnection(connection)
        connection as AbstractTestConnection
        val receivedPacket =  connection.receivedPackets.firstOrNull { it.packet == packet }
        if (receivedPacket == null) {
            throw AssertionError("Packet $packet (${packet::class.java.name}) was not received")
        }
    }

    fun assertSentPacketReceived(packetClass: Class<out IPacket>, connection: IConnection) {
        checkForTestConnection(connection)
        connection as AbstractTestConnection
        if (!connection.isOpen())
            throw IllegalStateException("Connection must be open to call this method")

        val otherSideConnection = connection.otherSideConnection!!
        return assertPacketReceived(packetClass, otherSideConnection)
    }

    fun assertSentPacketReceived(packet: IPacket, connection: IConnection) {
        checkForTestConnection(connection)
        connection as AbstractTestConnection
        if (!connection.isOpen())
            throw IllegalStateException("Connection must be open to call this method")

        val otherSideConnection = connection.otherSideConnection!!
        return assertPacketReceived(packet, otherSideConnection)
    }

    fun checkForTestConnection(sender: IPacketSender) {
        if (sender !is AbstractTestConnection) {
            throw IllegalArgumentException("ConnectionAssert is only available in a TEST environment")
        }
    }

}