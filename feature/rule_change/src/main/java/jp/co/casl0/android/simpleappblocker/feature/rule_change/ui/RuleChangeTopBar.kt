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

package jp.co.casl0.android.simpleappblocker.feature.rule_change.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import jp.co.casl0.android.simpleappblocker.feature.rule_change.R

@Composable
internal fun RuleChangeTopBar(
    @StringRes title: Int,
    showedSearchBox: Boolean,
    searchValue: String,
    onClickSearch: () -> Unit,
    onSearchValueChange: (newValue: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colors.primary)
    ) {
        if (showedSearchBox) {
            SearchTopAppBar(
                searchValue = searchValue,
                onSearchValueChange = onSearchValueChange,
                onClose = onClose,
                modifier = Modifier.statusBarsPadding()
            )
        } else {
            DefaultTopAppBar(
                title = title,
                onClickSearch = onClickSearch,
                onClose = onClose,
                modifier = Modifier.statusBarsPadding()
            )
        }
    }
}

@Composable
private fun DefaultTopAppBar(
    @StringRes title: Int,
    onClickSearch: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor = contentColorFor(MaterialTheme.colors.primarySurface)
    TopAppBar(
        modifier = modifier,
        backgroundColor = MaterialTheme.colors.primary,
        title = {
            Text(
                text = stringResource(id = title),
                color = contentColor
            )
        },
        elevation = 0.dp,
        navigationIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = contentColor
                )
            }
        },
        actions = {
            IconButton(
                onClick = onClickSearch
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = contentColor
                )
            }
        }
    )
}

@Composable
private fun SearchTopAppBar(
    searchValue: String,
    onSearchValueChange: (newValue: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        color = Color.Transparent,
    ) {
        val backgroundColor = MaterialTheme.colors.primary
        val contentColor = contentColorFor(MaterialTheme.colors.primarySurface)

        // 検索ボックスにフォーカスを当てるコルーチンを起動
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Row {
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .fillMaxHeight()
                    .background(backgroundColor)
            ) {
                Icon(
                    Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = contentColor,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            TextField(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .focusRequester(focusRequester),
                shape = MaterialTheme.shapes.large,
                value = searchValue,
                onValueChange = onSearchValueChange,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                placeholder = {
                    Text(
                        stringResource(R.string.search_placeholder)
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    textColor = contentColor,
                    placeholderColor = contentColor,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    backgroundColor = backgroundColor
                )
            )
        }
    }
}
