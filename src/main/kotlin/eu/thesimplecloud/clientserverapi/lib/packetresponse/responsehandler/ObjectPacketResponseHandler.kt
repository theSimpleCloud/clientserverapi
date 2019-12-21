package eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import kotlin.reflect.KClass

open class ObjectPacketResponseHandler<T : Any>(private val expectedClass: Class<T>) : IPacketResponseHandler<T> {


    companion object {

        inline fun <reified T : Any> new() = ObjectPacketResponseHandler(T::class.java)
    }

    override fun handleResponse(packet: IPacket): T? {
        packet as? ObjectPacket<T>
                ?: throw IllegalArgumentException("Failed to cast response. Expected 'ObjectPacket' found: '${packet::class.java.simpleName}'")
        val valueClass: Class<out T>? = packet.value?.let { it::class.java }
        if (valueClass != null && !Throwable::class.java.isAssignableFrom(valueClass)) {
            if (expectedClass == Unit::class.java) return Unit as T
            if (valueClass != expectedClass) throw IllegalStateException("The class of the value does not match the expected one: expected ${expectedClass.name} was ${valueClass.name}")
        }
        return packet.value
    }
}