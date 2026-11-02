package com.gabriel.financas.di

import android.content.Context
import androidx.room.Room
import com.gabriel.financas.data.local.AccountDao
import com.gabriel.financas.data.local.CategoryDao
import com.gabriel.financas.data.local.FinanceDatabase
import com.gabriel.financas.data.local.TransactionDao
import com.gabriel.financas.data.repository.FinanceRepositoryImpl
import com.gabriel.financas.domain.repository.FinanceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides @Singleton
    fun database(@ApplicationContext context: Context): FinanceDatabase =
        Room.databaseBuilder(context, FinanceDatabase::class.java, "financas.db").build()

    @Provides fun transactionDao(db: FinanceDatabase): TransactionDao = db.transactionDao()
    @Provides fun categoryDao(db: FinanceDatabase): CategoryDao = db.categoryDao()
    @Provides fun accountDao(db: FinanceDatabase): AccountDao = db.accountDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds abstract fun repository(impl: FinanceRepositoryImpl): FinanceRepository
}
