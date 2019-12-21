package eu.thesimplecloud.clientserverapi.server.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
class PacketInGetPacketId : ObjectPacket<String>() {

    override suspend fun handle(connection: IConnection): Int {
        connection as IConnectedClient<*>
        val packetName = this.value ?: return -1
        val packetClass = when {
            packetName.startsWith("PacketOut") -> {
                val newName = packetName.replaceFirst("PacketOut", "PacketIn")
                connection.getNettyServer().getPacketManager().getPacketClassByName(newName)
            }
            packetName.startsWith("PacketIn") -> {
                val newName = packetName.replaceFirst("PacketIn", "PacketOut")
                connection.getNettyServer().getPacketManager().getPacketClassByName(newName)
            }
            else ->  connection.getNettyServer().getPacketManager().getPacketClassByName(packetName)
        }
        packetClass ?: return -1

        val idFromPacket = connection.getNettyServer().getPacketManager().getIdFromPacket(packetClass)
        if (idFromPacket != null) {
            return idFromPacket
        }
        return -1

    }
}