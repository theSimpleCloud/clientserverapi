package eu.thesimplecloud.clientserverapi.server.packets

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClient
class PacketInGetPacketId : ObjectPacket<ArrayList<String>>() {

    override suspend fun handle(connection: IConnection): ICommunicationPromise<ArrayList<Int>> {
        connection as IConnectedClient<*>
        val packetNames = this.value ?: return contentException("value")
        val response = ArrayList<Int>()
        for (packetName in packetNames) {
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
            if (packetClass == null) {
                response.add(-1)
                continue
            }

            val idFromPacket = connection.getNettyServer().getPacketManager().getIdFromPacket(packetClass)
            if (idFromPacket != null) {
                response.add(idFromPacket)
            }
        }

        return success(response)

    }
}