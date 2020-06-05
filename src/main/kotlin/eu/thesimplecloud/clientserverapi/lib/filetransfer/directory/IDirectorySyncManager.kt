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

import java.io.File

interface IDirectorySyncManager {

    /**
     * Creates a [IDirectorySync] with the specified parameters
     * @param directory the directory to sync from
     * @param toDirectory the directory on the other side to sync to
     */
    fun createDirectorySync(directory: File, toDirectory: String): IDirectorySync

    /**
     * Deletes the [IDirectorySync] found by the specified directory
     */
    fun deleteDirectorySync(directory: File)

    /**
     * Returns the [IDirectorySync] found by the specified directory
     */
    fun getDirectorySync(directory: File): IDirectorySync?

    /**
     * Sets the directory where zip files will be stored.
     * This function can only be called before creating the first [IDirectorySync]
     */
    fun setTmpZipDirectory(tmpZipDir: File)

}