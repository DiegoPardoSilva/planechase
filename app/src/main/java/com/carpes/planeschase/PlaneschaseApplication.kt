package com.carpes.planeschase

import android.app.Application
import com.carpes.planeschase.data.local.PlaneschaseDatabase
import com.carpes.planeschase.data.seeder.LocalSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaneschaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            LocalSeeder(
                context = this@PlaneschaseApplication,
                db = PlaneschaseDatabase.getInstance(this@PlaneschaseApplication),
                assets = assets,
            ).seedIfEmpty()
        }
    }
}
