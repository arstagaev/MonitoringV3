package com.tagaev.myapplication

import android.os.Environment
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date

fun saveLog(msg: String, name: String) {
    val outStr1: String = "Programming Tutorials"
    println("NewFile: ${name}  and value: ${msg}")

    // Get the downloads directory
    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    // Create monitoring directory
    val monitoringDir = File(downloadsDir, "monitoring")

    // Create the directory if it doesn't exist
    if (!monitoringDir.exists()) {
        monitoringDir.mkdirs() // mkdirs() creates parent directories if needed
    }

    // Create file in the monitoring directory
    val file = File(monitoringDir, name)
    if (!file.exists()) {
        file.createNewFile()
    }

    val fw = FileWriter(file, true)
    val bw = BufferedWriter(fw)
    bw.write("$msg\n")
    bw.close()
}

fun getFileName(): String {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val sdf = SimpleDateFormat("HHmmss dd_MM_yyyy")

    val currentDateAndTime = sdf.format(Date())
    println("FILE NAME $$currentDateAndTime.txt")
    return "$currentDateAndTime.txt"//"${today.hour}_${today.minute}${today.second}_${today.dayOfMonth}${today.month}.txt"
}