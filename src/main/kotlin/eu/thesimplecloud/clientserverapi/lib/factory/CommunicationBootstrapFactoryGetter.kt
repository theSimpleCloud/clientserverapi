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

package eu.thesimplecloud.clientserverapi.lib.factory

/**
 * Created by IntelliJ IDEA.
 * Date: 25.09.2020
 * Time: 19:17
 * @author Frederick Baier
 */
object CommunicationBootstrapFactoryGetter {

    @Volatile
    private var factory: ICommunicationBootstrapFactory? = null

    /**
     * Sets the environment to work in
     */
    fun setEnvironment(environment: ApplicationEnvironment): CommunicationBootstrapFactoryGetter {
        this.factory = when (environment) {
            ApplicationEnvironment.NORMAL -> NormalCommunicationBootstrapFactory()
            ApplicationEnvironment.TEST -> TestCommunicationBootstrapFactory()
        }
        return this
    }

    /**
     * Returns the factory if set.
     * @throw [IllegalStateException] if the factory was not set
     */
    @Throws(IllegalStateException::class)
    fun getFactory(): ICommunicationBootstrapFactory {
        return this.factory ?: throw IllegalStateException("Factory is not set")
    }

    enum class ApplicationEnvironment {

        TEST, NORMAL

    }

}