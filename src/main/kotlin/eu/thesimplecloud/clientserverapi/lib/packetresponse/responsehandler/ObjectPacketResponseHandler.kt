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

package eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket

open class ObjectPacketResponseHandler<T : Any>(private val expectedClass: Class<T>) : IPacketResponseHandler<T> {


    companion object {

        inline fun <reified T : Any> new() = ObjectPacketResponseHandler(T::class.java)
    }

    override fun handleResponse(packet: IPacket): T? {
        packet as? ObjectPacket<T>
                ?: throw IllegalArgumentException("Failed to cast response. Expected 'ObjectPacket' found: '${packet::class.java.simpleName}'")
        val valueClass: Class<out T> = packet.value!!.let { it::class.java }
        if (!Throwable::class.java.isAssignableFrom(valueClass)) {
            if (expectedClass == Unit::class.java) return Unit as T
            if (!expectedClass.isAssignableFrom(valueClass)) throw IllegalStateException("The class of the value does not match the expected one: expected ${expectedClass.name} was ${valueClass.name}")
        }
        return packet.value
    }
}