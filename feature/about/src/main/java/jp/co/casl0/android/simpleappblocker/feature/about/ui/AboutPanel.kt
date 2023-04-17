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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.DrawablePainter
import jp.co.casl0.android.simpleappblocker.core.ui.theme.ApplicationTheme
import jp.co.casl0.android.simpleappblocker.feature.about.R
import jp.co.casl0.android.simpleappblocker.feature.about.utils.APP_NAME

@Composable
internal fun AboutPanel(appVersion: CharSequence, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val icon = LocalContext.current.getDrawable(R.drawable.ic_launcher_foreground)
        Image(
            painter = DrawablePainter(icon!!),
            contentDescription = "app icon",
            modifier = Modifier.size(100.dp)
        )
        Text(
            text = APP_NAME,
            fontSize = MaterialTheme.typography.h5.fontSize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.description),
            color = MaterialTheme.colors.onBackground,
            fontSize = MaterialTheme.typography.body2.fontSize,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        AppVersion(
            headerText = stringResource(id = R.string.version_header),
            contentText = appVersion
        )
    }
}

@Composable
private fun AppVersion(
    headerText: CharSequence,
    contentText: CharSequence,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = MaterialTheme.shapes.small) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = headerText.toString(),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.body2
            )
            Text(
                text = contentText.toString(),
                style = MaterialTheme.typography.body2
            )
        }
    }
}

@Preview(name = "light Mode")
@Preview(name = "Dark Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun PreviewAboutApp() {
    ApplicationTheme {
        AboutPanel("0.0.0")
    }
}
