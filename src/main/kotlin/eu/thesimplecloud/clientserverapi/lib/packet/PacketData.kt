package eu.thesimplecloud.clientserverapi.lib.packet
import java.util.*

data class PacketData(val uniqueId: UUID, val sentPacketName: String, val isResponse: Boolean)