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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import jp.co.casl0.android.simpleappblocker.ui.newrule.InstalledPackagesList
import jp.co.casl0.android.simpleappblocker.ui.newrule.NewRuleContent
import jp.co.casl0.android.simpleappblocker.ui.newrule.NewRuleScreen
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class NewRuleDialog : BottomSheetDialogFragment() {
    companion object {
        fun newInstance(): NewRuleDialog {
            return NewRuleDialog()
        }
    }

    private val viewModel: NewRuleViewModel by viewModels()

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

    protected open fun getDefaultState() = BottomSheetBehavior.STATE_EXPANDED
}

@Composable
fun NewRuleDialogScreen(viewModel: NewRuleViewModel, onClose: () -> Unit) {
    val uiState = viewModel.uiState.collectAsState()
    val installedApplications = viewModel.installedApplications.collectAsState(listOf()).value
    NewRuleScreen(
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
        NewRuleContent(
            InstalledPackagesList(filteredApplications),
            viewModel::changeFilterRule
        )
    }
}
