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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.repository.AllowlistRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllowlistViewModel @Inject constructor(private val allowlistRepository: AllowlistRepository) :
    ViewModel() {
    /**
     * 許可リスト
     */
    val allowlist = allowlistRepository.allowlist

    /**
     * 指定のパッケージをブロックに変更する
     */
    val disallowPackage: (AppPackage) -> Unit = { appPackage: AppPackage ->
        viewModelScope.launch {
            allowlistRepository.disallowPackage(appPackage.packageName)
        }
    }
}

class AllowlistViewModelFactory(private val repository: AllowlistRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AllowlistViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AllowlistViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
