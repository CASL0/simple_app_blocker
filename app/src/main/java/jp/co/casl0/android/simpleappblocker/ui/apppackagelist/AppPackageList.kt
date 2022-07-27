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

package jp.co.casl0.android.simpleappblocker.ui.apppackagelist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.model.AppPackage

@Composable
fun AppPackageList(packageList: List<AppPackage>, onCardClicked: ((AppPackage) -> Unit)) {
    Surface(modifier = Modifier.padding(vertical = 8.dp)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(items = packageList,
                key = { appPackage -> appPackage.packageName!! }) { appPackage ->
                if (appPackage.icon != null && appPackage.appName != null && appPackage.packageName != null) {
                    AppPackageItem(
                        icon = appPackage.icon,
                        appName = appPackage.appName,
                        packageName = appPackage.packageName,
                        modifier = Modifier.clickable { onCardClicked(appPackage) }
                    )
                }
            }
        }
    }
}