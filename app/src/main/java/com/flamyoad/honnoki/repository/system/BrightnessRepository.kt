package com.flamyoad.honnoki.repository.system

import kotlinx.coroutines.flow.Flow

interface BrightnessRepository {
    fun getBrightness(): Flow<Float>

    fun getUserBrightness(): Flow<Float>
    fun setUserBrightness(value: Float)

    fun useSystemBrightness(): Flow<Boolean>
    fun setUseSystemBrightness(value: Boolean)
}