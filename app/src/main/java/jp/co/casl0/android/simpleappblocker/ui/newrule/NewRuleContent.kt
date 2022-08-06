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

package jp.co.casl0.android.simpleappblocker.ui.newrule

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.ui.allowlist.AllowlistItem

@Composable
fun NewRuleContent(
    installedPackages: List<AppPackage>,
    searchValue: String,
    onSearchValueChange: (newValue: String) -> Unit,
    onListItemClicked: (appPackage: AppPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = searchValue,
            onValueChange = onSearchValueChange,
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
            singleLine = true,
            label = { Text(stringResource(R.string.search_placeholder)) },
            modifier = modifier
                .fillMaxWidth()
        )
        val scrollState = rememberLazyListState()
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(items = installedPackages,
                key = { installedPackage -> installedPackage.packageName!! }
            ) { installedPackage ->
                AllowlistItem(installedPackage, modifier = modifier.clickable { onListItemClicked(installedPackage) })
            }
        }
    }
}