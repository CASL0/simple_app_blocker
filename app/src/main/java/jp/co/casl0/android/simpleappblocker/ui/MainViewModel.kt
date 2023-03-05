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

package jp.co.casl0.android.simpleappblocker.ui

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.casl0.android.simpleappblocker.repository.AllowlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import javax.inject.Inject

@Parcelize
data class MainUiState(
    val filtersEnabled: Boolean = false
) : Parcelable

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    allowlistRepository: AllowlistRepository
) : ViewModel() {
    companion object {
        private const val KEY_UI_STATE = "ui_state"
    }

    /** UI状態 */
    private val _uiState =
        MutableStateFlow(savedStateHandle.get<MainUiState>(key = KEY_UI_STATE) ?: MainUiState())
    val uiState: StateFlow<MainUiState> get() = _uiState

    /** 許可リスト */
    val allowlist = allowlistRepository.allowlist

    /** フィルターの有効・無効を切り替えます */
    fun enableFilters(enable: Boolean) {
        _uiState.update { it.copy(filtersEnabled = enable) }
        savedStateHandle[KEY_UI_STATE] = uiState.value
    }
}
