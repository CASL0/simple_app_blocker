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
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.R
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@Composable
fun AllowlistSection(
    @StringRes title: Int,
    onAddButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Scaffold(floatingActionButton = {
        FloatingActionButton(onClick = onAddButtonClicked) {
            Icon(Icons.Filled.Add, contentDescription = "add")
        }
    }) {
        Column(modifier = modifier.padding(horizontal = 8.dp)) {
            Text(
                stringResource(title),
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.h6,
                modifier = modifier.paddingFromBaseline(top = 40.dp, bottom = 8.dp)
            )
            content()
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PreviewAllowlistSection() {
    ApplicationTheme {
        AllowlistSection(R.string.allowlist_section_header, {}) {
        }
    }
}