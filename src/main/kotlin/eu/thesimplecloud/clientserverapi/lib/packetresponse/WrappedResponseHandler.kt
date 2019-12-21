package eu.thesimplecloud.clientserverapi.lib.packetresponse

import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packetresponse.responsehandler.IPacketResponseHandler


data class WrappedResponseHandler<T : Any>(var packetResponseHandler: IPacketResponseHandler<T>, val communicationPromise: ICommunicationPromise<T>) {

}

