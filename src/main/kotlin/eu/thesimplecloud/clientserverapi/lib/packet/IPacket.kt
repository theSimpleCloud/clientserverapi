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

package eu.thesimplecloud.clientserverapi.lib.packet

import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf

interface IPacket : IPacketHelperMethods {

    /**
     * Returns whether this packet requires authentication
     */
    fun isAuthRequired(): Boolean = true

    /**
     * This method is used to read the content of the [ByteBuf] and save it in the packet
     * Note: [read] will be called before [handle]
     * @param byteBuf the [ByteBuf] to read from
     * @param sender the sender this packet was sent from
     */
    fun read(byteBuf: ByteBuf, sender: IPacketSender)

    /**
     * This method is used to write the packets content into the [ByteBuf]
     * @param byteBuf the [ByteBuf] to write to
     * @param sender the sender this packet will be sent to
     */
    fun write(byteBuf: ByteBuf, sender: IPacketSender)

    /**
     * Calls to handle a packet
     * @return the [ICommunicationPromise] that will be completed with the result or a failure.
     * The result will be sent as response.
     */
    suspend fun handle(sender: IPacketSender): ICommunicationPromise<Any>

}