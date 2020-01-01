package eu.thesimplecloud.clientserverapi.lib.packetmanager

import com.google.common.collect.Maps
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PacketManager : IPacketManager {

    private val packets = Maps.newConcurrentMap<Int, Class<out IPacket>>()

    private val packetRegisterCallbacks = Collections.synchronizedCollection(ArrayList<Pair<Class<out IPacket>, CompletableFuture<Int>>>())

    override fun getPacketClassById(id: Int) = packets[id]

    fun registerPacket(id: Int, packetClass: Class<out IPacket>) {
        packets[id] = packetClass
        val callbacks = packetRegisterCallbacks.filter { it.first == packetClass }
        callbacks.forEach { it.second.complete(id) }
        packetRegisterCallbacks.removeAll(callbacks)
    }

    override fun getIdFromPacket(packet: IPacket): Int? = getIdFromPacket(packet::class.java)

    override fun getIdFromPacket(packetClass: Class<out IPacket>): Int? = packets.entries.firstOrNull { it.value == packetClass }?.key

    fun clearPackets() = this.packets.clear()

    fun getUnusedId(): Int {
        var id = 0
        while (packets.contains(id)) id++
        return id
    }

    override fun getPacketClassByName(name: String) = packets.values.firstOrNull {
        it.simpleName == name
    }

    fun <T : IPacket> getPacketIdCompletableFuture(packetClass: Class<T>): CompletableFuture<Int> {
        val completableFuture = CompletableFuture<Int>()
        val id = getIdFromPacket(packetClass)
        if (id == null)
            packetRegisterCallbacks.add(packetClass to completableFuture)
        else
            completableFuture.complete(id)
        return completableFuture
    }


}