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

package eu.thesimplecloud.clientserverapi.node.list

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.auth.impl.SecretAuthProvider
import eu.thesimplecloud.clientserverapi.cluster.factory.DefaultClusterFactory
import eu.thesimplecloud.clientserverapi.lib.factory.BootstrapFactoryGetter
import eu.thesimplecloud.clientserverapi.lib.util.Address
import eu.thesimplecloud.clientserverapi.node.TestListObj
import org.junit.*

/**
 * Created by IntelliJ IDEA.
 * Date: 02/02/2021
 * Time: 18:00
 * @author Frederick Baier
 */
class ClusterListTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            BootstrapFactoryGetter.setEnvironment(BootstrapFactoryGetter.ApplicationEnvironment.TEST)
        }
    }

    private lateinit var clusterOne: ICluster
    private lateinit var clusterTwo: ICluster

    @Before
    fun setUp() {
        clusterOne = DefaultClusterFactory().createNewCluster(
            "1.0",
            SecretAuthProvider("123"),
            Address("127.0.0.1", 1504)
        )
        clusterTwo = DefaultClusterFactory().joinCluster(
            "1.0", SecretAuthProvider("123"),
            Address("127.0.0.1", 1505),
            Address("127.0.0.1", 1504)
        )
    }

    @After
    fun after() {
        if (this::clusterOne.isInitialized)
            clusterOne.shutdown().syncUninterruptibly()
        if (this::clusterTwo.isInitialized)
            clusterTwo.shutdown().syncUninterruptibly()
    }

    @Test
    fun newClusterList_IsEmpty() {
        val clusterList = clusterTwo.getClusterListManager()
            .getClusterListByNameOrCreate<TestListObj>("test", Array<TestListObj>::class.java)
        Assert.assertTrue(clusterList.getAllElements().isEmpty())
    }

    @Test
    fun afterElementAdded_OtherClusterCanSeeElement() {
        val clusterList = clusterTwo.getClusterListManager()
            .getClusterListByNameOrCreate<TestListObj>("test", Array<TestListObj>::class.java)
        clusterList.addElement(TestListObj("one", 45)).syncUninterruptibly()
        val clusterListOther = clusterTwo.getClusterListManager().getClusterListByName<TestListObj>("test")!!
        val first = clusterListOther.getAllElements().first()
        Assert.assertEquals("one", first.getIdentifier())
        Assert.assertEquals(45, first.number)
    }

    @Test
    fun afterElementAddedAndRemoved_ElementIsGone() {
        val clusterList = clusterTwo.getClusterListManager()
            .getClusterListByNameOrCreate<TestListObj>("test", Array<TestListObj>::class.java)
        clusterList.addElement(TestListObj("one", 45)).syncUninterruptibly()
        clusterList.removeElement("one").syncUninterruptibly()
        val clusterListOther = clusterTwo.getClusterListManager().getClusterListByName<TestListObj>("test")!!
        Assert.assertTrue(clusterList.getAllElements().isEmpty())
        Assert.assertTrue(clusterListOther.getAllElements().isEmpty())
    }

    @Test
    fun afterElementAddedAndUpdated_OtherNodeCanSee() {
        val clusterList = clusterTwo.getClusterListManager()
            .getClusterListByNameOrCreate<TestListObj>("test", Array<TestListObj>::class.java)
        val element = TestListObj("one", 45)
        clusterList.addElement(element).syncUninterruptibly()

        element.number = 20
        clusterList.updateElement(element).syncUninterruptibly()

        val clusterListOther = clusterTwo.getClusterListManager().getClusterListByName<TestListObj>("test")!!
        val first = clusterListOther.getAllElements().first()
        Assert.assertEquals(20, first.number)
    }

}