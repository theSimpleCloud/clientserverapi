package eu.thesimplecloud.clientserverapi.lib.packetmanager

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

interface IPacketManager {

    /**
     * Returns the first class of the packet found by the specified id
     */
    fun getPacketClassById(id: Int): Class<out IPacket>?

    /**
     * Returns the id of the specified packet
     */
    fun getIdFromPacket(packet: IPacket): Int?

    /**
     * Returns the id of the specified packet class
     */
    fun getIdFromPacket(packetClass: Class<out IPacket>): Int?

    /**
     * Returns the packet class found by the specified name
     */
    fun getPacketClassByName(name: String): Class<out IPacket>?
}