package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import java.lang.IllegalArgumentException

class JsonPacketResponseHandler : IPacketResponseHandler<JsonData> {

    override fun handleResponse(packet: IPacket): JsonData {
        packet as? JsonPacket ?: throw IllegalArgumentException("Failed to cast response. Expected 'JsonPacket' found: '${packet::class.java.simpleName}'")
        return packet.jsonData
    }
}