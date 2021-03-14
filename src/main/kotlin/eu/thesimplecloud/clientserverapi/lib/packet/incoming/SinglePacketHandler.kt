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

package eu.thesimplecloud.clientserverapi.lib.packet.incoming

import eu.thesimplecloud.clientserverapi.lib.coroutine.SingleThreadCoroutine
import eu.thesimplecloud.clientserverapi.lib.packet.PacketHeader
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.AbstractPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.packet.response.PacketOutErrorResponse
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import kotlinx.coroutines.launch

/**
 * Created by IntelliJ IDEA.
 * Date: 25.06.2020
 * Time: 11:07
 * @author Frederick Baier
 */
class SinglePacketHandler(private val sender: AbstractPacketSender, private val wrappedPacket: WrappedPacket) {

    fun handlePacket() {
        if (!hasAccess(this.wrappedPacket)) {
            return
        }
        if (this.wrappedPacket.packetHeader.isResponse) {
            handleResponsePacket()
        } else {
            SingleThreadCoroutine.scope.launch {
                handleQuery()
            }
        }
    }

    private fun hasAccess(wrappedPacket: WrappedPacket): Boolean {
        if (wrappedPacket.packetHeader.isResponse) {
            return isValidResponsePacket()
        }
        return checkAccess()
    }

    private fun isValidResponsePacket(): Boolean {
        val packetResponseManager = this.sender.getCommunicationBootstrap().getPacketResponseManager()
        return packetResponseManager.isResponseHandlerAvailable(this.wrappedPacket.packetHeader.uniqueId)
    }

    private fun checkAccess(): Boolean {
        return this.sender.getCommunicationBootstrap().getAccessHandler()
                .isAccessAllowed(this.sender, this.wrappedPacket.packet)
    }

    private fun handleResponsePacket() {
        val packetResponseManager = this.sender.getCommunicationBootstrap().getPacketResponseManager()
        packetResponseManager.incomingPacket(this.wrappedPacket)
    }

    private suspend fun handleQuery() {
        try {
            val result = this.wrappedPacket.packet.handle(this.sender)
            handleResult(result, this.wrappedPacket)
        } catch (e: Exception) {
            throw PacketException(
                    "An error occurred while attempting to handle packet: ${wrappedPacket.packet::class.java.simpleName}",
                    e
            )
        }
    }

    private fun handleResult(result: ICommunicationPromise<Any>, wrappedPacket: WrappedPacket) {
        val packetPromise = getPacketFromResult(result)
        packetPromise.then { packetToSend ->
            sendResponsePacket(wrappedPacket, packetToSend)
        }.addFailureListener {
            throw PacketException(
                    "An error occurred while attempting to send response for packet: " +
                            wrappedPacket.packet::class.java.simpleName, it as java.lang.Exception
            )
        }
    }

    private fun sendResponsePacket(wrappedPacket: WrappedPacket, packetToSend: ObjectPacket<out Any>) {
        val responseData = PacketHeader(wrappedPacket.packetHeader.uniqueId, packetToSend::class.java.simpleName, true)
        this.sender.sendPacket(WrappedPacket(responseData, packetToSend), CommunicationPromise())
    }

    private fun getPacketFromResult(result: ICommunicationPromise<Any>): ICommunicationPromise<ObjectPacket<out Any>> {
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