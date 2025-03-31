package com.tagaev.myapplication

import android.content.Context
import android.content.SharedPreferences


object SharedPreferenceMaestro {

    private var NAME = "AnalysisMonitoring"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    // list of app specific preferences
//    private val IS_FIRST_RUN_PREF = Pair("is_first_run", false)

    fun init(context: Context) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }



    /**
     * SharedPreferences extension function, so we won't need to call edit() and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var bondFeatureIs: Boolean
        // custom getter to get a preference of a desired type, with a predefined default value
        get() = preferences.getBoolean("bondfeature", true)
        // custom setter to save a preference back to preferences file
        set(value) = preferences.edit {
            it.putBoolean("bondfeature", value)
        }


    var analConfig: String
        get() = preferences.getString("AnalConfig", null).toString()
        set(value) = preferences.edit {
            it.putString("AnalConfig", value)
        }

}

fun getConfigParameter(context: Context, key: String): String? {
    // Access the SharedPreferences using the same file name used for saving configurations.
    val sharedPreferences = context.getSharedPreferences("AnalysisMonitoring", Context.MODE_PRIVATE)
    // Retrieve and return the parameter value using the provided key.
    return sharedPreferences.getString(key, null)
}