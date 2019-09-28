package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import io.netty.buffer.ByteBuf
import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender

abstract class JsonPacket : IPacket {

    companion object {
        fun getNewEmptyJsonPacket() = object: JsonPacket() {
            override suspend fun handle(packetSender: IPacketSender): IPacket? {
                return null
            }
        }
        fun getNewJsonPacketWithContent(jsonData: JsonData): JsonPacket {
            val jsonPacket = getNewEmptyJsonPacket()
            jsonPacket.jsonData = jsonData
            return jsonPacket
        }
    }

    var jsonData: JsonData = JsonData()

    override fun read(byteBuf: ByteBuf) {
        jsonData = JsonData.fromJsonString(ByteBufStringHelper.nextString(byteBuf))

    }

    override fun write(byteBuf: ByteBuf) {
        jsonData.asJsonString.let { ByteBufStringHelper.writeString(byteBuf, it) }
    }
}