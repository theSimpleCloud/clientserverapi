package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.debug.DebugMessage
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packetresponse.IPacketResponseManager
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder(private val communicationBootstrap: ICommunicationBootstrap, private val packetManager: PacketManager, private val packetResponseManager: IPacketResponseManager) : ByteToMessageDecoder() {


    @Synchronized
    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val receivedString = ByteBufStringHelper.nextString(byteBuf)
        val jsonData = JsonData.fromJsonString(receivedString)
        val packetData = jsonData.getObject("data", PacketData::class.java)
                ?: throw PacketException("PacketData is not present.")
        //objectPacket =-1
        val packet = if (packetData.isResponse) {
            val objectPacket = ObjectPacket.getNewEmptyObjectPacket<Any>()
            objectPacket.read(byteBuf, communicationBootstrap)
            objectPacket
        } else {
            val packetClass: Class<out IPacket>? = packetManager.getPacketClassByOppositePacketName(packetData.sentPacketName)
            packetClass
                    ?: throw PacketException("Can't find opposite packet of: ${packetData.sentPacketName}")
            val packet = packetClass.newInstance()
            packet.read(byteBuf, communicationBootstrap)
            packet
        }
        out.add(WrappedPacket(packetData, packet))
        if (this.communicationBootstrap.getDebugMessageManager().isActive(DebugMessage.PACKET_RECEIVED)) {
            println("Received Packet ${packet::class.java.simpleName} (${packetData.uniqueId})")
        }
    }


}