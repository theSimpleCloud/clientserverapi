package eu.thesimplecloud.clientserverapi.lib.packet.packetresponse

import eu.thesimplecloud.clientserverapi.lib.packet.packetpromise.IPacketPromise
import eu.thesimplecloud.clientserverapi.lib.packet.packetresponse.responsehandler.IPacketResponseHandler

class WrappedResponseHandler<T : Any>(val packetResponseHandler: IPacketResponseHandler<T>, val packetPromise: IPacketPromise<T>)

