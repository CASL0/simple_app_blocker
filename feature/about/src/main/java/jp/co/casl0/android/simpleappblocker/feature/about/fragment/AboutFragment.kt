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

package jp.co.casl0.android.simpleappblocker.feature.about.fragment

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.orhanobut.logger.Logger
import jp.co.casl0.android.simpleappblocker.core.ui.theme.ApplicationTheme
import jp.co.casl0.android.simpleappblocker.feature.about.BuildConfig
import jp.co.casl0.android.simpleappblocker.feature.about.databinding.FragmentAboutBinding
import jp.co.casl0.android.simpleappblocker.feature.about.ui.AboutScreen
import jp.co.casl0.android.simpleappblocker.feature.about.utils.SOURCE_CODE_URL

class AboutFragment : Fragment() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var _binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        _binding.othersComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ApplicationTheme {
                    AboutScreen(
                        BuildConfig.VERSION_NAME,
                        onClickOssLicenses = {
                            startActivity(
                                Intent(
                                    context,
                                    OssLicensesMenuActivity::class.java
                                ).apply {
                                    flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
                                }
                            )
                        },
                        onClickSource = {
                            try {
                                Intent().apply {
                                    action = Intent.ACTION_VIEW
                                    data = Uri.parse(SOURCE_CODE_URL)
                                    flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TOP
                                }.run {
                                    context.startActivity(this)
                                }
                            } catch (e: ActivityNotFoundException) {
                                e.localizedMessage?.let { Logger.d(it) }
                            }
                        }
                    )
                }
            }
        }
        return _binding.root
    }
}
