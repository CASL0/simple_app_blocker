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

package jp.co.casl0.android.simpleappblocker.ui.newrule

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.DrawablePainter
import jp.co.casl0.android.simpleappblocker.model.AppPackage

@Composable
fun NewRuleItem(
    appPackage: AppPackage,
    modifier: Modifier = Modifier
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth()
    ) {
        Row(
            modifier = modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image( // アイコン画像
                painter = DrawablePainter(appPackage.icon),
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
                    text = appPackage.appName,
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    // パッケージ名
                    text = appPackage.packageName,
                    color = MaterialTheme.colors.onSurface,
                )
            }
            val allowAppPackage = LocalButtonClickHandler.current
            val context = LocalContext.current
            IconButton(
                onClick = { allowAppPackage(context, appPackage) },
                modifier = modifier
                    .padding(12.dp)
                    .size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LibraryAdd,
                    contentDescription = "Allow",
                    tint = MaterialTheme.colors.secondary
                )
            }
        }
    }
}