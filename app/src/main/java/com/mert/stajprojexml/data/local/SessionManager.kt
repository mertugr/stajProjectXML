package com.mert.stajprojexml.data.local

import android.content.Context
import java.util.UUID

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("session", Context.MODE_PRIVATE)

    fun currentUserId(): String = prefs.getString(KEY_USER_ID, GUEST_ID) ?: GUEST_ID

    fun setCurrentUserId(id: String) {
        prefs.edit().putString(KEY_USER_ID, id).apply()
    }

    fun isGuest(): Boolean = currentUserId() == GUEST_ID

    companion object {
        const val GUEST_ID = "guest"
        private const val KEY_USER_ID = "current_user_id"
    }
}
