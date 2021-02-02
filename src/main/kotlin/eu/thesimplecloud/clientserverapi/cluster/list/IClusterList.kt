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

package eu.thesimplecloud.clientserverapi.cluster.list

import eu.thesimplecloud.clientserverapi.cluster.list.adapter.IClusterListAdapter
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 20:56
 * @author Frederick Baier
 */
interface IClusterList<T : IClusterListItem> {

    /**
     * Adds an element to the list
     * @param element the element to be added
     */
    fun addElement(element: T, fromPacket: Boolean = false)

    /**
     * Removes the element found by the specified [identifier]
     * @param identifier the unique identifier to find the element to remove
     */
    fun removeElement(identifier: Any, fromPacket: Boolean = false)

    /**
     * Sends the changes to the other nodes
     */
    fun updateElement(cachedValue: T)

    /**
     * Applies changes received by another node
     */
    fun applyUpdate(json: JsonLib)

    /**
     * Returns all elements
     */
    fun getAllElements(): List<T>

    fun getArrayClass(): Class<Array<Any>>

    /**
     * Adds an adapter to the list to listen for changes
     */
    fun addAdapter(adapter: IClusterListAdapter<T>)

    /**
     * Removes the specified [adapter]
     */
    fun removeAdapter(adapter: IClusterListAdapter<T>)

}