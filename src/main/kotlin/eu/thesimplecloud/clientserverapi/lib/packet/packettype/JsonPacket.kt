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

import eu.thesimplecloud.clientserverapi.lib.ByteBufStringHelper
import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.jsonlib.GsonCreator
import eu.thesimplecloud.jsonlib.JsonLib
import io.netty.buffer.ByteBuf

abstract class JsonPacket : IPacket {

    companion object {
        val PACKET_GSON = GsonCreator().excludeAnnotations(PacketExclude::class.java).create()

        fun getNewEmptyJsonPacket() = object: JsonPacket() {
            override suspend fun handle(connection: IConnection): ICommunicationPromise<Any> {
                return success(Unit)
            }
        }
        fun getNewJsonPacketWithContent(jsonLib: JsonLib): JsonPacket {
            val jsonPacket = getNewEmptyJsonPacket()
            jsonPacket.jsonLib = jsonLib
            return jsonPacket
        }
    }

    var jsonLib: JsonLib = JsonLib.empty(PACKET_GSON)

    override fun read(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        jsonLib = JsonLib.fromJsonString(ByteBufStringHelper.nextString(byteBuf), PACKET_GSON)

    }

    override fun write(byteBuf: ByteBuf, communicationBootstrap: ICommunicationBootstrap) {
        jsonLib.getAsJsonString().let { ByteBufStringHelper.writeString(byteBuf, it) }
    }
}