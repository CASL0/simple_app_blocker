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

import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.orhanobut.logger.Logger

/**
 * ランタイムパーミッションをリクエストします
 */
internal fun AppCompatActivity.requestPermission(
    permission: CharSequence,
    showRequestPermissionRationale: (() -> Unit)? = null,
    permissionLauncher: ActivityResultLauncher<String>
) {
    when {
        ContextCompat.checkSelfPermission(
            this,
            permission.toString()
        ) == PackageManager.PERMISSION_GRANTED -> {
            Logger.d("permission granted: $permission")
        }
        shouldShowRequestPermissionRationale(permission.toString()) -> {
            showRequestPermissionRationale?.let { showRequestPermissionRationale() }
        }
        else -> {
            permissionLauncher.launch(permission.toString())
        }
    }
}
