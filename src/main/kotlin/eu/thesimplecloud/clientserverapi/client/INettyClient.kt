package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.communicationpromise.ICommunicationPromise

interface INettyClient : IConnection, ICommunicationBootstrap {

    override fun getCommunicationBootstrap(): INettyClient

    fun getPacketIdsSyncPromise(): ICommunicationPromise<Unit>

}