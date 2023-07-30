/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.casl0.android.simpleappblocker.core.data.repository

import jp.co.casl0.android.simpleappblocker.core.data.datasource.FakeBlockedPacketsLocalDataSource
import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultBlockedPacketsRepositoryTest {
    private lateinit var blockedPackets: List<DomainBlockedPacket>
    private lateinit var blockedPacketsDataSource: FakeBlockedPacketsLocalDataSource

    @Before
    fun setup() {
        val blockedPacket1 =
            DomainBlockedPacket(
                packageName = "package1",
                srcAddress = "10.10.10.1",
                srcPort = 1111,
                dstAddress = "100.100.100.1",
                dstPort = 11111,
                protocol = "http",
                blockedAt = "2000-01-01"
            )
        val blockedPacket2 =
            DomainBlockedPacket(
                packageName = "package2",
                srcAddress = "10.10.10.2",
                srcPort = 2222,
                dstAddress = "100.100.100.2",
                dstPort = 22222,
                protocol = "http",
                blockedAt = "2000-02-02"
            )
        val blockedPacket3 =
            DomainBlockedPacket(
                packageName = "package3",
                srcAddress = "10.10.10.3",
                srcPort = 3333,
                dstAddress = "100.100.100.3",
                dstPort = 33333,
                protocol = "http",
                blockedAt = "2000-03-03"
            )
        blockedPackets = listOf(blockedPacket1, blockedPacket2, blockedPacket3)
        blockedPacketsDataSource = FakeBlockedPacketsLocalDataSource(blockedPackets)
    }

    @Test
    fun insertBlockedPacket_addNewBlockedPackage() = runTest {
        val newBlockedPackage = DomainBlockedPacket(
            packageName = "newPackage",
            srcAddress = "10.10.10.4",
            srcPort = 4444,
            dstAddress = "100.100.100.4",
            dstPort = 44444,
            protocol = "http",
            blockedAt = "2000-04-04"
        )
        val blockedPacketsRepository =
            DefaultBlockedPacketsRepository(
                blockedPacketsDataSource,
                UnconfinedTestDispatcher(testScheduler)
            )

        blockedPacketsRepository.insertBlockedPacket(newBlockedPackage)
        assertThat(
            blockedPacketsRepository.getBlockedPacketsStream().first(),
            `is`(blockedPackets + newBlockedPackage)
        )
    }
}
