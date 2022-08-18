/*
 * Copyright 2022 CASL0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package jp.co.casl0.android.simpleappblocker.newrule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.app.AppBlockerApplication
import jp.co.casl0.android.simpleappblocker.ui.newrule.NewRuleContent
import jp.co.casl0.android.simpleappblocker.ui.newrule.NewRuleScreen
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

open class NewRuleDialog : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): NewRuleDialog {
            return NewRuleDialog()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(
            this,
            NewRuleViewModelFactory((requireActivity().applicationContext as AppBlockerApplication).repository)
        ).get(NewRuleViewModel::class.java)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadInstalledPackages(requireContext())
                requireDialog().findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    ?.let {
                        val bsb = BottomSheetBehavior.from(it)
                        if (bsb.state == BottomSheetBehavior.STATE_COLLAPSED) bsb.state =
                            getDefaultState()
                    }
            }
        }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ApplicationTheme {
                    NewRuleDialogScreen(viewModel = viewModel) {
                        dismiss()
                    }
                }
            }
        }
    }

    protected open fun getDefaultState() = BottomSheetBehavior.STATE_EXPANDED
}

@Composable
fun NewRuleDialogScreen(viewModel: NewRuleViewModel, onClose: () -> Unit) {
    val searchValue = viewModel.searchValue
    val installedPackages = viewModel.installedPackages.filter {
        it.appName.contains(searchValue, ignoreCase = true)
    }
    NewRuleScreen(onClose = onClose) {
        NewRuleContent(
            installedPackages,
            searchValue,
            viewModel.onSearchValueChange,
            viewModel.createNewRule
        )
    }
}