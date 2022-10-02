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

package jp.co.casl0.android.simpleappblocker.app

import android.app.Application
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import jp.co.casl0.android.simpleappblocker.repository.AllowlistRepository
import jp.co.casl0.android.simpleappblocker.database.AllowlistDatabase

class AppBlockerApplication : Application() {
    init {
        Logger.addLogAdapter(
            AndroidLogAdapter(
                PrettyFormatStrategy.newBuilder().tag("SimpleAppBlocker").build()
            )
        )
    }

    /**
     * 許可アプリ操作用のデータベースインスタンス
     */
    val database by lazy { AllowlistDatabase.getDatabase(this) }

    /**
     * 許可アプリリポジトリ
     */
    val repository by lazy { AllowlistRepository(database.allowlistDao()) }
}
