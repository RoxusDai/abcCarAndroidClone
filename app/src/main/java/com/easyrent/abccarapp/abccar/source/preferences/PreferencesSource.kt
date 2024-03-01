package com.easyrent.abccarapp.abccar.source.preferences

import kotlinx.coroutines.flow.Flow

interface PreferencesSource {

    suspend fun setOriginalToken(token: String)
    fun getToken(): Flow<String>
    suspend fun clearPreference()

}