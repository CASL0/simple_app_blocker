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

package jp.co.casl0.android.simpleappblocker.feature.blocklog.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.core.ui.theme.ApplicationTheme
import jp.co.casl0.android.simpleappblocker.feature.blocklog.R
import jp.co.casl0.android.simpleappblocker.feature.blocklog.viewmodel.UiState

@Composable
internal fun BlockLogItem(
    blockedApp: UiState.BlockedApp,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = blockedApp.appName.toString(),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                // プロトコル
                Text(
                    text = blockedApp.protocol.toString(),
                    textAlign = TextAlign.Left,
                    modifier = Modifier.weight(1.0f),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
                // ブロック時刻
                Text(
                    text = blockedApp.blockedAt.toString(),
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.weight(1.0f)
                )
            }
            Row { // 送信元IPアドレス
                Text(
                    text = stringResource(R.string.block_log_src),
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = blockedApp.src.toString(),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Row { // 宛先IPアドレス
                Text(
                    text = stringResource(R.string.block_log_dst),
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = blockedApp.dst.toString(),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBlockLogItem() {
    ApplicationTheme {
        BlockLogItem(
            blockedApp = UiState.BlockedApp(
                appName = "Chrome",
                packageName = "com.android.vending",
                src = "10.1.10.1 (40000)",
                dst = "100.100.100.100 (443)",
                protocol = "TCP",
                blockedAt = "2000-01-01 00:00:00"
            )
        )
    }
}
