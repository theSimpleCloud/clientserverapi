/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.AbstractConnection
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

abstract class AbstractChannelInboundHandlerImpl : SimpleChannelInboundHandler<WrappedPacket>() {

    abstract fun getConnection(ctx: ChannelHandlerContext): AbstractConnection

    override fun channelRead0(ctx: ChannelHandlerContext, wrappedPacket: WrappedPacket) {
        val connection = getConnection(ctx)
        if (wrappedPacket.packetData.isResponse) {
            connection.packetResponseManager.incomingPacket(wrappedPacket)
        } else {
            GlobalScope.launch(Dispatchers.Default) {
                val result = try {
                    wrappedPacket.packet.handle(connection)
                } catch (e: Exception) {
                    throw PacketException("An error occurred while attempting to handle packet: ${wrappedPacket.packet::class.java.simpleName}", e)
                }
                val packetPromise = getPacketFromResult(result)
                packetPromise.then { packetToSend ->
                    val responseData = PacketData(wrappedPacket.packetData.uniqueId, packetToSend::class.java.simpleName, true)
                    connection.sendPacket(WrappedPacket(responseData, packetToSend), CommunicationPromise())
                }.addFailureListener {
                    if (!connection.wasConnectionCloseIntended())
                        throw PacketException("An error occurred while attempting to send response for packet: " +
                                wrappedPacket.packet::class.java.simpleName, it as java.lang.Exception)
                }
            }
        }
    }

    private fun getPacketFromResult(result: ICommunicationPromise<out Any>): ICommunicationPromise<ObjectPacket<out Any>> {
        val returnPromise = CommunicationPromise<ObjectPacket<out Any>>(enableTimeout = false)
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