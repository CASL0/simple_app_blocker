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

package jp.co.casl0.android.simpleappblocker.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jp.co.casl0.android.simpleappblocker.core.common.di.IoDispatcher
import jp.co.casl0.android.simpleappblocker.core.database.SimpleAppBlockerDatabase
import jp.co.casl0.android.simpleappblocker.data.AllowlistDataSource
import jp.co.casl0.android.simpleappblocker.data.BlockedPacketsDataSource
import jp.co.casl0.android.simpleappblocker.data.InstalledApplicationDataSource
import jp.co.casl0.android.simpleappblocker.data.local.AllowlistLocalDataSource
import jp.co.casl0.android.simpleappblocker.data.local.BlockedPacketsLocalDataSource
import jp.co.casl0.android.simpleappblocker.data.local.InstalledApplicationLocalDataSource
import jp.co.casl0.android.simpleappblocker.repository.AllowlistRepository
import jp.co.casl0.android.simpleappblocker.repository.BlockedPacketsRepository
import jp.co.casl0.android.simpleappblocker.repository.InstalledApplicationRepository
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideAllowlistRepository(
        allowlistDataSource: AllowlistDataSource,
        @IoDispatcher defaultDispatcher: CoroutineDispatcher
    ): AllowlistRepository {
        return AllowlistRepository(allowlistDataSource, defaultDispatcher)
    }

    @Singleton
    @Provides
    fun provideInstalledApplicationRepository(
        installedApplicationDataSource: InstalledApplicationDataSource,
        @IoDispatcher defaultDispatcher: CoroutineDispatcher
    ): InstalledApplicationRepository {
        return InstalledApplicationRepository(installedApplicationDataSource, defaultDispatcher)
    }

    @Singleton
    @Provides
    fun provideBlockedPacketsRepository(
        blockedPacketsDataSource: BlockedPacketsDataSource,
        @IoDispatcher defaultDispatcher: CoroutineDispatcher
    ): BlockedPacketsRepository {
        return BlockedPacketsRepository(blockedPacketsDataSource, defaultDispatcher)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    fun provideAllowlistLocalDataSource(database: SimpleAppBlockerDatabase): AllowlistDataSource {
        return AllowlistLocalDataSource(database)
    }

    @Singleton
    @Provides
    fun provideInstalledApplicationLocalDataSource(@ApplicationContext context: Context): InstalledApplicationDataSource {
        return InstalledApplicationLocalDataSource(context)
    }

    @Singleton
    @Provides
    fun provideBlockedPacketsLocalDataSource(database: SimpleAppBlockerDatabase): BlockedPacketsDataSource {
        return BlockedPacketsLocalDataSource(database)
    }
}
