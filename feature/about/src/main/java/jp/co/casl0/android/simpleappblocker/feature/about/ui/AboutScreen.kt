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

package jp.co.casl0.android.simpleappblocker.feature.about.ui

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.core.ui.theme.ApplicationTheme
import jp.co.casl0.android.simpleappblocker.feature.about.R

@Composable
fun AboutScreen(
    appVersion: CharSequence,
    onClickOssLicenses: () -> Unit,
    onClickSource: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        AboutPanel(appVersion = appVersion)
        Spacer(modifier = Modifier.height(16.dp))
        LinkedItem(
            Icons.Filled.List,
            headerText = R.string.licenses_header,
            onClick = onClickOssLicenses
        )
        LinkedItem(
            leadingIcon = Icons.Filled.Code,
            headerText = R.string.source_code,
            onClick = onClickSource
        )
    }
}

@Composable
private fun LinkedItem(
    leadingIcon: ImageVector,
    @StringRes headerText: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "icon",
                modifier = Modifier.padding(end = 16.dp)
            )
            Text(
                text = stringResource(id = headerText),
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewLinkedItem() {
    ApplicationTheme {
        LinkedItem(
            leadingIcon = Icons.Filled.List,
            headerText = R.string.licenses_header,
            onClick = { /* no op */ }
        )
    }
}
