package com.gabriel.financas

import android.app.Application
import com.gabriel.financas.domain.repository.FinanceRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FinancasApp : Application() {
    @Inject lateinit var repository: FinanceRepository

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch { repository.seedDefaultsIfEmpty() }
    }
}
