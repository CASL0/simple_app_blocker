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

package jp.co.casl0.android.simpleappblocker.ui.allowlist

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.casl0.android.simpleappblocker.model.AppPackage

@Composable
fun AllowlistScreen(
    allowedPackages: List<AppPackage>,
    onAddButtonClicked: () -> Unit,
    onItemRemove: (appPackage: AppPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = onAddButtonClicked) {
            Icon(Icons.Filled.Add, contentDescription = "add")
        }
    }, backgroundColor = MaterialTheme.colors.background, modifier = modifier) {
        AllowlistContent(allowedPackages, onItemRemove, Modifier.padding(it))
    }
}
