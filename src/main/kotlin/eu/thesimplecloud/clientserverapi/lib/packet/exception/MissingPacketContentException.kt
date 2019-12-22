package eu.thesimplecloud.clientserverapi.lib.packet.exception

import java.lang.Exception

/**
 * This exception is returned as response when a packet does not contain required content.
 */
class MissingPacketContentException(unavailableProperty: String) : Exception("Cannot find required content: $unavailableProperty") {
}