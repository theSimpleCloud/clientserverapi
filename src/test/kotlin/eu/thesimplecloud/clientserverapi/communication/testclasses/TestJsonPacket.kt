package eu.thesimplecloud.clientserverapi.communication.testclasses

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket

class TestJsonPacket(): JsonPacket() {

    constructor(message: String) : this() {
        this.jsonData.append("message", message)
    }

    override suspend fun handle(connection: IConnection): IPacket? {
        val string = this.jsonData.getString("message")
        return string?.let { TestJsonPacket(it) }
    }
}