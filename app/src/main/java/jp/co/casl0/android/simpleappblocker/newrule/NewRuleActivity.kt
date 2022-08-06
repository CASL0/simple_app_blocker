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

package jp.co.casl0.android.simpleappblocker.newrule

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import jp.co.casl0.android.simpleappblocker.app.AppBlockerApplication
import jp.co.casl0.android.simpleappblocker.ui.newrule.NewRuleContent
import jp.co.casl0.android.simpleappblocker.ui.newrule.NewRuleScreen
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme
import kotlinx.coroutines.launch

class NewRuleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel = ViewModelProvider(
            this,
            NewRuleViewModelFactory((applicationContext as AppBlockerApplication).repository)
        ).get(NewRuleViewModel::class.java)
        lifecycleScope.launch {
            viewModel.loadInstalledPackages(this@NewRuleActivity)
        }
        supportActionBar?.hide()
        setContent {
            ApplicationTheme {
                NewRuleActivityScreen(viewModel) { finish() }
            }
        }
    }
}

@Composable
fun NewRuleActivityScreen(viewModel: NewRuleViewModel, onClose: () -> Unit) {
    val searchValue = viewModel.searchValue
    val installedPackages = viewModel.installedPackages.filter {
        it.appName?.contains(searchValue, ignoreCase = true)
            ?: true
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