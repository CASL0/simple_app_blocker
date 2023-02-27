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

package jp.co.casl0.android.simpleappblocker.ui.blocklog

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.blocklog.UiState
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@Composable
fun BlockLogItem(
    blockedApp: UiState.BlockedApp,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .width(IntrinsicSize.Max),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(modifier = modifier.fillMaxWidth()) {
                Text(
                    text = blockedApp.appName.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    modifier = modifier.align(Alignment.CenterVertically)
                )
            }
            Row(modifier = modifier.fillMaxWidth()) {
                // プロトコル
                Text(
                    text = blockedApp.protocol.toString(),
                    textAlign = TextAlign.Left,
                    modifier = modifier.weight(1.0f),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                // ブロック時刻
                Text(
                    text = blockedApp.blockedAt.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Right,
                    modifier = modifier.weight(1.0f)
                )
            }
            Row { // 送信元IPアドレス
                Text(
                    text = stringResource(R.string.block_log_src),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = modifier.width(8.dp))
                Text(
                    text = blockedApp.src.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
            Row { // 宛先IPアドレス
                Text(
                    text = stringResource(R.string.block_log_dst),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = modifier.width(8.dp))
                Text(
                    text = blockedApp.dst.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
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
