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

package jp.co.casl0.android.simpleappblocker.feature.allowlist.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.core.model.AppPackage
import jp.co.casl0.android.simpleappblocker.feature.allowlist.R

@Immutable
data class AllowedPackagesList(val items: List<AppPackage>)

@Composable
fun AllowlistContent(
    allowedPackages: AllowedPackagesList,
    onItemRemove: (appPackage: AppPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    if (allowedPackages.items.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(stringResource(R.string.empty_allowlist))
        }
    } else {
        val scrollState = rememberLazyListState()
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = modifier
        ) {
            items(
                items = allowedPackages.items,
                key = { allowedPackage -> allowedPackage.packageName }
            ) { allowedPackage ->
                AllowlistItem(allowedPackage, onItemRemove, modifier = Modifier)
            }
        }
    }
}
