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

package jp.co.casl0.android.simpleappblocker.feature.rule_change.viewmodel

import jp.co.casl0.android.simpleappblocker.FakeAllowlistRepository
import jp.co.casl0.android.simpleappblocker.SpyInstalledApplicationRepository
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
class RuleChangeViewModelTest {

    private lateinit var allowlistRepository: FakeAllowlistRepository
    private lateinit var installedApplicationRepository: SpyInstalledApplicationRepository

    @Before
    fun setup() {
        // viewModelScope向けにmainディスパッチャを変更
        Dispatchers.setMain(UnconfinedTestDispatcher())

        allowlistRepository = FakeAllowlistRepository(UnconfinedTestDispatcher())
        installedApplicationRepository =
            SpyInstalledApplicationRepository(UnconfinedTestDispatcher())
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onSearchValueChange_newValue_uiStateUpdated() = runTest {
        val viewModel = RuleChangeViewModel(
            allowlistRepository,
            installedApplicationRepository
        )
        var resultUiState = UiState.RuleChangeUiState()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect {
                resultUiState = it
            }
        }

        assertThat(resultUiState.searchValue, `is`(""))
        viewModel.onSearchValueChange("new value")
        assertThat(resultUiState.searchValue, `is`("new value"))

        job.cancel()
    }

    @Test
    fun onClickSearch_showedSearchBox() = runTest {
        val viewModel = RuleChangeViewModel(
            allowlistRepository,
            installedApplicationRepository
        )
        var resultUiState = UiState.RuleChangeUiState()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect {
                resultUiState = it
            }
        }

        assertThat(resultUiState.showedSearchBox, `is`(false))
        viewModel.onClickSearch()
        assertThat(resultUiState.showedSearchBox, `is`(true))

        job.cancel()
    }

    @Test
    fun refreshInstalledApplications() {
        // TODO: implement
    }

    @Test
    fun changeFilterRule() {
        // TODO: implement
    }
}
