package com.tagaev.myapplication

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.arstagaev.flowble.BLEStarter
import com.arstagaev.flowble.BleParameters
import com.arstagaev.flowble.enums.Connect
import com.arstagaev.flowble.enums.Disconnect
import com.arstagaev.flowble.enums.EnableNotifications
import com.arstagaev.flowble.enums.Retard
import com.arstagaev.flowble.enums.StartScan
import com.arstagaev.flowble.enums.StopScan
import com.arstagaev.flowble.extensions.requestPermission
import com.arstagaev.flowble.gentelman_kit.bytesToHex
import com.arstagaev.flowble.gentelman_kit.logInfo
import com.arstagaev.flowble.gentelman_kit.logWarning
import com.arstagaev.flowble.gentelman_kit.toast
import com.tagaev.myapplication.Variables.CHART_ARRAY_SIZE
import com.tagaev.myapplication.Variables.MEASURMENT_ARRAY_SIZE
import com.tagaev.myapplication.Variables.NAME_OF_LOG_FILE
import com.tagaev.myapplication.Variables.accChangesArray
import com.tagaev.myapplication.Variables.allPackets
import com.tagaev.myapplication.Variables.countPacketsPerSecond
import com.tagaev.myapplication.Variables.currentScreen
import com.tagaev.myapplication.Variables.lastXAccelerations
import com.tagaev.myapplication.Variables.lastYAccelerations
import com.tagaev.myapplication.Variables.lastZAccelerations
import com.tagaev.myapplication.Variables.measureAverageX
import com.tagaev.myapplication.Variables.measureAverageY
import com.tagaev.myapplication.Variables.measureAverageZ
import com.tagaev.myapplication.Variables.measureMaxX
import com.tagaev.myapplication.Variables.measureMaxY
import com.tagaev.myapplication.Variables.measureMaxZ
import com.tagaev.myapplication.Variables.measureMinX
import com.tagaev.myapplication.Variables.measureMinY
import com.tagaev.myapplication.Variables.measureMinZ
import com.tagaev.myapplication.Variables.measureValueX
import com.tagaev.myapplication.Variables.measureValueY
import com.tagaev.myapplication.Variables.measureValueZ
import com.tagaev.myapplication.Variables.recState
import com.tagaev.myapplication.ble.BleDevices
import com.tagaev.myapplication.domain.RecState
import com.tagaev.myapplication.ui.screens.AnalysisScreen
import com.tagaev.myapplication.ui.screens.BLEDeviceListScreen
import com.tagaev.myapplication.ui.screens.ChartScreen
import com.tagaev.myapplication.ui.screens.MissingPermissionsComponent
import com.tagaev.myapplication.ui.screens.Screen.*
import com.tagaev.myapplication.ui.theme.ColorBackground1
import com.tagaev.myapplication.ui.theme.MonitoringV3Theme
import com.tagaev.myapplication.ui.theme.textWhiterColor2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    var bleStarter : BLEStarter? = null
    val corScope = CoroutineScope(lifecycleScope.coroutineContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }

        //////DEMOOO
//        corScope.launch {
//            BLEStarter.bleCommandTrain.emit(mutableListOf(
//                Retard(200),
//                Connect(address = "94:B9:7E:E8:E6:C2"),
//                Retard(1000L),
//                EnableNotifications(BleDevices.EMULATOR1.address, characteristicUuid = UUID.fromString(
//                    BleDevices.EMULATOR1.uuid
//                    //"ca73b3ba-39f6-4ab3-91ae-186dc9577d99"
//                )),
//            ))
//            currentScreen.value = CHART
//        }

        ////////////////////////////////////

        val maxPoints = 100

        // DEMO GENERATE:
