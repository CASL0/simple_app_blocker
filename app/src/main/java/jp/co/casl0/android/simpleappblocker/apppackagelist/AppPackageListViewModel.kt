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

package jp.co.casl0.android.simpleappblocker.apppackagelist

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import jp.co.casl0.android.simpleappblocker.PackageInfo
import jp.co.casl0.android.simpleappblocker.utilities.NetworkConnectivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AppPackageListViewModel(private val allowlistRepository: AllowlistRepository) : ViewModel() {

    /**
     * 許可済みパッケージリスト
     */
    val allowlist: LiveData<List<String>> = allowlistRepository.allowlist.asLiveData()

    /**
     * インストール済みパッケージリスト
     */
    private val _packageInfoList = mutableStateListOf<PackageInfo>()
    val packageInfoList: List<PackageInfo>
        get() = _packageInfoList

    /**
     * Manifest.permission.INTERNETが付与されているインストール済みパッケージを読み込む関数
     */
    suspend fun loadInstalledPackagesConnectingToNetwork(context: Context?) =
        withContext(Dispatchers.Main) {
            context?.packageManager?.also { pm ->
                pm.getInstalledApplications(0).forEach { appInfo ->
                    // Manifest.permission.INTERNETが付与されていないアプリは表示しない
                    val installed = packageInfoList.find { it.packageName == appInfo.packageName }
                    if (installed == null && NetworkConnectivity.hasInternetPermission(
                            context,
                            appInfo.packageName
                        )
                    ) {
                        _packageInfoList.add(
                            PackageInfo(
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
     * リストのアイテムクリック時のイベントハンドラ
     */
    val onCardClicked: (PackageInfo) -> Unit = { packageInfo ->
        Logger.d("onCardClicked: ${packageInfo.appName} (${packageInfo.packageName})")
        changeFiltersRule(packageInfo)
    }

    /**
     * 許可アプリを変更する関数
     * @param packageInfo フィルター規則を変更したいパッケージ名
     */
    private fun changeFiltersRule(packageInfo: PackageInfo) {
        val currentList = allowlist.value
        viewModelScope.launch {
            if (currentList != null && currentList.contains(packageInfo.packageName)) {
                // 許可 → 拒否
                allowlistRepository.disallowPackage(packageInfo.packageName)
            } else {
                // 拒否 → 許可
                allowlistRepository.insertAllowedPackage(
                    packageInfo.packageName,
                    packageInfo.appName
                )
            }
        }
    }
}

class AppPackageListViewModelFactory(private val repository: AllowlistRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppPackageListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppPackageListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}