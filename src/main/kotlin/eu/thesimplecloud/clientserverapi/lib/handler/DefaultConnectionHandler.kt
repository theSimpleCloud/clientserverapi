/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package eu.thesimplecloud.clientserverapi.lib.handler

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.filetransfer.directory.DirectorySyncManager

open class DefaultConnectionHandler : IConnectionHandler {

    override fun onConnectionActive(connection: IConnection) {
        connection.setAuthenticated(true)
    }

    override fun onConnectionAuthenticated(connection: IConnection) {
        synchronizeSyncListsIfThisIsServer(connection)
    }

    private fun synchronizeSyncListsIfThisIsServer(connection: IConnection) {
        if (connection.getCommunicationBootstrap().isServer()) {
            synchronizeSyncLists(connection)
        }
    }

    private fun synchronizeSyncLists(connection: IConnection) {
        val syncListManager = connection.getCommunicationBootstrap().getClientServerSyncListManager()
        syncListManager.synchronizeAllWithPacketSender(connection)
    }

    override fun onFailure(connection: IConnection, ex: Throwable) {
        throw ex
    }

    override fun onConnectionInactive(connection: IConnection) {
        val directorySyncManager = connection.getCommunicationBootstrap().getDirectorySyncManager()
        directorySyncManager as DirectorySyncManager
        directorySyncManager.removeFromDirectorySyncs(connection)
    }

}