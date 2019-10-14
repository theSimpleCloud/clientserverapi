package eu.thesimplecloud.clientserverapi.communication.inoutpackettest.`in`.`in`

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.JsonPacket

class PacketInTest : JsonPacket() {

    override suspend fun handle(connection: IConnection): IPacket? = null
}