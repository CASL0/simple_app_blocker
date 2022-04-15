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

package jp.co.casl0.android.simpleappblocker.ui.atoms

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.DrawablePainter
import jp.co.casl0.android.simpleappblocker.ui.theme.ApplicationTheme

@Composable
fun IconImage(icon: Drawable, description: String? = null) {
    Image(
        painter = DrawablePainter(icon),
        contentDescription = description,
        modifier = Modifier.clip(CircleShape).size(50.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewIconImage() {
    ApplicationTheme {
        val packageName = LocalContext.current.packageName
        val icon = LocalContext.current.packageManager.getApplicationIcon(packageName)
        IconImage(icon)
    }
}