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

package jp.co.casl0.android.simpleappblocker.ui.organisms

import android.content.res.Configuration
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jp.co.casl0.android.simpleappblocker.ui.molecules.BlockLogContent
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@Composable
fun BlockLogItem(
    src: String,
    dst: String,
    protocol: String,
    time: String,
    appName: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(IntrinsicSize.Max).fillMaxWidth()
    ) {
        BlockLogContent(src, dst, protocol, time, appName, modifier)
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewBlockLogItem() {
    ApplicationTheme {
        BlockLogItem(
            src = "1.1.1.1 (40000)",
            dst = "2.2.2.2 (50000)",
            protocol = "TCP",
            time = "yyyy-MM-dd HH:mm:ss",
            appName = "Chrome",
        )
    }
}