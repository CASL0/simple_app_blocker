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

package jp.co.casl0.android.simpleappblocker.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.installStatus
import com.google.android.play.core.ktx.updatePriority
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/** アップデート情報 */
data class UpdateInfo(
    val updateAvailability: Int, // アップデートの有無
    val immediateAllowed: Boolean, // Immediate Update可能か
    val flexibleAllowed: Boolean, // Flexible Update可能か
    val availableVersionCode: Int, // アップデート可能なバージョンコード
    val installStatus: Int, // インストールステータス
    val packageName: CharSequence, // パッケージ名
    val clientVersionStalenessDays: Int?, // 公開後経過した日数
    val updatePriority: Int // 優先度
)

sealed interface Result<out T> {
    data class Success<out T>(val value: T) : Result<T>
    data class Error(val exception: Exception) : Result<Nothing>
}

/** アプリ内アップデートのコントローラー */
open class AppUpdateController(private val appUpdateManager: AppUpdateManager) :
    DefaultLifecycleObserver {

    companion object {
        private const val REQUEST_CODE_START_UPDATE = 1000
    }

    interface OnAppUpdateStateChangeListener {
        fun onAppUpdateStateChange(state: Int)
    }

    private var _appUpdateStateChangeListener: OnAppUpdateStateChangeListener? = null

    fun setOnAppUpdateStateChangeListener(listener: OnAppUpdateStateChangeListener) {
        _appUpdateStateChangeListener = listener
    }

    /** インストールステータスのリスナー */
    private var _listener = InstallStateUpdatedListener { installState ->
        _appUpdateStateChangeListener?.onAppUpdateStateChange(installState.installStatus())
    }

    /** 最新の更新情報 */
    private var _updateInfo: AppUpdateInfo? = null

    init {
        appUpdateManager.registerListener(_listener)
    }

    /** アップデートの有無の確認 */
    open fun checkForUpdateAvailability(): Flow<Result<UpdateInfo>> = callbackFlow {
        appUpdateManager.appUpdateInfo.apply {
            addOnSuccessListener {
                _updateInfo = it
                val result = Result.Success(
                    UpdateInfo(
                        updateAvailability = it.updateAvailability(),
                        immediateAllowed = it.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE),
                        flexibleAllowed = it.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE),
                        availableVersionCode = it.availableVersionCode(),
                        installStatus = it.installStatus,
                        packageName = it.packageName(),
                        clientVersionStalenessDays = it.clientVersionStalenessDays,
                        updatePriority = it.updatePriority
                    )
                )
                trySend(result)
            }
            addOnFailureListener {
                _updateInfo = null
                val result = Result.Error(it)
                trySend(result)
            }
        }
        awaitClose { cancel() }
    }

    /** Flexibleアップデートを実行します */
    open fun startFlexibleUpdate(activityResultCallback: ActivityResultLauncher<IntentSenderRequest>) {
        _updateInfo?.let {
            val starter =
                IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                    val request = IntentSenderRequest.Builder(intent)
                        .setFillInIntent(fillInIntent)
                        .setFlags(flagsValues, flagsMask)
                        .build()

                    activityResultCallback.launch(request)
                }
            appUpdateManager.startUpdateFlowForResult(
                it,
                AppUpdateType.FLEXIBLE,
                starter,
                REQUEST_CODE_START_UPDATE
            )
        }
    }

    /** Immediateアップデートを実行します */
    open fun startImmediateUpdate(activityResultCallback: ActivityResultLauncher<IntentSenderRequest>) {
        _updateInfo?.let {
            val starter =
                IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                    val request = IntentSenderRequest.Builder(intent)
                        .setFillInIntent(fillInIntent)
                        .setFlags(flagsValues, flagsMask)
                        .build()

                    activityResultCallback.launch(request)
                }
            appUpdateManager.startUpdateFlowForResult(
                it,
                AppUpdateType.IMMEDIATE,
                starter,
                REQUEST_CODE_START_UPDATE
            )
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            _appUpdateStateChangeListener?.onAppUpdateStateChange(appUpdateInfo.installStatus)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        appUpdateManager.unregisterListener(_listener)
    }
}
