package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler

data class WrappedResponseHandler<T : Any>(var packetResponseHandler: IPacketResponseHandler<T>, val communicationPromise: ICommunicationPromise<T>) {

}

