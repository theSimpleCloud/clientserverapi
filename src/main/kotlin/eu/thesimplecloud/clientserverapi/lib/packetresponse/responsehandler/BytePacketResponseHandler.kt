package eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import java.lang.IllegalArgumentException

class BytePacketResponseHandler : IPacketResponseHandler<Array<Byte>> {

    override fun handleResponse(packet: IPacket): Array<Byte> {
        packet as? BytePacket
                ?: throw IllegalArgumentException("Failed to cast response. Expected 'BytePacket' found: '${packet::class.java.simpleName}'")
        return packet.buffer.array().toTypedArray()
    }
}