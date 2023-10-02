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

package jp.co.casl0.android.simpleappblocker.core.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class MigrationTest {
    private val TEST_DB = "migration-test"

    /** 初期データ投入用のSQL */
    private val SEED_SQL = """
        INSERT INTO 
            blocked_packets (package_name, src_address, src_port, dst_address, dst_port, protocol, blocked_at) 
        VALUES
            ('package1', '10.10.10.10', 100, '20.20.20.20', 200, 'protocol1', '2000-01-01 00:00:00')
        ;
    """.trimIndent()

    private val ALL_MIGRATIONS = arrayOf(
        MIGRATION_2_3
    )

    /** スキーマJSONファイルからDBを作成します */
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        SimpleAppBlockerDatabase::class.java
    )

    @Before
    fun setup() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL(SEED_SQL)
            close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true)
        val ret = db.query(
            """
            SELECT 
                app_name
            FROM
                blocked_packets
            WHERE
                package_name = 'package1'
        """.trimIndent()
        )

        ret.moveToFirst()

        assertThat(ret.getString(0), `is`(""))

        ret.close()
        db.close()
    }

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            SimpleAppBlockerDatabase::class.java,
            TEST_DB
        ).addMigrations(*ALL_MIGRATIONS).build().apply {
            openHelper.writableDatabase.close()
        }
    }
}
