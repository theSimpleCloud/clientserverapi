package eu.thesimplecloud.clientserverapi.lib.packet

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.json.JsonData
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.IPacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.WrappedResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.ObjectPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.BytePacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import java.lang.IllegalStateException

class PacketDecoder(private val packetManager: PacketManager, private val packetResponseManager: IPacketResponseManager) : ByteToMessageDecoder() {


    override fun decode(ctx: ChannelHandlerContext, byteBuf: ByteBuf, out: MutableList<Any>) {
        val receivedString = ByteBufStringHelper.nextString(byteBuf)
        val jsonData = JsonData.fromJsonString(receivedString)
        val packetData = jsonData.getObject("data", PacketData::class.java)
                ?: throw PacketException("PacketData is not present.")
        //jsonpacket =-1 bytepacket = -2
        val packet = when (packetData.id) {
            -1 -> {
                val jsonPacket = JsonPacket.getNewEmptyJsonPacket()
                jsonPacket.read(byteBuf)
                jsonPacket
            }
            -2 -> {
                val wrappedResponseHandler = packetResponseManager.getResponseHandler(packetData.uniqueId)
                wrappedResponseHandler ?: throw IllegalStateException("No ResponseHandler was available for packet by id : " + packetData.uniqueId)
                if (wrappedResponseHandler.packetResponseHandler == IPacketResponseHandler.getNullHandler<Any>()) {
                    val wrappedResponseHandler = wrappedResponseHandler as WrappedResponseHandler<Any>
                    wrappedResponseHandler.packetResponseHandler = ObjectPacketResponseHandler.getNullHandler<Any>()
                }
                val packetResponseHandler = wrappedResponseHandler.packetResponseHandler as? ObjectPacketResponseHandler
                        ?: throw IllegalStateException("Received ObjectPacket as response but the response handler was no ObjectPacketResponseHandler: " + packetData.uniqueId)
                val objectPacket = ObjectPacket.getNewEmptyObjectPacket(packetResponseHandler.type)
                objectPacket.read(byteBuf)
                objectPacket
            }
            -3 -> {
                val bytePacket = BytePacket.getNewEmptyBytePacket()
                bytePacket.read(byteBuf)
                bytePacket
            }
            else -> {
                val packetClass: Class<out IPacket>? = packetManager.getPacketClassById(packetData.id)
                packetClass ?: throw PacketException("Can't find packet by id ${packetData.id}")
                val packet = packetClass.newInstance()
                packet.read(byteBuf)
                packet
            }
        }
        out.add(WrappedPacket(packetData, packet))
    }


}