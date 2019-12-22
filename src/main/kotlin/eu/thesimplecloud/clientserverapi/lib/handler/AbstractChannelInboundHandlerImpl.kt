package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
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
                val responseResult = runCatching {
                    wrappedPacket.packet.handle(connection)
                }
                val packetPromise = getPacketFromResponse(responseResult)
                packetPromise.thenAccept { packetToSend ->
                    val responseData = PacketData(wrappedPacket.packetData.uniqueId, -1, packetToSend::class.java.simpleName)
                    connection.sendPacket(WrappedPacket(responseData, packetToSend))
                }
            }
        }
    }

    fun getPacketFromResponse(responseResult: Result<Any?>): ICommunicationPromise<ObjectPacket<out Any>> {
        when {
            responseResult.isFailure -> {
                return CommunicationPromise.of(PacketOutErrorResponse(responseResult.exceptionOrNull()!!))
            }
            else -> {
                val result = responseResult.getOrNull()
                when {
                    result is ICommunicationPromise<*> -> {
                        val returnPromise = CommunicationPromise<ObjectPacket<out Any>>(result.getTimeout())
                        result.addCompleteListener {
                            if (it.isSuccess) {
                                returnPromise.setSuccess(ObjectPacket.getNewObjectPacketWithContent(it.getNow()))
                            } else {
                                returnPromise.setSuccess(PacketOutErrorResponse(it.cause()))
                            }
                        }
                        return returnPromise
                    }
                    else -> return CommunicationPromise.of(ObjectPacket.getNewObjectPacketWithContent(result))
                }
            }
        }
    }

}