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

package eu.thesimplecloud.clientserverapi.lib.util

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream


class ZipUtils {

    companion object {

        fun zipFiles(zipFile: File, files: List<File>, subtractRelativePath: String = "") {
            try { // create byte buffer
                val buffer = ByteArray(1024)
                val fos = FileOutputStream(zipFile)
                val zos = ZipOutputStream(fos)
                for (i in files.indices) {
                    val srcFile = files[i]
                    val fis = FileInputStream(srcFile)
                    // begin writing a new ZIP entry, positions the stream to the start of the entry data
                    zos.putNextEntry(ZipEntry(srcFile.path.replaceFirst(subtractRelativePath, "")))
                    var length: Int
                    while (fis.read(buffer).also { length = it } > 0) {
                        zos.write(buffer, 0, length)
                    }
                    zos.closeEntry()
                    // close the InputStream
                    fis.close()
                }
                // close the ZipOutputStream
                zos.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }
        }

        @Throws(IOException::class)
        fun zipDir(fileToZip: File, zipFile: File) {
            val fos = FileOutputStream(zipFile)
            val zipOut = ZipOutputStream(fos)
            zipFile(null, fileToZip, fileToZip.name, zipOut)
            zipOut.close()
            fos.close()
        }

        @Throws(IOException::class)
        fun zipDirFiles(fileToZip: File, files: List<File>, zipFile: File) {
            val fos = FileOutputStream(zipFile)
            val zipOut = ZipOutputStream(fos)
            zipFile(files, fileToZip, fileToZip.name, zipOut)
            zipOut.close()
            fos.close()
        }

        @Throws(IOException::class)
        private fun zipFile(files: List<File>?, fileToZip: File, fileName: String, zipOut: ZipOutputStream) {
            if (fileToZip.isHidden) {
                return
            }
            if (fileToZip.isDirectory) {
                val children = fileToZip.listFiles()
                val zipEntry = ZipEntry("$fileName/")
                zipOut.putNextEntry(zipEntry)
                for (childFile in children) {
                    if (childFile.isDirectory || files == null || files.contains(childFile)) zipFile(files, childFile, fileName + "/" + childFile.name, zipOut)
                }
                return
            }
            val fis = FileInputStream(fileToZip)
            val zipEntry = ZipEntry(fileName)
            zipOut.putNextEntry(zipEntry)
            val bytes = ByteArray(1024)
            var length: Int
            while (fis.read(bytes).also { length = it } >= 0) {
                zipOut.write(bytes, 0, length)
            }
            fis.close()
        }

        @Throws(IOException::class)
        fun unzipDir(zipFile: File, dirToUnzip: String) {
            val buffer = ByteArray(1024)
            val zis = ZipInputStream(FileInputStream(zipFile))
            var zipEntry = zis.nextEntry
            while (zipEntry != null) {
                val fileName = zipEntry.name
                val newFile = File("$dirToUnzip/$fileName")
                if (zipEntry.isDirectory) {
                    newFile.mkdirs()
                } else {
                    File(newFile.parent).mkdirs()
                    val fos = FileOutputStream(newFile)
                    var len: Int
                    while (zis.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                    fos.close()
                }
                zipEntry = zis.nextEntry
            }
            zis.closeEntry()
            zis.close()
        }
    }

}