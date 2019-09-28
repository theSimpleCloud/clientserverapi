package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import java.lang.IllegalArgumentException

class ObjectPacketResponseHandler<T : Any>(val type: Class<T>): IPacketResponseHandler<T> {

    override fun handleResponse(packet: IPacket): T? {
        packet as? ObjectPacket<T>
                ?: throw IllegalArgumentException("Failed to cast response. Expected 'ObjectPacket' found: '${packet::class.java.simpleName}'")
        return packet.value
    }
}