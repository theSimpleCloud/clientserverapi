package eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler

import eu.thesimplecloud.clientserverapi.lib.packet.IPacket

@FunctionalInterface
interface IPacketResponseHandler<T : Any> {

    companion object {
        private val UNIT_HANDLER = object: IPacketResponseHandler<Any> {
            override fun handleResponse(packet: IPacket): Any? = Unit
        }

        fun <T : Any> getUnitHandler() = UNIT_HANDLER as IPacketResponseHandler<T>
    }

    /**
     * Handles a packets sent as response and returns its content as an object.
     */
    fun handleResponse(packet: IPacket): T?

}