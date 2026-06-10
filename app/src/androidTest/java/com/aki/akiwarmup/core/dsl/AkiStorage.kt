package com.aki.akiwarmup.core.dsl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

// Khởi tạo DataStore instance (đảm bảo chỉ có một instance cho mỗi tên file)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aki_storage")

/**
 * Lớp bọc (wrapper) để cung cấp các hàm lưu trữ dễ dàng qua AkiContext.
 */
class AkiStorage(private val context: Context) {
    private val dataStore get() = context.dataStore

    // ----------------------------------------------------
    // String
    // ----------------------------------------------------
    suspend fun saveString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        dataStore.edit { prefs -> prefs[prefKey] = value }
    }

    suspend fun readString(key: String, defaultValue: String = ""): String {
        val prefKey = stringPreferencesKey(key)
        val prefs = dataStore.data.first()
        return prefs[prefKey] ?: defaultValue
    }

    // ----------------------------------------------------
    // Int
    // ----------------------------------------------------
    suspend fun saveInt(key: String, value: Int) {
        val prefKey = intPreferencesKey(key)
        dataStore.edit { prefs -> prefs[prefKey] = value }
    }

    suspend fun readInt(key: String, defaultValue: Int = 0): Int {
        val prefKey = intPreferencesKey(key)
        val prefs = dataStore.data.first()
        return prefs[prefKey] ?: defaultValue
    }

    // ----------------------------------------------------
    // Boolean
    // ----------------------------------------------------
    suspend fun saveBoolean(key: String, value: Boolean) {
        val prefKey = booleanPreferencesKey(key)
        dataStore.edit { prefs -> prefs[prefKey] = value }
    }

    suspend fun readBoolean(key: String, defaultValue: Boolean = false): Boolean {
        val prefKey = booleanPreferencesKey(key)
        val prefs = dataStore.data.first()
        return prefs[prefKey] ?: defaultValue
    }

    // ----------------------------------------------------
    // Xoá tất cả dữ liệu
    // ----------------------------------------------------
    suspend fun clearAll() {
        dataStore.edit { prefs -> prefs.clear() }
    }
}

/**
 * Extension helper để chạy các hàm storage một cách đồng bộ.
 * Tránh việc lặp code (Boilerplate) cho từng hàm.
 * 
 * Cách dùng: context.storage.sync { saveInt("key", 1) }
 */
fun <T> AkiStorage.sync(block: suspend AkiStorage.() -> T): T {
    return runBlocking { block() }
}
