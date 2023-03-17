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

package jp.co.casl0.android.simpleappblocker.blocklog

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jp.co.casl0.android.simpleappblocker.core.data.repository.BlockedPacketsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    data class BlockLogUiState(
        override val blockedApps: List<BlockedApp> = listOf()
    ) : UiState
}

@HiltViewModel
class BlockLogViewModel @Inject constructor(
    private val blockedPacketsRepository: BlockedPacketsRepository,
    @ApplicationContext context: Context
) :
    AndroidViewModel(context.applicationContext as Application) {

    /** UI状態 */
    private val _uiState = MutableStateFlow(UiState.BlockLogUiState())
    val uiState: StateFlow<UiState.BlockLogUiState> get() = _uiState

    init {
        viewModelScope.launch {
            blockedPacketsRepository.blockedPackets.collect { domainBlockedPackets ->
                val packageManager = context.packageManager
                val blockedPackets: List<UiState.BlockedApp?> =
                    domainBlockedPackets.map { domainBlockedPacket ->
                        try {
                            val appInfo = if (Build.VERSION.SDK_INT >= 33) {
                                packageManager.getApplicationInfo(
                                    domainBlockedPacket.packageName.toString(),
                                    PackageManager.ApplicationInfoFlags.of(0)
                                )
                            } else {
                                packageManager.getApplicationInfo(
                                    domainBlockedPacket.packageName.toString(),
                                    0
                                )
                            }
                            UiState.BlockedApp(
                                appName = appInfo.loadLabel(packageManager),
                                packageName = domainBlockedPacket.packageName,
                                src = domainBlockedPacket.srcAddressAndPort,
                                dst = domainBlockedPacket.dstAddressAndPort,
                                protocol = domainBlockedPacket.protocol,
                                blockedAt = domainBlockedPacket.blockedAt
                            )
                        } catch (e: PackageManager.NameNotFoundException) {
                            e.localizedMessage?.let { err -> Logger.d(err) }
                            null
                        }
                    }
                _uiState.update { it.copy(blockedApps = blockedPackets.filterNotNull()) }
            }
        }
    }
}
