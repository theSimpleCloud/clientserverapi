package eu.thesimplecloud.clientserverapi.communication.testclasses

import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

import kotlin.random.Random

class TestConnectedClientValue : IConnectedClientValue {

    val number = Random.nextInt(0, 100)

}