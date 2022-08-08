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

package jp.co.casl0.android.simpleappblocker.ui.others

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import jp.co.casl0.android.simpleappblocker.BuildConfig
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.ui.others.licenses.LicensesList

@Composable
fun OthersScreen(modifier: Modifier = Modifier) {
    Column {
        OthersItem(itemHeader = R.string.version_header, modifier = modifier) {
            // バージョン情報
            Text(
                text = "v${BuildConfig.VERSION_NAME}",
                fontWeight = FontWeight.Bold
            )
        }
        OthersItem(itemHeader = R.string.licenses_header, modifier = modifier) {
            // オープンソースライセンス
            LicensesList(modifier = modifier)
        }
    }
}