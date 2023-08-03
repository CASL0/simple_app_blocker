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

package jp.co.casl0.android.simpleappblocker.core.data.datasource.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import jp.co.casl0.android.simpleappblocker.core.database.SimpleAppBlockerDatabase
import jp.co.casl0.android.simpleappblocker.core.model.DomainBlockedPacket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@SmallTest
class BlockedPacketsLocalDataSourceTest {
    private lateinit var localDataSource: BlockedPacketsLocalDataSource
    private lateinit var database: SimpleAppBlockerDatabase

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            SimpleAppBlockerDatabase::class.java
        ).allowMainThreadQueries().build()

        localDataSource = BlockedPacketsLocalDataSource(database)
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertBlockedPacket() = runTest {
        val blockedPacket = DomainBlockedPacket(
            "package1",
            "10.10.10.10",
            11111,
            "20.20.20.20",
            22222,
            "protocol",
            "2000-01-01"
        )
        var result = listOf<DomainBlockedPacket>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            localDataSource.getBlockedPacketsStream().collect {
                result = it
            }
        }

        localDataSource.insertBlockedPacket(blockedPacket)
        assertThat(result, `is`(listOf(blockedPacket)))
        job.cancel()
    }
}
