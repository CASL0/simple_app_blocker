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

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.model.AppPackage

@Composable
fun AllowlistScreen(allowedPackages: List<AppPackage>, modifier: Modifier = Modifier) {
    Column {
        AllowlistSection(
            R.string.allowlist_section_header,
            onAddButtonClicked = { /* TODO: フィルター追加画面への遷移　*/ },
            modifier = modifier
        ) {
            AllowlistContent(allowedPackages, modifier)
        }
    }
}