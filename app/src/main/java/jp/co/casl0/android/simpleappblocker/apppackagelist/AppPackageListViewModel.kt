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

package jp.co.casl0.android.simpleappblocker.apppackagelist

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.co.casl0.android.simpleappblocker.PackageInfo

class AppPackageListViewModel : ViewModel() {
    private val _packageInfoList = MutableLiveData<MutableList<PackageInfo>>(null)
    val packageInfoList: LiveData<MutableList<PackageInfo>>
        get() = _packageInfoList

    fun loadInstalledPackages(context: Context?) {
        val tmp: MutableList<PackageInfo> = mutableListOf()
        val pm = context?.packageManager
        if (pm != null) {
            pm.getInstalledApplications(0).forEach {
                tmp.add(
                    PackageInfo(
                        it.loadIcon(pm),
                        it.loadLabel(pm).toString(),
                        it.packageName
                    )
                )
            }
            _packageInfoList.postValue(tmp)
        }
    }

}