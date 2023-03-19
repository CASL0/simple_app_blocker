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

package jp.co.casl0.android.simpleappblocker.feature.blocklog.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import jp.co.casl0.android.simpleappblocker.core.ui.theme.ApplicationTheme
import jp.co.casl0.android.simpleappblocker.feature.blocklog.databinding.FragmentBlocklogBinding
import jp.co.casl0.android.simpleappblocker.feature.blocklog.ui.BlockLogContent
import jp.co.casl0.android.simpleappblocker.feature.blocklog.ui.BlockLogScreen
import jp.co.casl0.android.simpleappblocker.feature.blocklog.ui.BlockedPacketsList
import jp.co.casl0.android.simpleappblocker.feature.blocklog.viewmodel.BlockLogViewModel

@AndroidEntryPoint
class BlockLogFragment : Fragment() {

    private var _binding: FragmentBlocklogBinding? = null
    private val binding get() = _binding!!

    private val _viewModel: BlockLogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlocklogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.blocklogComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ApplicationTheme {
                    BlockLogFragmentScreen(viewModel = _viewModel)
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

@Composable
private fun BlockLogFragmentScreen(viewModel: BlockLogViewModel) {
    val uiState = viewModel.uiState.collectAsState()
    BlockLogScreen {
        BlockLogContent(blockedPackets = BlockedPacketsList(uiState.value.blockedApps))
    }
}
