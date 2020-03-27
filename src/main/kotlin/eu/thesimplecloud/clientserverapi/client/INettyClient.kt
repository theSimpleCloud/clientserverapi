package eu.thesimplecloud.clientserverapi.client

import eu.thesimplecloud.clientserverapi.lib.bootstrap.ICommunicationBootstrap
import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise

interface INettyClient : IConnection, ICommunicationBootstrap {

    override fun getCommunicationBootstrap(): INettyClient


}