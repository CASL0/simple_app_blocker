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

package jp.co.casl0.android.simpleappblocker.ui.others.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.R

@Composable
fun LicensesList(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(end = 16.dp)
    ) {
        val licenses = listOf(
            Pair(R.string.accompanist_library_name, R.string.accompanist_license),
            Pair(R.string.coil_library_name, R.string.coil_license),
            Pair(R.string.eventbus_library_name, R.string.eventbus_license),
            Pair(R.string.logger_library_name, R.string.logger_license),
            Pair(R.string.pcapplusplus_library_name, R.string.pcapplusplus_license),
        )
        licenses.forEach { license ->
            LicensesItem(libraryName = license.first, licenseContent = license.second)
        }
    }
}