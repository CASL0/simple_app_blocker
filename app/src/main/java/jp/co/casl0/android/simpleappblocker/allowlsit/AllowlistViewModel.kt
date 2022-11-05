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

package jp.co.casl0.android.simpleappblocker.allowlsit

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.repository.AllowlistRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllowlistViewModel @Inject constructor(
    private val allowlistRepository: AllowlistRepository,
    @ApplicationContext context: Context
) :
    AndroidViewModel(context.applicationContext as Application) {
    /**
     * 許可リスト
     */
    val allowlist = allowlistRepository.allowlist.map { allowedPackages ->
        val pm = getApplication<Application>().packageManager
        allowedPackages.map {
            val appInfo = if (Build.VERSION.SDK_INT >= 33) {
                pm.getPackageInfo(
                    it.toString(),
                    PackageManager.PackageInfoFlags.of(0)
                ).applicationInfo
            } else {
                pm.getPackageInfo(it.toString(), 0).applicationInfo
            }
            AppPackage(
                appInfo.loadIcon(pm),
                appInfo.loadLabel(pm).toString(),
                appInfo.packageName,
            )
        }
    }

    /**
     * 指定のパッケージをブロックに変更する
     */
    fun disallowPackage(appPackage: AppPackage) {
        viewModelScope.launch {
            allowlistRepository.disallowPackage(appPackage.packageName)
        }
    }
}
