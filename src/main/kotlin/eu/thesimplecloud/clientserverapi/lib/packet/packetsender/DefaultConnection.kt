package eu.thesimplecloud.clientserverapi.lib.packet.packetsender

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.IPacket
import eu.thesimplecloud.clientserverapi.lib.packet.PacketData
import eu.thesimplecloud.clientserverapi.lib.packetmanager.PacketManager
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.exception.PacketException
import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.IPacketPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.PacketPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.PacketResponseManager
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.WrappedResponseHandler
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Consumer

abstract class DefaultConnection(val packetManager: PacketManager, val packetResponseManager: PacketResponseManager) : IConnection {

    override fun <T : Any> sendQuery(packet: IPacket, packetResponseFunction: (IPacket) -> T?): IPacketPromise<T> {
        return sendQuery(packet, object : IPacketResponseHandler<T> {
            override fun handleResponse(packet: IPacket): T? {
                return packetResponseFunction(packet)
            }
        })
    }

    override fun <T : Any> sendQuery(packet: IPacket, packetResponseHandler: IPacketResponseHandler<T>): IPacketPromise<T> {
        val uniqueId = UUID.randomUUID()
        val packetPromise = PacketPromise<T>(this)
        packetResponseManager.registerResponseHandler(uniqueId, WrappedResponseHandler(packetResponseHandler, packetPromise))
        val idFromPacket = packetManager.getIdFromPacket(packet)
        if (idFromPacket == null) {
            GlobalScope.launch {
                val packetIdCompletableFuture = packetManager.getPacketIdCompletableFuture(packet::class.java)
                try {
                    val id = packetIdCompletableFuture.get(1, TimeUnit.SECONDS)
                    sendPacket(WrappedPacket(PacketData(uniqueId, id), packet))
                } catch (ex: TimeoutException) {
                    throw PacketException("No id for packet ${packet::class.java.simpleName} was available after one second. It looks like this packet was not registered.")
                }
            }
        } else {
            this.sendPacket(WrappedPacket(PacketData(uniqueId, idFromPacket), packet))
        }
        return packetPromise
    }

    /**
     * Sends a packet
     * Can be overridden to prevent packet sending when the connection is not open yet.
     */
    open fun sendPacket(wrappedPacket: WrappedPacket) {
        if (!isOpen()) throw IOException("Connection is not open.")
        getChannel()?.writeAndFlush(wrappedPacket)?.syncUninterruptibly()
    }

}