//        corScope.launch {
//            while (true) {
//                if (accChangesArray.size > maxPoints) {
//                    accChangesArray.removeAt(0)
//                    println("DROP ${accChangesArray.size}")
//                }
//                accChangesArray.add(Random.nextInt(-10,10).toDouble())
//                println("adding  ${accChangesArray.size}")
//
//
//
//                measureMinZ.value = accChangesArray.min()
//
//                measureAverageZ.value = accChangesArray.average()
//
//                measureMaxZ.value = accChangesArray.max()
//
//
//                println(">>> ${BLEStarter.scanDevices.value.size}")
//                //////////////////////////////////////////////////////////////////////////////
//                delay(1000)
//            }
//        }

        launchLib()
        bleStarter = BLEStarter(this@MainActivity).also {
            it.showOperationToasts = true // show logs in Toast
        }
        corScope.launch {
            BLEStarter.scanDevices.collect {
                //logWarning("What I found: ${it}")
            }
        }

        corScope.launch {
            BLEStarter.outputBytesNotifyIndicate.collect {
                logWarning("Result: ${bytesToHex(it.value!!)} ${it.value?.decodeToString()} ${BleParameters.STATE_BLE}")
                val newData = it.value?.decodeToString()

                if (recState.value == RecState.RECORDING_MODE) {
                    newData?.let { it1 -> saveLog(it1+";${System.currentTimeMillis()}", NAME_OF_LOG_FILE) }
                }


                val splitedData = newData?.split(";")

                val newValueZ = splitedData?.getOrNull(2)?.toDoubleOrNull()

                countPacketsPerSecond.value++
                allPackets++

                if (newValueZ != null) {
                    if (accChangesArray.size > CHART_ARRAY_SIZE) {
                        accChangesArray.removeFirstOrNull()
                        //chartEntryModel.entries.get(0).get(0).y
                    }
                    //toChartCarrier.emit(newValueZ)
                    accChangesArray.add(newValueZ)

                    //measureValueZ.value = newValueZ

                    try {
                        splitedData.getOrNull(0)?.toDoubleOrNull()?.let { measureValueX.value = it }
                        splitedData.getOrNull(1)?.toDoubleOrNull()?.let { measureValueY.value = it }
                        splitedData.getOrNull(2)?.toDoubleOrNull()?.let { measureValueZ.value = it }

                        if (lastXAccelerations.size > MEASURMENT_ARRAY_SIZE) {
                            lastXAccelerations.removeFirstOrNull()
                            lastYAccelerations.removeFirstOrNull()
                            lastZAccelerations.removeFirstOrNull()
                        }

                        lastXAccelerations.add(measureValueX.value)
                        lastYAccelerations.add(measureValueY.value)
                        lastZAccelerations.add(measureValueZ.value)

                        // set new MAX
                        if (measureMaxX.value < measureValueX.value) {
                            measureMaxX.value = measureValueX.value
                        }
                        if (measureMaxY.value < measureValueY.value) {
                            measureMaxY.value = measureValueY.value
                        }
                        if (measureMaxZ.value < measureValueZ.value) {
                            measureMaxZ.value = measureValueZ.value
                        }

                        // set new MIN
                        if (measureMinX.value > measureValueX.value) {
                            measureMinX.value = measureValueX.value
                        }
                        if (measureMinY.value > measureValueY.value) {
                            measureMinY.value = measureValueY.value
                        }
                        if (measureMinZ.value > measureValueZ.value) {
                            measureMinZ.value = measureValueZ.value
                        }

                        measureAverageX.value = lastXAccelerations.average().toDouble()
                        measureAverageY.value = lastYAccelerations.average().toDouble()
                        measureAverageZ.value = lastZAccelerations.average().toDouble()


//                                                lastYAccelerations.maxOrNull()?.let {  measureMaxY.value = it }
//                                                lastZAccelerations.maxOrNull()?.let {  measureMaxZ.value = it }
//
//                                                lastXAccelerations.minOrNull()?.let {  measureMinX.value = it }
//                                                lastYAccelerations.minOrNull()?.let {  measureMinY.value = it }
//                                                lastZAccelerations.minOrNull()?.let {  measureMinZ.value = it }
                    }catch (e: Exception) {
                        println("ERROR: MainActivity: ${e.message} ")
                    }
                    //print("asd size: ${asd.size}")
                }
            }
        }
        CoroutineScope(lifecycleScope.coroutineContext).launch(Dispatchers.IO) {
            while (lifecycleScope.coroutineContext.isActive) {
                countPacketsPerSecond.value = 0
                delay(1000)
            }
        }

        setContent {
            val currentShowingScreen by rememberSaveable { currentScreen }

            MonitoringV3Theme {
                MissingPermissionsComponent(
                    content = {
                        Box(Modifier.fillMaxSize()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.DarkGray)
                                    .padding(vertical = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .weight(0.95f)
                                        .fillMaxWidth()
                                ) {
                                    when(currentShowingScreen) {
                                        SCANNER -> {
                                            BLEDeviceListScreen(
                                                bleDevices = BLEStarter.scanDevices.collectAsState(),
                                                onConnectClicked = {

                                                    corScope.launch {
                                                        BLEStarter.bleCommandTrain.emit(mutableListOf(
                                                            Retard(200),
                                                            Connect(address = it.macAddress),
                                                            Retard(1000L),
                                                            EnableNotifications(it.macAddress, characteristicUuid = UUID.fromString(
//                                                                BleDevices.EMULATOR1.uuid
                                                        BleDevices.AUTONOMOUS1.uuid
                                                            )),
                                                        ))
                                                        currentScreen.value = CHART

                                                    }
                                                }
                                            )
                                        }
                                        CHART -> {
                                            ChartScreen(
                                                items = accChangesArray, //Variables.accChangesArray,
                                                logCat = Variables.chatCanva,
                                                onMarkClicked = {

                                                },
                                                onStartClicked = {
                                                    recState.value = RecState.RECORDING_MODE
                                                    NAME_OF_LOG_FILE = getFileName()
                                                },
                                                onStopClicked = {
                                                    recState.value = RecState.STANDBY_MODE
                                                }
                                            )
                                        }
                                        ANALYSIS -> {
                                            AnalysisScreen()
                                        }
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(ColorBackground1)
                                        .weight(0.05f),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.clickable {
                                        currentScreen.value = SCANNER
                                    }) {
                                        Text("Scan", color = textWhiterColor2)
                                    }
                                    Box(modifier = Modifier.clickable {
                                        currentScreen.value = CHART
                                    }) {
                                        Text("Measurements", color = textWhiterColor2)
                                    }
                                    Box(modifier = Modifier.clickable {
                                        currentScreen.value = ANALYSIS
                                    }) {
                                        Text("Analysis", color = textWhiterColor2)
                                    }

                                }
                            }
                        }
                    },
                    onResult = {
                        toast("DONE!")

                    }
                )
            }
        }
    }


    ///////////////////////
    // How to use library:
    private fun launchLib() {
        if (!BLEStarter.isActive) {
            corScope.launch {
                BLEStarter.bleCommandTrain.emit(mutableListOf(
                    Retard(1000L),
                    StartScan(),
                    Retard(4000L),
                ))
            }
        }

    }

    private fun stopLib() {
        CoroutineScope(lifecycleScope.coroutineContext).launch {
            bleStarter?.forceStop()
        }
    }
}

