package eu.thesimplecloud.clientserverapi.lib.connection

import io.netty.channel.Channel
import eu.thesimplecloud.clientserverapi.lib.packet.WrappedPacket
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import java.io.IOException

interface IConnection : IPacketSender {

    /**
     * Returns the channel of this connection or null if the connection is not connected.
     * @return the channel or null if the connection is not connected.
     */
    fun getChannel(): Channel?

    /**
     * Returns weather the connection is open
     * @return true if the connection is open
     */
    fun isOpen(): Boolean {
        return getChannel() != null && getChannel()?.isActive ?: false
    }


}