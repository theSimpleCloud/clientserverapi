package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class AbstractChannelInboundHandlerImpl : SimpleChannelInboundHandler<WrappedPacket>() {

    abstract fun getConnection(ctx: ChannelHandlerContext): AbstractConnection

    override fun channelRead0(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket) {
        val connection = getConnection(ctx)
        if (wrappedPacket.packetData.isResponse()) {
            connection.packetResponseManager.incomingPacket(wrappedPacket)
        } else {
            GlobalScope.launch {
                val result = try {
                    wrappedPacket.packet.handle(connection)
                } catch (e: Exception) {
                    throw PacketException("An error occurred while attempting to handle packet: ${wrappedPacket.packet::class.java.simpleName}", e)
                }
                val packetPromise = getPacketFromResult(result)
                packetPromise.then { packetToSend ->
                    val responseData = PacketData(wrappedPacket.packetData.uniqueId, -1, packetToSend::class.java.simpleName)
                    connection.sendPacket(WrappedPacket(responseData, packetToSend), CommunicationPromise())
                }
            }
        }
    }

    fun getPacketFromResult(result: ICommunicationPromise<out Any>): ICommunicationPromise<ObjectPacket<out Any>> {
        val returnPromise = CommunicationPromise<ObjectPacket<out Any>>(result.getTimeout())
        result.addCompleteListener {
            if (it.isSuccess) {
                returnPromise.setSuccess(ObjectPacket.getNewObjectPacketWithContent(it.get()))
            } else {
                returnPromise.setSuccess(PacketOutErrorResponse(it.cause()))
            }
        }
        return returnPromise
    }
}