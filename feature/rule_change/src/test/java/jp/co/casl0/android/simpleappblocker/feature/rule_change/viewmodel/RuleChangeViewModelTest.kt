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

import android.graphics.drawable.Drawable
import jp.co.casl0.android.simpleappblocker.FakeAllowlistRepository
import jp.co.casl0.android.simpleappblocker.SpyInstalledApplicationRepository
import jp.co.casl0.android.simpleappblocker.core.model.AppPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

@OptIn(ExperimentalCoroutinesApi::class)
class RuleChangeViewModelTest {

    private lateinit var allowlistRepository: FakeAllowlistRepository
    private lateinit var installedApplicationRepository: SpyInstalledApplicationRepository

    private lateinit var fakeInstalledApps: List<AppPackage>

    @Before
    fun setup() {
        // viewModelScope向けにmainディスパッチャを変更
        Dispatchers.setMain(UnconfinedTestDispatcher())


        fakeInstalledApps = listOf(
            AppPackage(
                icon = mock(Drawable::class.java),
                appName = "App1",
                packageName = "app1"
            ),
            AppPackage(
                icon = mock(Drawable::class.java),
                appName = "App2",
                packageName = "app2"
            )
        )

        allowlistRepository = FakeAllowlistRepository(UnconfinedTestDispatcher())
        installedApplicationRepository =
            SpyInstalledApplicationRepository(
                fakeInstalledApps = fakeInstalledApps,
                dispatcher = UnconfinedTestDispatcher()
            )
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
    fun refreshInstalledApplications_exclusiveCallingRepository() = runTest {
        val viewModel = RuleChangeViewModel(
            allowlistRepository,
            installedApplicationRepository
        )
        // コンストラクション時のrefreshコルーチンを再開
        advanceTimeBy(101)
        assertThat(installedApplicationRepository.refreshCallCount, `is`(1))

        viewModel.refreshInstalledApplications()
        viewModel.refreshInstalledApplications()

        advanceTimeBy(101)
        assertThat(installedApplicationRepository.refreshCallCount, `is`(2))
    }

    @Test
    fun changeFilterRule_installedApplicationsUpdated() = runTest {
        val viewModel = RuleChangeViewModel(
            allowlistRepository,
            installedApplicationRepository
        )

        var resultApps = listOf<AppPackage>()
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.installedApplications.collect {
                // 許可済みのアプリをフィルター
                resultApps = it
            }
        }

        val app1 = fakeInstalledApps[0]
        assertThat(resultApps.count { it.isAllowed }, `is`(0))

        // App1を許可に変更
        viewModel.changeFilterRule(allow = true, appPackage = app1)
        assertThat(resultApps.count { it.isAllowed }, `is`(1))
        assertThat(resultApps.first { it.isAllowed }.packageName, `is`(app1.packageName))

        // App1を拒否に変更
        viewModel.changeFilterRule(allow = false, appPackage = app1)
        assertThat(resultApps.count { it.isAllowed }, `is`(0))

        job.cancel()
    }
}
