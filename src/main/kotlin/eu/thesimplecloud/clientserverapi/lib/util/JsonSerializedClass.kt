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

import eu.thesimplecloud.clientserverapi.lib.json.PacketExclude
import eu.thesimplecloud.jsonlib.JsonLib
import eu.thesimplecloud.jsonlib.JsonLibExclude

/**
 * Created by IntelliJ IDEA.
 * Date: 01/02/2021
 * Time: 08:25
 * @author Frederick Baier
 */
class JsonSerializedClass<T : Any>(
    value: T
) {

    @Volatile
    @JsonLibExclude
    @PacketExclude
    private var value: T? = value

    @Volatile
    private var jsonLib: JsonLib = JsonLib.fromObject(value)

    @Volatile
    private var className: String = value::class.java.name

    fun setValue(value: T) {
        this.value = value
        this.jsonLib = JsonLib.fromObject(value)
        this.className = value::class.java.name
    }

    fun getValue(): T {
        if (this.value == null) {
            this.value = jsonLib.getObject(Class.forName(className)) as T
        }
        return this.value!!
    }

}