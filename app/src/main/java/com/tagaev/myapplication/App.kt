package com.tagaev.myapplication

import android.app.Application

class App : Application(){

    override fun onCreate() {
        super.onCreate()

        SharedPreferenceMaestro.init(applicationContext)

        configs()
    }

    fun configs() {
//        // Build configuration array with toggle values (as strings) and text field contents
//        val configArray = arrayOf(
//            toggle1.toString(),
//            toggle2.toString(),
//            toggle3.toString(),
//            text1,
//            text2,
//            text3,
//            text4,
//            text5
//        )
//        // For simplicity, store the configuration as a single comma-separated string
//        editor.putString("AnalConfig", configArray.joinToString(","))
//        editor.apply()
    }
}