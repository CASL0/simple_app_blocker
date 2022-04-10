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
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import jp.co.casl0.android.simpleappblocker.PackageInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppPackageListViewModel(private val allowlistRepository: AllowlistRepository) : ViewModel() {

    /**
     * 許可済みパッケージリスト
     */
    val allowlist: LiveData<List<String>> = allowlistRepository.allowlist.asLiveData()

    /**
     * インストール済みパッケージリスト
     */
    private val _packageInfoList = MutableLiveData<MutableList<PackageInfo>?>(null)
    val packageInfoList: LiveData<MutableList<PackageInfo>?>
        get() = _packageInfoList

    /**
     * インストール済みパッケージを読み込む関数
     */
    fun loadInstalledPackages(context: Context?) {
        val tmp: MutableList<PackageInfo> = mutableListOf()
        context?.packageManager?.also { pm ->
            viewModelScope.launch(Dispatchers.IO) {
                val currentAllowed = allowlistRepository.allowlist.first()
                pm.getInstalledApplications(0).forEach { appInfo ->
                    tmp.add(
                        PackageInfo(
                            appInfo.loadIcon(pm),
                            appInfo.loadLabel(pm).toString(),
                            appInfo.packageName,
                            currentAllowed.contains(appInfo.packageName)
                        )
                    )
                }
                _packageInfoList.postValue(tmp)
            }
        }
    }

    /**
     * 許可アプリを変更する関数
     * @param packageInfo フィルター規則を変更したいパッケージ名
     */
    fun changeFiltersRule(packageInfo: PackageInfo) {
        val currentList = allowlist.value
        var isAllowed: Boolean
        viewModelScope.launch {
            isAllowed = if (currentList != null && currentList.contains(packageInfo.packageName)) {
                // 許可 → 拒否
                allowlistRepository.disallowPackage(packageInfo.packageName)
                false
            } else {
                // 拒否 → 許可
                allowlistRepository.insertAllowedPackage(
                    packageInfo.packageName,
                    packageInfo.appName
                )
                true
            }

            // UI表示用にフラグ変更
            notifyAllowed(packageInfo.packageName, isAllowed)
        }
    }

    /**
     * フィルター規則を変更したことをLiveDataに伝える関数
     * @param packageName 規則を変更したパッケージ名
     * @param isAllowed 変更後の規則
     */
    private fun notifyAllowed(packageName: String, isAllowed: Boolean) {
        _packageInfoList.value?.also { tmp ->
            tmp.forEach {
                if (it.packageName == packageName) it.isAllowed = isAllowed
            }
            _packageInfoList.postValue(tmp)
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