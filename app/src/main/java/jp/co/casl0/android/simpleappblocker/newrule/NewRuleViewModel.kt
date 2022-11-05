/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.casl0.android.simpleappblocker.newrule

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.repository.AllowlistRepository
import jp.co.casl0.android.simpleappblocker.repository.InstalledApplicationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewRuleViewModel @Inject constructor(
    private val allowlistRepository: AllowlistRepository,
    private val installedApplicationRepository: InstalledApplicationRepository
) :
    ViewModel() {

    init {
        refreshInstalledApplications()
    }

    /**
     * 許可済みパッケージリスト
     */
    val allowlist = allowlistRepository.allowlist

    /**
     * インストール済みパッケージ一覧
     */
    val installedApplications = installedApplicationRepository.installedApplications

    /**
     * 検索ボックスの入力値
     */
    private var _searchValue by mutableStateOf("")
    val searchValue get() = _searchValue

    /**
     * 検索ボックスの入力値変更のイベントハンドラ
     */
    val onSearchValueChange = { newValue: String ->
        _searchValue = newValue
    }

    /**
     * インストール済みパッケージを読み込む関数
     */
    private fun refreshInstalledApplications() {
        viewModelScope.launch {
            installedApplicationRepository.refresh()
        }
    }

    /**
     * 許可アプリを変更する関数
     */
    val createNewRule: (Context, AppPackage) -> Unit = { context, appPackage: AppPackage ->
        viewModelScope.launch {
            val currentList = allowlist.first()
            if (!currentList.contains(appPackage.packageName)) {
                allowlistRepository.insertAllowedPackage(
                    appPackage.packageName,
                    appPackage.appName
                )
            }
        }
        Toast.makeText(
            context,
            String.format(context.getString(R.string.toast_allow_app), appPackage.appName),
            Toast.LENGTH_SHORT
        ).show()
    }
}
