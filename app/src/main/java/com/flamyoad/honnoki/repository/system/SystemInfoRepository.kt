package com.flamyoad.honnoki.repository.system

import kotlinx.coroutines.flow.Flow

interface SystemInfoRepository {
    fun getNetworkCondition(): Flow<String>
    fun getCurrentBatteryPercentage(): Flow<Int>
}