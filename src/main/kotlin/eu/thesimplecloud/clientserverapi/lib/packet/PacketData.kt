package eu.thesimplecloud.clientserverapi.lib.packet
import java.util.*

data class PacketData(val uniqueId: UUID, val id: Int, val sentPacketName: String){

    fun isResponse() = id < 0

}