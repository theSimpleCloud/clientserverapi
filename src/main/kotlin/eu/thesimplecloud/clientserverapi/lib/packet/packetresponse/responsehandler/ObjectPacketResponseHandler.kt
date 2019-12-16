package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.lang.IllegalArgumentException

open class ObjectPacketResponseHandler<T : Any>(val type: Class<T>) : IPacketResponseHandler<T> {

    companion object {

        inline fun <reified T : Any> new() = ObjectPacketResponseHandler(T::class.java)

        private val NULL_HANDLER = object : ObjectPacketResponseHandler<Any>(Any::class.java) {
            override fun handleResponse(packet: IPacket): Any? = null
        }

        fun <T : Any> getNullHandler() = NULL_HANDLER as ObjectPacketResponseHandler<T>
    }

    override fun handleResponse(packet: IPacket): T? {
        packet as? ObjectPacket<T>
                ?: throw IllegalArgumentException("Failed to cast response. Expected 'ObjectPacket' found: '${packet::class.java.simpleName}'")
        return packet.value
    }
}