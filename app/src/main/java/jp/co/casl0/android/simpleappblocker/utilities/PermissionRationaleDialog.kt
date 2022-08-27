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

package jp.co.casl0.android.simpleappblocker.utilities

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.co.casl0.android.simpleappblocker.BuildConfig

const val ARGS_MESSAGE = "ARGS_MESSAGE"
const val ARGS_POSITIVE_LABEL = "ARGS_POSITIVE_LABEL"
const val ARGS_NEGATIVE_LABEL = "ARGS_NEGATIVE_LABEL"

class PermissionRationaleDialog : DialogFragment() {
    companion object {
        fun newInstance(
            @StringRes dialogMessage: Int,
            @StringRes positiveButtonLabel: Int,
            @StringRes negativeButtonLabel: Int
        ): PermissionRationaleDialog {
            return PermissionRationaleDialog().apply {
                arguments = bundleOf(
                    ARGS_MESSAGE to dialogMessage,
                    ARGS_POSITIVE_LABEL to positiveButtonLabel,
                    ARGS_NEGATIVE_LABEL to negativeButtonLabel
                )
            }
        }
    }

    private var _dialogMessage: Int = -1
    private var _positiveButtonLabel: Int = -1
    private var _negativeButtonLabel: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _dialogMessage = arguments?.getInt(ARGS_MESSAGE) ?: return
        _positiveButtonLabel = arguments?.getInt(ARGS_POSITIVE_LABEL) ?: return
        _negativeButtonLabel = arguments?.getInt(ARGS_NEGATIVE_LABEL) ?: return
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext()).apply {
            setMessage(_dialogMessage)
            setPositiveButton(_positiveButtonLabel) { _, _ ->
                dismiss()

                // 設定のアプリ情報画面へ遷移
                launchSettings(BuildConfig.APPLICATION_ID)
            }
            setNegativeButton(_negativeButtonLabel) { _, _ ->
                dismiss()
            }
        }
        return builder.create()
    }

    private fun launchSettings(packageName: String) {
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$packageName")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }.run {
            startActivity(this)
        }
    }
}