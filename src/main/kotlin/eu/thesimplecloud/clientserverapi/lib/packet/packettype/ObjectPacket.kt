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

package eu.thesimplecloud.clientserverapi.lib.packet.packettype

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import io.netty.buffer.ByteBuf


abstract class ObjectPacket<T : Any>() : JsonPacket() {

    var value: T? = null

    constructor(value: T?) : this() {
        this.value = value
    }

    override fun read(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        super.read(byteBuf, communicationBootstrap)
        val className = this.jsonLib.getString("className")
        //return because the value to sent was null in this case.
        if (className.isNullOrBlank()) return
        val clazz = Class.forName(
                className,
                true,
                communicationBootstrap.getClassLoaderToSearchObjectPacketsClasses()
        ) as Class<T>
        this.value = this.jsonLib.getObject("data", clazz)
    }

    override fun write(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        if (this.value != null) {
            this.jsonLib.append("className", value!!::class.java.name)
            this.jsonLib.append("data", value)
        }
        super.write(byteBuf, communicationBootstrap)
    }

    companion object {

        fun <T : Any> getNewEmptyObjectPacket() = object : ObjectPacket<T>() {
            override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
                return success(Unit)
            }
        }

        fun <T : Any> getNewObjectPacketWithContent(value: T?): ObjectPacket<T> {
            val objectPacket = getNewEmptyObjectPacket<T>()
            objectPacket.value = value
            return objectPacket
        }
    }

}