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
import eu.thesimplecloud.clientserverapi.lib.defaultpackets.*
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatch
import eu.thesimplecloud.clientserverapi.lib.directorywatch.IDirectoryWatchListener
import eu.thesimplecloud.clientserverapi.lib.filetransfer.FileInfo
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.sendQuery
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.combineAllPromises
import eu.thesimplecloud.clientserverapi.lib.promise.flatten
import eu.thesimplecloud.clientserverapi.lib.util.ZipUtils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File
import java.io.IOException
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit

class DirectorySync(private val directory: File, toDirectory: String, private val tmpZipDir: File, directoryWatch: IDirectoryWatch) : IDirectorySync {

    private val receivers = CopyOnWriteArrayList<IConnection>()
    private val zipFile = File(tmpZipDir, directory.name + ".zip")
    private val toDirectory = toDirectory.replace("/", "\\")

    init {
        tmpZipDir.mkdirs()
        if (zipFile.exists())
            zipFile.delete()

        require(this.directory.isDirectory) { "Specified file must be a directory." }

        directoryWatch.addWatchListener(object : IDirectoryWatchListener {
            override fun fileCreated(file: File) {
                //normal files will be sent in [fileModified]
                GlobalScope.launch {
                    var count = 0
                    while (true) {
                        count++
                        try {
                            changesDetected()
                            receivers.forEach { connection -> sendFileOrDirectory(file, connection, true) }
                        } catch (e: Exception) {
                            delay(100)
                            if (count == 10 * 50 * 2) {
                                throw IOException(e)
                            }
                            continue
                            //ignore "file is in use by another process"
                        }
                        break
                    }
                }
            }

            override fun fileModified(file: File) {
                if (file.isDirectory) return
                try {
                    changesDetected()
                    receivers.forEach { connection -> sendFileOrDirectory(file, connection, false) }
                } catch (e: Exception) {
                    throw IOException(e)
                }
            }

            override fun fileDeleted(file: File) {
                changesDetected()
                val otherSidePath = getPathOnOtherSide(file)
                receivers.forEach { connection -> connection.sendUnitQuery(PacketIODeleteFile(otherSidePath)) }
            }

        })
    }

    private fun changesDetected() {
        synchronized(this) {
            this.zipFile.delete()
        }
    }

    override fun getDirectory(): File = this.directory

    override fun syncDirectory(connection: IConnection): ICommunicationPromise<Unit> {
        synchronized(this) {
            return CommunicationPromise.runAsync {
                this.receivers.add(connection)
                //only sync non dir files
                syncDirectories(connection)
                val allFiles = getAllFilesAndDirectories().filter { !it.isDirectory }
                val filesOnOtherSide = allFiles.map { file -> FileInfo(getPathOnOtherSide(file), file.lastModified()) }
                val neededFilesPromise = connection.sendQuery<Array<FileInfo>>(PacketIOSyncFiles(toDirectory, filesOnOtherSide), 5000)
                val neededFileInfoList = neededFilesPromise.getBlockingOrNull()
                        ?: return@runAsync CommunicationPromise.failed<Unit>(neededFilesPromise.cause())
                val neededFiles = neededFileInfoList.map { getFileFromOtherSidePath(it.relativePath) }
                if (shouldSendViaZip(allFiles, neededFiles)) {
                    return@runAsync sendFileAsZip(connection)
                }
                val filePromises = neededFiles.map { file -> sendFileOrDirectory(file, connection, false) }
                filePromises.combineAllPromises()
            }.flatten()
        }
    }

    private fun shouldSendViaZip(allFiles: List<File>, neededFiles: List<File>): Boolean {
        val allFilesSize = allFiles.map { it.length() }.sum()
        val neededFilesSize = neededFiles.map { it.length() }.sum()
        return (allFilesSize / 3) < neededFilesSize
    }

    private fun syncDirectories(connection: IConnection) {
        val allDirectories = getAllFilesAndDirectories().filter { it.isDirectory }
        val relativePathsOnOtherSide = allDirectories.map { getPathOnOtherSide(it) }
        connection.sendUnitQuery(PacketIOSyncDirectories(getToDirectoryPathWithoutEndingSlash(), relativePathsOnOtherSide), 5000).awaitUninterruptibly()
    }

    private fun sendFileAsZip(connection: IConnection): ICommunicationPromise<Unit> {
        //cleanup old dir
        connection.sendUnitQuery(PacketIODeleteFile(getToDirectoryPathWithoutEndingSlash())).awaitUninterruptibly()
        if (!zipFile.exists())
            zipDirectory()
        val promise = connection.sendFile(zipFile, tmpZipDir.path + "/C-" + zipFile.name, TimeUnit.MINUTES.toMillis(1))
        val sizeInMB = (zipFile.length() / 1000) / 1000
        val unzippedPromise = promise.then { connection.sendUnitQuery(PacketIOUnzipZipFile(tmpZipDir.path + "/C-" + zipFile.name, getToDirectoryPathWithoutEndingSlash()), (sizeInMB * 100) * 2) }.flatten()
        return unzippedPromise.then {
            val modifyPromises = getAllFilesAndDirectories().map { file -> connection.sendUnitQuery(PacketIOSetLastModified(FileInfo(getPathOnOtherSide(file), file.lastModified()))) }
            modifyPromises.combineAllPromises()
        }.flatten()
    }

    private fun getToDirectoryPathWithoutEndingSlash(): String {
        if (toDirectory.endsWith("\\") || toDirectory.endsWith("/")) {
            return toDirectory.dropLast(1)
        }
        return toDirectory
    }

    private fun sendFileOrDirectory(file: File, connection: IConnection, sendSubFilesOfDir: Boolean): ICommunicationPromise<Unit> {
        if (file.isDirectory) {
            val createDirPromise = connection.sendUnitQuery(PacketIOCreateDirectory(getPathOnOtherSide(file)))
            if (!sendSubFilesOfDir) return createDirPromise
            return createDirPromise.then {
                val files = file.listFiles() ?: emptyArray<File>()
                val promises = files.map { sendFileOrDirectory(it, connection, true) }
                promises.combineAllPromises()
            }.flatten()
        } else {
            val timeToWait = file.length() + 400
            return connection.sendFile(file, getPathOnOtherSide(file), timeToWait.toLong())
        }
    }

    override fun syncNoLonger(connection: IConnection) {
        this.receivers.remove(connection)
    }

    private fun getAllFilesAndDirectories(): Collection<File> {
        val acceptAllFilter = object : IOFileFilter {
            override fun accept(file: File?): Boolean {
                return true
            }

            override fun accept(dir: File?, name: String?): Boolean {
                return true
            }
        }
        //filter dir to sync from
        return FileUtils.listFilesAndDirs(directory, acceptAllFilter, acceptAllFilter).filter { it != this.directory }
    }

    private fun getPathOnOtherSide(file: File): String {
        val toDirectoryWithoutEndingSlash = getToDirectoryPathWithoutEndingSlash()
        return if (file.isDirectory) {
            file.path.replace(this.directory.path, toDirectoryWithoutEndingSlash)
        } else {
            file.path.replace(this.directory.path, toDirectoryWithoutEndingSlash)
        }
    }

    private fun getFileFromOtherSidePath(path: String) = File(path.replace(toDirectory, this.directory.path + "\\"))

    private fun zipDirectory() {
        synchronized(this) {
            ZipUtils.zipFiles(zipFile, getAllFilesAndDirectories().filter { !it.isDirectory }, subtractRelativePath = directory.path)
        }
    }

}