package eu.thesimplecloud.clientserverapi.lib.packetmanager

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.lib.extension.getKey
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

class PacketManager() : IPacketManager {

    private val packets = Maps.newConcurrentMap<String, Class<out IPacket>>()

    override fun getPacketClassBySimpleName(name: String): Class<out IPacket>? {
        return packets[name]
    }

    override fun getPacketClassByOppositePacketName(name: String): Class<out IPacket>? {
        val oppositePacketName = when {
            name.startsWith("PacketOut") -> {
                name.replaceFirst("PacketOut", "PacketIn")
            }
            name.startsWith("PacketIn") -> {
                name.replaceFirst("PacketIn", "PacketOut")
            }
            else -> name
        }
        return packets[oppositePacketName]
    }

    override fun registerPacket(packetClass: Class<out IPacket>) {
        packets[packetClass.simpleName] = packetClass
    }

    override fun unregisterPacket(packetClass: Class<out IPacket>): Boolean {
        val key = packets.getKey { it.simpleName == packetClass.simpleName }
        key?.let { packets.remove(it) }
        return key != null
    }

    override fun clearPackets() {
        this.packets.clear()
    }


}