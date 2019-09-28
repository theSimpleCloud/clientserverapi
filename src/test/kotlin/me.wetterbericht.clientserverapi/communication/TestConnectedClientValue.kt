package me.wetterbericht.clientserverapi.communication

import eu.thesimplecloud.clientserverapi.server.client.connectedclient.IConnectedClientValue

import kotlin.random.Random

class TestConnectedClientValue : IConnectedClientValue {

    val number = Random.nextInt(0, 100)

}