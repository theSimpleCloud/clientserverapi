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

package eu.thesimplecloud.clientserverapi.lib.filetransfer.directory

import eu.thesimplecloud.clientserverapi.lib.connection.IConnection
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchManager
import java.io.File

class DirectorySyncManager(private val directoryWatchManager: IDirectoryWatchManager) : IDirectorySyncManager {

    private val directorySyncList: MutableList<DirectorySync> = ArrayList()
    @Volatile
    var tmpZipDir: File = File("tmpZip/")
        private set

    override fun createDirectorySync(directory: File, toDirectory: String): IDirectorySync {
        val directorySync = DirectorySync(directory, toDirectory, this.tmpZipDir, this.directoryWatchManager.createDirectoryWatch(directory))
        directorySyncList.add(directorySync)
        return directorySync
    }

    override fun deleteDirectorySync(directory: File) {
        val directorySync = getDirectorySync(directory)
        if (directorySync !is DirectorySync) return
        this.directorySyncList.remove(directorySync)
    }

    override fun getDirectorySync(directory: File): IDirectorySync? = this.directorySyncList.firstOrNull { it.getDirectory() == directory }

    override fun setTmpZipDirectory(tmpZipDir: File) {
        if (this.directorySyncList.isNotEmpty()) throw IllegalStateException("There must be no registered DirectorySyncs when calling this")
        this.tmpZipDir = tmpZipDir
    }

    fun removeFromDirectorySyncs(connection: IConnection) {
        directorySyncList.forEach { it.syncNoLonger(connection) }
    }
}