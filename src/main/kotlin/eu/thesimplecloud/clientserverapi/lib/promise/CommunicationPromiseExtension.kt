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

package eu.thesimplecloud.clientserverapi.lib.promise


fun ICommunicationPromise<Unit>.completeWhenAllCompleted(promises: List<ICommunicationPromise<*>>): ICommunicationPromise<Unit> {
    if (promises.isEmpty()) {
        trySuccess(Unit)
        return this
    }
    promises[0].combineAll(promises.drop(1)).addResultListener { this.trySuccess(Unit) }
    return this
}

fun Collection<ICommunicationPromise<*>>.combineAllPromises(): ICommunicationPromise<Unit> {
    return CommunicationPromise.combineAllToUnitPromise(this)
}

/**
 * Returns a new promise that will complete when the inner promise completes.
 * The new promise will complete with the same specifications.
 */
fun <T : Any> ICommunicationPromise<out ICommunicationPromise<T>>.flatten(additionalTimeout: Long = 0, timeoutEnabled: Boolean = true): ICommunicationPromise<T> {
    val enableTimeout = this.isTimeoutEnabled() && timeoutEnabled
    val newPromise = CommunicationPromise<T>(this.getTimeout() + additionalTimeout, enableTimeout)
    this.addCompleteListener {
        if (it.isSuccess) {
            val innerPromise = it.get()
            newPromise.copyStateFromOtherPromise(innerPromise)
        } else {
            newPromise.tryFailure(it.cause())
        }
    }
    return newPromise
}

/**
 * Returns a [ICommunicationPromise] with a list. The returned promise will complete when all promises in this list are completed.
 */
fun <T : Any> List<ICommunicationPromise<T>>.toListPromise(): ICommunicationPromise<List<T?>> {
    return this.combineAllPromises().then { this.map { it.getNow() } }
}

/**
 * Returns a [ICommunicationPromise] with a list. The returned promise will complete when all promises in this list are completed.
 * Note: if the expected list is large it is recommended to increase the [additionalTimeout]. Otherwise the returned promise will just time out.
 */
fun <T : Any> ICommunicationPromise<List<ICommunicationPromise<T>>>.toListPromise(additionalTimeout: Long = 400): ICommunicationPromise<List<T?>> {
    return this.then { list -> list.toListPromise()  }.flatten(additionalTimeout)
}

/*
* Copies the information of the specified promise to this promise when the specified promise completes.
*/
fun <T: Any> ICommunicationPromise<Unit>.copyStateFromOtherPromiseToUnitPromise(otherPromise: ICommunicationPromise<T>) {
    otherPromise.addCompleteListener {
        if (it.isSuccess) {
            this.trySuccess(Unit)
        } else {
            this.tryFailure(it.cause())
        }
    }
}