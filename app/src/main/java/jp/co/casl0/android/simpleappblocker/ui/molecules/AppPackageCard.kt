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

package jp.co.casl0.android.simpleappblocker.ui.molecules

import android.content.res.Configuration
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.ui.atoms.HeaderText
import jp.co.casl0.android.simpleappblocker.ui.atoms.IconImage
import jp.co.casl0.android.simpleappblocker.ui.atoms.SubText
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@Composable
fun AppPackageCard(
    icon: Drawable,
    appName: String,
    packageName: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.height(IntrinsicSize.Max).fillMaxWidth()
    ) {
        Row(
            modifier = modifier.background(MaterialTheme.colors.background)
                .padding(8.dp)
        ) {
            IconImage(icon)
            Column(modifier = modifier.padding(start = 8.dp).align(Alignment.CenterVertically)) {
                HeaderText(appName)
                SubText(packageName)
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAppPackageCard() {
    ApplicationTheme {
        val packageName = LocalContext.current.packageName
        val icon = LocalContext.current.packageManager.getApplicationIcon(packageName)
        AppPackageCard(icon, "SimpleAppBlocker", packageName)
    }
}