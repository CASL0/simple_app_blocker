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

package jp.co.casl0.android.simpleappblocker.feature.rule_change.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.co.casl0.android.simpleappblocker.core.ui.theme.ApplicationTheme
import jp.co.casl0.android.simpleappblocker.feature.rule_change.R
import jp.co.casl0.android.simpleappblocker.feature.rule_change.ui.InstalledPackagesList
import jp.co.casl0.android.simpleappblocker.feature.rule_change.ui.RuleChangeContent
import jp.co.casl0.android.simpleappblocker.feature.rule_change.ui.RuleChangeScreen
import jp.co.casl0.android.simpleappblocker.feature.rule_change.viewmodel.RuleChangeViewModel
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class RuleChangeDialog : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): RuleChangeDialog {
            return RuleChangeDialog()
        }
    }

    private val viewModel: RuleChangeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                requireDialog().findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                    ?.let {
                        val bsb = BottomSheetBehavior.from(it)
                        bsb.isDraggable = false
                        if (bsb.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            bsb.state =
                                getDefaultState()
                        }
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

    /** Edge to Edge対応のためカスタムしたBottomSheetDialog */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme) {
            override fun onAttachedToWindow() {
                super.onAttachedToWindow()

                findViewById<View>(com.google.android.material.R.id.container)?.fitsSystemWindows =
                    false
                findViewById<View>(com.google.android.material.R.id.coordinator)?.fitsSystemWindows =
                    false
            }
        }
    }

    protected open fun getDefaultState() = BottomSheetBehavior.STATE_EXPANDED
}

@Composable
private fun NewRuleDialogScreen(viewModel: RuleChangeViewModel, onClose: () -> Unit) {
    val uiState = viewModel.uiState.collectAsState()
    val installedApplications = viewModel.installedApplications.collectAsState(listOf()).value
    RuleChangeScreen(
        isRefreshing = uiState.value.isRefreshing,
        showedSearchBox = uiState.value.showedSearchBox,
        searchValue = uiState.value.searchValue,
        onClickSearch = viewModel::onClickSearch,
        onSearchValueChange = viewModel::onSearchValueChange,
        onRefresh = viewModel::refreshInstalledApplications,
        onClose = onClose
    ) {
        val searchValue = uiState.value.searchValue
        val filteredApplications = installedApplications.filter {
            it.appName.contains(searchValue, ignoreCase = true)
        }
        RuleChangeContent(
            InstalledPackagesList(filteredApplications),
            viewModel::changeFilterRule
        )
    }
}
