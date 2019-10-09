package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.packet.connectionpromise.IConnectionPromise

interface INettyClient : IConnection, ICommunicationBootstrap {

    override fun getCommunicationBootstrap(): INettyClient

    override fun <T> newPromise(): IConnectionPromise<T> {
        return super<IConnection>.newPromise()
    }

}