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

package jp.co.casl0.android.simpleappblocker.feature.blocklog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jp.co.casl0.android.simpleappblocker.core.data.repository.BlockedPacketsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed interface UiState {
    data class BlockedApp(
        val appName: CharSequence,
        val packageName: CharSequence,
        val src: CharSequence,
        val dst: CharSequence,
        val protocol: CharSequence,
        val blockedAt: CharSequence
    )

    val blockedApps: List<BlockedApp>

    /** ブロックログ画面のUI状態 */
    data class BlockLogUiState(
        override val blockedApps: List<BlockedApp> = listOf()
    ) : UiState
}

/** ブロックログ画面のビジネスロジックを扱うViewModel */
@HiltViewModel
class BlockLogViewModel @Inject constructor(
    private val blockedPacketsRepository: BlockedPacketsRepository
) :
    ViewModel() {

    /** UI状態 */
    private val _uiState = MutableStateFlow(UiState.BlockLogUiState())
    val uiState: StateFlow<UiState.BlockLogUiState> get() = _uiState

    /** DateTimeフォーマッタ */
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        viewModelScope.launch {
            // ブロックログ追加の度にUI状態を更新する
            blockedPacketsRepository.getBlockedPacketsStream().collect { domainBlockedPackets ->
                val blockedPackets =
                    domainBlockedPackets.map { domainBlockedPacket ->
                        UiState.BlockedApp(
                            appName = domainBlockedPacket.appName,
                            packageName = domainBlockedPacket.packageName,
                            src = domainBlockedPacket.srcAddressAndPort,
                            dst = domainBlockedPacket.dstAddressAndPort,
                            protocol = domainBlockedPacket.protocol,
                            blockedAt = domainBlockedPacket.blockedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                                .toJavaLocalDateTime().format(formatter)
                        )
                    }.toList()
                _uiState.update { it.copy(blockedApps = blockedPackets) }
            }
        }
    }
}
