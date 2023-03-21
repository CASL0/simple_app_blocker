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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.casl0.android.simpleappblocker.core.data.repository.AllowlistRepository
import jp.co.casl0.android.simpleappblocker.core.data.repository.InstalledApplicationRepository
import jp.co.casl0.android.simpleappblocker.core.model.AppPackage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState {
    val searchValue: String
    val isRefreshing: Boolean
    val showedSearchBox: Boolean

    data class RuleChangeUiState(
        /** 検索ボックスの入力値 */
        override val searchValue: String = "",
        /** インストール済みアプリ読み込み中フラグ */
        override val isRefreshing: Boolean = false,
        /** 検索ボックス表示フラグ */
        override val showedSearchBox: Boolean = false
    ) : UiState
}

@HiltViewModel
class RuleChangeViewModel @Inject constructor(
    private val allowlistRepository: AllowlistRepository,
    private val installedApplicationRepository: InstalledApplicationRepository
) :
    ViewModel() {

    /** UI状態 */
    private val _uiState = MutableStateFlow(UiState.RuleChangeUiState())
    val uiState: StateFlow<UiState.RuleChangeUiState> get() = _uiState

    /** 許可済みパッケージリスト */
    val allowlist = allowlistRepository.allowlist

    /** インストール済みパッケージ一覧 */
    val installedApplications =
        combine(
            installedApplicationRepository.installedApplications,
            allowlist
        ) { allInstalledApps, allowlist ->
            allInstalledApps.map {
                it.copy(isAllowed = allowlist.contains(it.packageName))
            }
        }

    init {
        refreshInstalledApplications()
    }

    /** 検索ボックスの入力値変更のイベントハンドラ */
    fun onSearchValueChange(newValue: String) {
        _uiState.update { it.copy(searchValue = newValue) }
    }

    /** 検索ボタンクリック時のイベントハンドラ */
    fun onClickSearch() {
        _uiState.update { it.copy(showedSearchBox = true) }
    }

    /** インストール済みパッケージを読み込む関数 */
    fun refreshInstalledApplications() {
        Logger.d("refresh installed applications")
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            installedApplicationRepository.refresh()
            _uiState.update { it.copy(isRefreshing = false) }
        }
    }

    /** 許可・拒否を切り替える関数 */
    fun changeFilterRule(
        allow: Boolean,
        appPackage: AppPackage
    ) {
        viewModelScope.launch {
            if (allow) {
                allowlistRepository.insertAllowedPackage(
                    appPackage.packageName,
                    appPackage.appName
                )
            } else {
                allowlistRepository.disallowPackage(appPackage.packageName)
            }
        }
    }
}
