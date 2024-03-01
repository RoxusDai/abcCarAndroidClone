package com.easyrent.abccarapp.abccar.source.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 *  使用 Preferences DataStore 儲存App設定
 *
 *  DataStore可參考
 *   - https://developer.android.com/topic/libraries/architecture/datastore
 */

// 依附在Context的extension
private val Context.preferenceDataStore: DataStore<Preferences> by preferencesDataStore(name = "setting")

class PreferencesSourceImp(
    private val appContext: Context,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO // 預設是IO Thread
) : PreferencesSource {

    override suspend fun setOriginalToken(token: String) {
        withContext(ioDispatcher) {
            appContext.preferenceDataStore.edit { preferences ->
                preferences[ORIGINAL_TOKEN] = token
            }
        }
    }

    override fun getToken(): Flow<String> {
        return appContext.preferenceDataStore.data
            .map { preferences ->
                preferences[ORIGINAL_TOKEN] ?: ""
        }
    }

    // 登出清空Preference
    override suspend fun clearPreference() {
        appContext.preferenceDataStore.edit { preferences ->
            preferences.clear()
        }
    }


    companion object {
        // 欄位
        private val ORIGINAL_TOKEN = stringPreferencesKey("original_token")
    }

}