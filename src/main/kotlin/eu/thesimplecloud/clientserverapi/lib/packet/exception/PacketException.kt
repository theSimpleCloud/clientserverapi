package eu.thesimplecloud.clientserverapi.lib.packet.exception

import java.lang.Exception

class PacketException : Exception {

    constructor()

    constructor(message: String) : super(message)

    constructor(message: String, cause: Exception) : super(message, cause)

}