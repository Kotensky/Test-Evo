package com.kotensky.testevo.managers

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(context: Context) {

    private val preferencesFileName = "test_evo.shared_preferences"

    private val IS_GRID_VIEW_TYPE = "is_grid_view_type"
    private val IS_DENIED_CALL_PERMISSION_REQUEST = "is_denied_call_permission_request"

    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences(preferencesFileName, Context.MODE_PRIVATE)
    }

    fun isGridViewType(): Boolean = sharedPreferences.getBoolean(IS_GRID_VIEW_TYPE, false)


    fun setIsGridViewType(isGridViewType: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_GRID_VIEW_TYPE, isGridViewType)
        editor.apply()
    }

    fun isDeniedCallPermissionRequest(): Boolean = sharedPreferences
            .getBoolean(IS_DENIED_CALL_PERMISSION_REQUEST, false)

    fun setDeniedCallPermissionRequest() {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_DENIED_CALL_PERMISSION_REQUEST, true)
        editor.apply()
    }

}