package eu.thesimplecloud.clientserverapi.server.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
class PacketInGetPacketId : eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket<String>(String::class.java) {

    override suspend fun handle(connection: IConnection): IPacket? {
        connection as IConnectedClient<*>
        val packetName = this.value ?: return null
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
        packetClass ?: return null

        val idFromPacket = connection.getNettyServer().getPacketManager().getIdFromPacket(packetClass)
        if (idFromPacket != null) {
            return getNewObjectPacketWithContent(idFromPacket)
        }
        return null

    }
}