package com.tagaev.myapplication.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.arstagaev.flowble.BLEStarter.Companion.scanDevices
import com.arstagaev.flowble.models.ScannedDevice
import com.tagaev.myapplication.Variables.allPackets
import com.tagaev.myapplication.Variables.countPacketsPerSecond
import com.tagaev.myapplication.Variables.measureAverageX
import com.tagaev.myapplication.Variables.measureAverageY
import com.tagaev.myapplication.Variables.measureAverageZ
import com.tagaev.myapplication.Variables.measureMaxX
import com.tagaev.myapplication.Variables.measureMaxY
import com.tagaev.myapplication.Variables.measureMaxZ
import com.tagaev.myapplication.Variables.measureMinX
import com.tagaev.myapplication.Variables.measureMinY
import com.tagaev.myapplication.Variables.measureMinZ

// Data model for a BLE device.
data class BLEDevice(
    val name: String,
    val macAddress: String,
    val rssi: Int,
    //val isConnected: Boolean
)

// A composable for an individual BLE device item.
@Composable
fun BLEDeviceItem(
    device: BLEDevice,
    onConnectClicked: (BLEDevice) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Device details
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Name: ${device.name}")
            Text(text = "MAC: ${device.macAddress}")
            Text(text = "RSSI: ${device.rssi}")
            //Text(text = "Status: ${if (device.isConnected) "Connected" else "Disconnected"}")
        }
        // Button to connect/disconnect.
        Button(onClick = { onConnectClicked(device) }) {
            Text(text = "Connect")
        }
    }
}

// The BLE device list screen.
@SuppressLint("MissingPermission", "StateFlowValueCalledInComposition")
@Composable
fun BLEDeviceListScreen(
    bleDevices: State<ArrayList<ScannedDevice>>,
    onConnectClicked: (BLEDevice) -> Unit
) {
    // Collect the latest scanned devices as Compose state.
    val scannedDevices by scanDevices.collectAsState()

    // Map the ScannedDevice list to BLEDevice list.
    val bleDevices = mapScannedDevicesToBLEDevices(scannedDevices)

    Column {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(bleDevices) { device ->
                BLEDeviceItem(device = device, onConnectClicked = onConnectClicked)
                HorizontalDivider()
            }
//            items(bleDevices.value) { index ->
//                BLEDeviceItem(device = BLEDevice(
//                    name = bleDevices.value[index].bt.name,
//                    macAddress = bleDevices.value[index].bt.address,
//                    rssi = bleDevices.value[index].rssi,
//                    //isConnected = bleDevices.value[index].bt.,
//                ), onConnectClicked = onConnectClicked)
//                HorizontalDivider()
//            }
        }
    }
}

@SuppressLint("MissingPermission")
fun mapScannedDevicesToBLEDevices(
    scannedDevices: List<ScannedDevice>,
    connectedDeviceMacs: Set<String> = emptySet()
): List<BLEDevice> {
    return scannedDevices.map { scannedDevice ->
        BLEDevice(
            name = scannedDevice.bt.name ?: "Unknown",
            macAddress = scannedDevice.bt.address,
            rssi = scannedDevice.rssi,
        )
    }
}

@SuppressLint("MissingPermission")
fun mapScannedDeviceToBLEDevice(scannedDevice: ScannedDevice, isConnected: Boolean = false): BLEDevice {
    return BLEDevice(
        name = scannedDevice.bt.name ?: "Unknown",
        macAddress = scannedDevice.bt.address,
        rssi = scannedDevice.rssi,
        //isConnected = isConnected
    )
}


//@Preview(showBackground = true)
//@Composable
//fun BLEDeviceListScreenPreview() {
//    // Example devices for preview.
//    val devices = listOf(
//        BLEDevice("Sensor 1", "AA:BB:CC:DD:EE:01", -60),
//        BLEDevice("Sensor 2", "AA:BB:CC:DD:EE:02", -70),
//        BLEDevice("Sensor 3", "AA:BB:CC:DD:EE:03", -65)
//    )
//    BLEDeviceListScreen(
//        bleDevices = devices,
//        onConnectClicked = { device ->
//            // Here, add your logic to connect/disconnect the device.
//            println("Clicked on: ${device.name}")
//        }
//    )
//}
