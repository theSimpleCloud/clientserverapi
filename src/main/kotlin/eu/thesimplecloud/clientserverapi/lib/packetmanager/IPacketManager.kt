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

package eu.thesimplecloud.clientserverapi.lib.packetmanager

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

interface IPacketManager {

    /**
     * Returns the first class of the packet found by the specified id
     */
    fun getPacketClassBySimpleName(name: String): Class<out IPacket>?

    /**
     * Returns the packet class by the name of the opposite packet
     * Example: for "PacketInMessage" it will return the class of "PacketOutMessage"
     */
    fun getPacketClassByOppositePacketName(name: String): Class<out IPacket>?

    /**
     * Registers the specified [packetClass]
     */
    fun registerPacket(packetClass: Class<out IPacket>)

    /**
     * Unregisters the specified [packetClass]
     * @return whether the packet was registered
     */
    fun unregisterPacket(packetClass: Class<out IPacket>): Boolean

    /**
     * Clears all packets
     */
    fun clearPackets()
}