package eu.thesimplecloud.clientserverapi.lib.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.IPacketResponseHandler


data class WrappedResponseHandler<T : Any>(var packetResponseHandler: IPacketResponseHandler<T>, val communicationPromise: ICommunicationPromise<T>) {

}

