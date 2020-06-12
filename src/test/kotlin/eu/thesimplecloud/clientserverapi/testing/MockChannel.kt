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

package eu.thesimplecloud.clientserverapi.testing

import io.netty.channel.Channel
import io.netty.channel.DefaultChannelPromise
import io.netty.channel.embedded.EmbeddedChannel
import io.netty.util.concurrent.GlobalEventExecutor
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.Mockito.spy


/**
 * Created by IntelliJ IDEA.
 * Date: 11.06.2020
 * Time: 18:16
 * @author Frederick Baier
 */
class MockChannel {

    fun newPromise(channel: Channel): DefaultChannelPromise {
        return DefaultChannelPromise(channel, GlobalEventExecutor.INSTANCE)
    }

    fun mockChannel() {
        val channel = spy(EmbeddedChannel())
        `when`(channel.writeAndFlush(ArgumentMatchers.any())).then {
            println("writing" + it.arguments.first())
            return@then newPromise(channel)
        }
        `when`(channel.writeInbound())
    }

}