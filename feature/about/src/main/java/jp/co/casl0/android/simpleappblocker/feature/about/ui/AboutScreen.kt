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

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.feature.about.R

@Composable
fun AboutScreen(
    onClickOssLicenses: () -> Unit,
    onClickSource: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    Column(modifier = modifier.verticalScroll(scrollState)) {
        ListItem(
            headerText = stringResource(id = R.string.version_header),
            contentText = "1.1.5" // TODO: FIX ME
        )
        LinkedItem(headerText = R.string.licenses_header, onClick = onClickOssLicenses)
        LinkedItem(headerText = R.string.source_code, onClick = onClickSource)
    }
}

@Composable
private fun ListItem(
    headerText: CharSequence,
    contentText: CharSequence,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(0)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = headerText.toString(),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = contentText.toString()
            )
        }
    }
}

@Composable
private fun LinkedItem(
    @StringRes headerText: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0)
    ) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = stringResource(id = headerText),
                    color = MaterialTheme.colors.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
