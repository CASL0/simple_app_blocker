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

package jp.co.casl0.android.simpleappblocker

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.InflateException
import android.view.Menu
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import jp.co.casl0.android.simpleappblocker.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    companion object {
        private const val REQUEST_CODE_VPN_SERVICE = 100
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    var appBlockerService: AppBlockerService? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AppBlockerService.AppBlockerBinder
            appBlockerService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Logger.d("service disconnected")
        }
    }

    private fun configureVpnService() {
        val vpnPrepareIntent = VpnService.prepare(this)
        if (vpnPrepareIntent != null) {
            startActivityForResult(vpnPrepareIntent, REQUEST_CODE_VPN_SERVICE)
        } else {
            // 既にVPN同意済み
            Logger.d("VpnService already agreed")
            Intent(this, AppBlockerService::class.java).also {
                bindService(it, connection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.addLogAdapter(
            AndroidLogAdapter(
                PrettyFormatStrategy.newBuilder().tag(getString(R.string.app_name)).build()
            )
        )
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        configureVpnService()
        mainActivityViewModel =
            ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onDestroy() {
        super.onDestroy()
        appBlockerService?.disableFilters()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        try {
            menuInflater.inflate(R.menu.options, menu)

            // アクションバーのスイッチのイベントハンドラを設定
            val actionSwitch = menu?.findItem(R.id.app_bar_switch)?.actionView as? SwitchCompat
            actionSwitch?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    mainActivityViewModel.filtersEnabled = true
                    setActionBarTextColor(
                        supportActionBar,
                        getColorInt(R.color.filters_enabled)
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        (application as? AppBlockerApplication)?.repository?.allowlist?.let {
                            appBlockerService?.updateFilters(
                                it.first()
                            )
                        }
                    }
                } else {
                    mainActivityViewModel.filtersEnabled = false
                    setActionBarTextColor(
                        supportActionBar,
                        getColorInt(R.color.filters_disabled)
                    )
                    appBlockerService?.disableFilters()
                }
            }
            actionSwitch?.isChecked = mainActivityViewModel.filtersEnabled
        } catch (e: InflateException) {
            val errMsg = e.localizedMessage
            if (errMsg != null) Logger.d(errMsg)
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_VPN_SERVICE) {
            if (resultCode == RESULT_OK) {
                Intent(this, AppBlockerService::class.java).also {
                    bindService(it, connection, Context.BIND_AUTO_CREATE)
                }
                return
            } else {
                Logger.d("VpnService rejected")
                finish()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
    }

    private fun setActionBarTextColor(actionBar: ActionBar?, color: Int) {
        val title = SpannableString(actionBar?.title ?: "")
        title.setSpan(
            ForegroundColorSpan(color),
            0,
            title.length,
            Spannable.SPAN_INCLUSIVE_INCLUSIVE
        )
        actionBar?.title = title
    }

    private fun getColorInt(resourceId: Int): Int {
        return if (Build.VERSION.SDK_INT >= 23) {
            resources.getColor(resourceId, theme)
        } else {
            resources.getColor(resourceId)
        }
    }
}