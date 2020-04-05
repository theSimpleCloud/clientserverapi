package eu.thesimplecloud.clientserverapi.lib.packetmanager

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.lib.extension.getKey
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

class PacketManager() : IPacketManager {

    private val packets = Maps.newConcurrentMap<Int, Class<out IPacket>>()

    private val packetRegisterCallbacks = Maps.newConcurrentMap<Class<out IPacket>, ICommunicationPromise<Int>>()

    override fun getPacketClassById(id: Int) = packets[id]

    override fun registerPacket(id: Int, packetClass: Class<out IPacket>) {
        if (packets.containsKey(id)) throw PacketException("There is already a packet registered with the id: $id")
        packets[id] = packetClass
        val promise = packetRegisterCallbacks[packetClass]
        promise?.trySuccess(id)
    }

    override fun registerPacket(packetClass: Class<out IPacket>) {
        registerPacket(getUnusedId(), packetClass)
    }

    override fun unregisterPacket(packetClass: Class<out IPacket>): Boolean {
        val key = packets.getKey { it.simpleName == packetClass.simpleName }
        key?.let { packets.remove(it) }
        return key != null
    }

    override fun getIdFromPacket(packet: IPacket): Int? = getIdFromPacket(packet::class.java)

    override fun getIdFromPacket(packetClass: Class<out IPacket>): Int? = packets.entries.firstOrNull { it.value.simpleName == packetClass.simpleName }?.key

    fun clearPackets() {
        this.packets.clear()
        this.packetRegisterCallbacks.clear()
    }

    fun getUnusedId(): Int {
        var id = 0
        while (packets.contains(id)) id++
        return id
    }

    override fun getPacketClassByName(name: String) = packets.values.firstOrNull {
        it.simpleName == name
    }

    @Synchronized
    fun <T : IPacket> getPacketIdRegisteredPromise(packetClass: Class<T>): ICommunicationPromise<Int> {
        val id = getIdFromPacket(packetClass)
        if (id != null) return CommunicationPromise.of(id)
        return this.packetRegisterCallbacks.getOrPut(packetClass) { CommunicationPromise(5000) }
    }


}