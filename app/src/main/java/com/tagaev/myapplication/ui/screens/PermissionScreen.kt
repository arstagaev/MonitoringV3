package com.tagaev.myapplication.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.arstagaev.flowble.extensions.requestPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MissingPermissionsComponent(
    content: @Composable () -> Unit,
    onResult: (Boolean) -> Unit
) {
    var indexOfPermission = 0


    val permissionsState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        rememberMultiplePermissionsState(
            permissions = listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,

    //                Manifest.permission.MANAGE_EXTERNAL_STORAGE,


            ),
        )
    } else {
        TODO("VERSION.SDK_INT < S")
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
        onResult
    )
    val context = LocalContext.current



    if (permissionsState.allPermissionsGranted) {
        println("ALL PERMISSION GRANTED")
        content()
    } else {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            launcher.launch(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
//        }
        Box(Modifier.fillMaxSize()) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .align(Alignment.Center),
                colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color.Blue),
                onClick = {

                    permissionsState.permissions.getOrNull(indexOfPermission)?.permission?.let {
                        println("Try to request permission $it")
                        launcher.launch(it)
                    }
                    indexOfPermission++
                    //permissionsState.launchMultiplePermissionRequest() // 7.
                }
            ) {
                Text(text = "Request permissions (${permissionsState.permissions.size})")
            }
        }

    }
}