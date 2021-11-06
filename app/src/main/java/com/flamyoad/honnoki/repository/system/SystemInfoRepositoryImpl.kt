package com.flamyoad.honnoki.repository.system

import android.content.Context
import kotlinx.coroutines.flow.Flow

class SystemInfoRepositoryImpl(private val context: Context): SystemInfoRepository {

    override fun getCurrentBatteryPercentage(): Flow<Int> {
        TODO("Not yet implemented")
    }

    override fun getNetworkCondition(): Flow<String> {
        TODO("Not yet implemented")
    }
}