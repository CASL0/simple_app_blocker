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

package jp.co.casl0.android.simpleappblocker.feature.rule_change.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.core.model.AppPackage

@Immutable
internal data class InstalledPackagesList(val items: List<AppPackage>)

@Composable
internal fun RuleChangeContent(
    installedPackages: InstalledPackagesList,
    changeFilterRule: (allow: Boolean, appPackage: AppPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberLazyListState()
    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = installedPackages.items,
            key = { installedPackage -> installedPackage.packageName }
        ) { installedPackage ->
            RuleChangeItem(installedPackage, changeFilterRule, modifier = Modifier)
        }
    }
}
