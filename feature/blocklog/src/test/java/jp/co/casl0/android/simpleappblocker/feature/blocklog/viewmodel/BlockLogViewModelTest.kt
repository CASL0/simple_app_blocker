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

package jp.co.casl0.android.simpleappblocker.feature.blocklog.viewmodel

import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import jp.co.casl0.android.simpleappblocker.feature.FakeBlockedPacketsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BlockLogViewModelTest {
    private lateinit var blockedPacketsRepository: FakeBlockedPacketsRepository

    @Before
    fun setup() {
        // viewModelScope向けにmainディスパッチャを変更
        Dispatchers.setMain(UnconfinedTestDispatcher())

        blockedPacketsRepository = FakeBlockedPacketsRepository()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateUiState_whenInsertBlockedPackets() = runTest {
        val viewModel =
            BlockLogViewModel(blockedPacketsRepository)

        var resultUiState = UiState.BlockLogUiState()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect {
                resultUiState = it
            }
        }

        val data = DomainBlockedPacket(
            packageName = "package1",
            appName = "app1",
            srcAddress = "10.10.10.10",
            srcPort = 100,
            dstAddress = "20.20.20.20",
            dstPort = 200,
            protocol = "protocol1",
            blockedAt = "2000-01-01"
        )

        blockedPacketsRepository.insertBlockedPacket(data)

        assertThat(
            resultUiState.blockedApps, `is`(
                listOf(
                    UiState.BlockedApp(
                        appName = data.appName,
                        packageName = data.packageName,
                        src = data.srcAddressAndPort,
                        dst = data.dstAddressAndPort,
                        protocol = data.protocol,
                        blockedAt = data.blockedAt
                    )
                )
            )
        )

        job.cancel()
    }
}
