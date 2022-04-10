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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import jp.co.casl0.android.simpleappblocker.PackageInfo
import kotlinx.coroutines.launch

class AppPackageListViewModel : ViewModel() {

    /**
     * 許可済みパッケージリスト
     */
    val allowlist: LiveData<List<String>> = AllowlistRepository.allowlist.asLiveData()

    /**
     * インストール済みパッケージリスト
     */
    private val _packageInfoList = MutableLiveData<MutableList<PackageInfo>>(null)
    val packageInfoList: LiveData<MutableList<PackageInfo>>
        get() = _packageInfoList

    /**
     * インストール済みパッケージを読み込む関数
     */
    fun loadInstalledPackages(context: Context?) {
        val tmp: MutableList<PackageInfo> = mutableListOf()
        val pm = context?.packageManager
        if (pm != null) {
            pm.getInstalledApplications(0).forEach {
                tmp.add(
                    PackageInfo(
                        it.loadIcon(pm),
                        it.loadLabel(pm).toString(),
                        it.packageName
                    )
                )
            }
            _packageInfoList.postValue(tmp)
        }
    }

    /**
     * 許可アプリを変更する関数
     */
    fun changeFiltersRule(packageInfo: PackageInfo) {
        val currentList = allowlist.value
        if (currentList != null && currentList.contains(packageInfo.packageName)) {
            // 許可 → 拒否
            viewModelScope.launch {
                AllowlistRepository.disallowPackage(packageInfo.packageName)
            }
        } else {
            // 拒否 → 許可
            viewModelScope.launch {
                AllowlistRepository.insertAllowedPackage(
                    packageInfo.packageName,
                    packageInfo.appName
                )
            }
        }
    }
}