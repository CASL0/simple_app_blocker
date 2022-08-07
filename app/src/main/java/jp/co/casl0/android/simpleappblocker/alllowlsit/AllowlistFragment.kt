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

package jp.co.casl0.android.simpleappblocker.alllowlsit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import jp.co.casl0.android.simpleappblocker.app.AppBlockerApplication
import jp.co.casl0.android.simpleappblocker.databinding.FragmentAllowlistBinding
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.newrule.NewRuleActivity
import jp.co.casl0.android.simpleappblocker.ui.allowlist.AllowlistScreen
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

class AllowlistFragment : Fragment() {

    companion object {
        fun newInstance() = AllowlistFragment()
    }

    private lateinit var viewModel: AllowlistViewModel
    private lateinit var binding: FragmentAllowlistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(
                this,
                AllowlistViewModelFactory((context?.applicationContext as AppBlockerApplication).repository)
            ).get(AllowlistViewModel::class.java)
        binding = FragmentAllowlistBinding.inflate(layoutInflater, container, false)
        binding.allowlistComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ApplicationTheme {
                    AllowlistFragmentScreen(viewModel) {
                        Intent(context, NewRuleActivity::class.java).also {
                            startActivity(it)
                        }
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
    val allowedPackages: List<String> by allowlistViewModel.allowlist.observeAsState(
        listOf()
    )
    val pm = LocalContext.current.packageManager
    val newAllowedPackages: MutableList<AppPackage> = mutableListOf()
    allowedPackages.forEach { allowedPackageName ->
        pm.getPackageInfo(
            allowedPackageName,
            0
        ).applicationInfo.let { appInfo ->
            newAllowedPackages.add(
                AppPackage(
                    appInfo.loadIcon(pm),
                    appInfo.loadLabel(pm).toString(),
                    appInfo.packageName,
                )
            )
        }
    }
    AllowlistScreen(newAllowedPackages, onAddButtonClicked, allowlistViewModel.disallowPackage)
}