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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import jp.co.casl0.android.simpleappblocker.alllowlsit.AllowlistRepository
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewRuleViewModel(private val allowlistRepository: AllowlistRepository) : ViewModel() {

    /**
     * 許可済みパッケージリスト
     */
    val allowlist = allowlistRepository.allowlist

    /**
     * インストール済みパッケージ一覧
     */
    private val _installedPackages = mutableStateListOf<AppPackage>()
    val installedPackages: List<AppPackage>
        get() = _installedPackages

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
    suspend fun loadInstalledPackages(context: Context?) =
        withContext(Dispatchers.Main) {
            context?.packageManager?.also { pm ->
                pm.getInstalledApplications(0).forEach { appInfo ->
                    val notInList =
                        installedPackages.find { it.packageName == appInfo.packageName } == null
                    if (notInList) {
                        _installedPackages.add(
                            AppPackage(
                                appInfo.loadIcon(pm),
                                appInfo.loadLabel(pm).toString(),
                                appInfo.packageName,
                            )
                        )
                    }
                }
            }
        }

    /**
     * 許可アプリを変更する関数
     */
    val createNewRule: (AppPackage) -> Unit = { appPackage: AppPackage ->
        viewModelScope.launch {
            val currentList = allowlist.first()
            if (!currentList.contains(appPackage.packageName)) {
                allowlistRepository.insertAllowedPackage(
                    appPackage.packageName!!,
                    appPackage.appName!!
                )
            }
        }
    }
}

class NewRuleViewModelFactory(private val repository: AllowlistRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewRuleViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NewRuleViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}