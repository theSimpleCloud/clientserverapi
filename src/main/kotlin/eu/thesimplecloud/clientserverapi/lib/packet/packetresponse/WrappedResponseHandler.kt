package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler

class WrappedResponseHandler<T : Any>(val packetResponseHandler: IPacketResponseHandler<T>, val communicationPromise: ICommunicationPromise<T>)

