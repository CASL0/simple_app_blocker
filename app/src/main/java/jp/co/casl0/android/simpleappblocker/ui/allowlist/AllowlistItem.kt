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

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.drawablepainter.DrawablePainter
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.model.AppPackage
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@Composable
fun AllowlistItem(
    appPackage: AppPackage,
    onItemRemove: (appPackage: AppPackage) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image( // アイコン画像
                painter =
                if (appPackage.icon != null) {
                    DrawablePainter(appPackage.icon)
                } else {
                    // デフォルトでドロイド君
                    rememberImagePainter(
                        data = stringResource(R.string.default_icon_img_url)
                    )
                },
                contentDescription = "icon",
                modifier = modifier
                    .clip(CircleShape)
                    .size(50.dp)
            )
            Column(
                modifier = modifier
                    .padding(start = 8.dp)
                    .weight(1f)
            ) {
                Text(
                    // アプリ名
                    text = appPackage.appName ?: "",
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    // パッケージ名
                    text = appPackage.packageName ?: "",
                    color = MaterialTheme.colors.onSurface,
                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colors.onSurface,
                modifier = modifier
                    .clickable { onItemRemove(appPackage) }
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAllowlistItem() {
    ApplicationTheme {
        val packageName = LocalContext.current.packageName
        val icon = LocalContext.current.packageManager.getApplicationIcon(packageName)
        AllowlistItem(AppPackage(icon, "SimpleAppBlocker", packageName), {})
    }
}