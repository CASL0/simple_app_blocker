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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.co.casl0.android.simpleappblocker.databinding.FragmentAllowlistBinding
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.newrule.NewRuleDialog
import jp.co.casl0.android.simpleappblocker.ui.allowlist.AllowlistScreen
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@AndroidEntryPoint
class AllowlistFragment : Fragment() {

    companion object {
        fun newInstance() = AllowlistFragment()
    }

    private val viewModel: AllowlistViewModel by viewModels()
    private lateinit var binding: FragmentAllowlistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAllowlistBinding.inflate(layoutInflater, container, false)
        binding.allowlistComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ApplicationTheme {
                    AllowlistFragmentScreen(viewModel) {
                        NewRuleDialog.newInstance().show(childFragmentManager, "NewRuleDialog")
                    }
                }
            }
        }
        return binding.root
    }
}

@Composable
fun AllowlistFragmentScreen(
    allowlistViewModel: AllowlistViewModel,
    onAddButtonClicked: () -> Unit
) {
    val allowedPackages: List<AppPackage> by allowlistViewModel.allowlist.collectAsState(
        listOf()
    )
    AllowlistScreen(allowedPackages, onAddButtonClicked, allowlistViewModel.disallowPackage)
}
