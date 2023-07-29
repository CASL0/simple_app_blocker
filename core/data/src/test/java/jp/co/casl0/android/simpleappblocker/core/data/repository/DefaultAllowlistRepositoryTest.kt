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

package jp.co.casl0.android.simpleappblocker.core.data.repository

import jp.co.casl0.android.simpleappblocker.core.data.datasource.FakeAllowlistDataSource
import jp.co.casl0.android.simpleappblocker.core.model.DomainAllowedPackage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultAllowlistRepositoryTest {
    private lateinit var allowedPackages: List<DomainAllowedPackage>
    private lateinit var allowedPackagesDataSource: FakeAllowlistDataSource

    @Before
    fun setup() {
        val package1 = DomainAllowedPackage(packageName = "package1", appName = "app1")
        val package2 = DomainAllowedPackage(packageName = "package2", appName = "app2")
        val package3 = DomainAllowedPackage(packageName = "package3", appName = "app3")
        allowedPackages = listOf(package1, package2, package3)

        allowedPackagesDataSource = FakeAllowlistDataSource(allowedPackages = allowedPackages)
    }

    @Test
    fun insertAllowedPackage_addNewPackage() = runTest {
        val newPackage = DomainAllowedPackage(packageName = "newPackage", appName = "newApp")
        val allowlistRepository =
            DefaultAllowlistRepository(
                allowedPackagesDataSource,
                UnconfinedTestDispatcher(testScheduler)
            )

        allowlistRepository.insertAllowedPackage(
            newPackage.packageName.toString(),
            newPackage.appName.toString()
        )
        assertThat(
            allowlistRepository.getAllowlistStream().first(),
            `is`((allowedPackages + newPackage).map { it.packageName })
        )
    }

    @Test
    fun disallowPackage_removePackage3() = runTest {
        val packageToRemove = DomainAllowedPackage(packageName = "package3", appName = "app3")
        val allowlistRepository =
            DefaultAllowlistRepository(
                allowedPackagesDataSource,
                UnconfinedTestDispatcher(testScheduler)
            )

        allowlistRepository.disallowPackage(packageToRemove.packageName.toString())
        assertThat(
            allowlistRepository.getAllowlistStream().first(),
            `is`((allowedPackages - packageToRemove).map { it.packageName })
        )
    }
}
