package com.tagaev.myapplication

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.tagaev.myapplication.ble.BleState
import com.tagaev.myapplication.domain.RecState
import com.tagaev.myapplication.ui.screens.BLEDevice
import com.tagaev.myapplication.ui.screens.Screen

object Variables {
    var currentScreen = mutableStateOf<Screen>(Screen.SCANNER)
    var bleState = mutableStateOf<BleState>(BleState.DISCONNECTED)
    var recState = mutableStateOf<RecState>(RecState.STANDBY_MODE)




    var listOfBLEDevices = mutableStateListOf<BLEDevice>(
        BLEDevice("Sensor 3", "AA:BB:CC:DD:EE:03", -65),
        BLEDevice("Sensor 3", "AA:BB:CC:DD:EE:03", -65)
    )

    val MEASURMENT_ARRAY_SIZE = 1000
    val CHART_ARRAY_SIZE = 500
    val MINIMAL_VALUE_SENSOR = 0f//-20_000f
    var NAME_OF_LOG_FILE = getFileName()

    var countPacketsPerSecond = mutableStateOf(0)
    var allPackets = 0

    // current value:
    var measureValueX = mutableStateOf(0.0)
    var measureValueY = mutableStateOf(0.0)
    var measureValueZ = mutableStateOf(0.0)

    var lastXAccelerations = mutableListOf<Double>()
    var lastYAccelerations = mutableListOf<Double>()
    var lastZAccelerations = mutableStateListOf<Double>()

    // max:
    var measureMaxX = mutableStateOf(0.0)
    var measureMaxY = mutableStateOf(0.0)
    var measureMaxZ = mutableStateOf(0.0)

    // min:
    var measureMinX = mutableStateOf(0.0)
    var measureMinY = mutableStateOf(0.0)
    var measureMinZ = mutableStateOf(0.0)

    // average:
    var measureAverageX = mutableStateOf(0.0)
    var measureAverageY = mutableStateOf(0.0)
    var measureAverageZ = mutableStateOf(0.0)

    var accChangesArray = mutableStateListOf<Double>(0.0,1.0,2.0,3.0,4.0,5.0)
    var logCat = mutableStateListOf<String>("")
    var chatCanva = mutableStateListOf<String>()

}