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