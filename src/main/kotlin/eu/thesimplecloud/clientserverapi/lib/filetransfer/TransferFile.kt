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

package eu.thesimplecloud.clientserverapi.lib.filetransfer

import org.apache.commons.io.FileUtils
import java.io.File
import java.io.FileNotFoundException
import java.util.*

class TransferFile(val uuid: UUID, val lastModified: Long, savePath: String) {

    val file = File(savePath)

    init {
        file.delete()
        file.parentFile?.mkdirs()
    }

    fun addBytes(byteArray: Array<Byte>) {
        while(true) {
            try {
                FileUtils.writeByteArrayToFile(file, byteArray.toByteArray(), true)
                break
            } catch (e: FileNotFoundException) {
                Thread.sleep(3)
            }
        }
    }

    fun setLasModified() {
        this.file.setLastModified(lastModified)
    }

}