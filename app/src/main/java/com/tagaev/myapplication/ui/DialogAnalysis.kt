package com.tagaev.myapplication.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.json.JSONObject

@Composable
fun ConfigDialogJson(
    context: Context,
    onDismiss: () -> Unit
) {
    // Obtain SharedPreferences.
    val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    // Remember states for toggles and text fields.
    var toggle1 by remember { mutableStateOf(false) }
    var toggle2 by remember { mutableStateOf(false) }
    var toggle3 by remember { mutableStateOf(false) }

    var text1 by remember { mutableStateOf("") }
    var text2 by remember { mutableStateOf("") }
    var text3 by remember { mutableStateOf("") }
    var text4 by remember { mutableStateOf("") }
    var text5 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configuration") },
        text = {
            // Use LazyColumn in a Box with a maximum height.
            Box(modifier = Modifier.heightIn(max = 400.dp)) {
                LazyColumn {
                    // Toggles
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Toggle 1", modifier = Modifier.weight(1f))
                            Switch(checked = toggle1, onCheckedChange = { toggle1 = it })
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Toggle 2", modifier = Modifier.weight(1f))
                            Switch(checked = toggle2, onCheckedChange = { toggle2 = it })
                        }
                    }
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Text("Toggle 3", modifier = Modifier.weight(1f))
                            Switch(checked = toggle3, onCheckedChange = { toggle3 = it })
                        }
                    }
                    // Spacer between toggles and text fields.
                    item { Spacer(modifier = Modifier.height(16.dp)) }
                    // Text fields
                    item {
                        OutlinedTextField(
                            value = text1,
                            onValueChange = { text1 = it },
                            label = { Text("Text Field 1") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = text2,
                            onValueChange = { text2 = it },
                            label = { Text("Text Field 2") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = text3,
                            onValueChange = { text3 = it },
                            label = { Text("Text Field 3") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = text4,
                            onValueChange = { text4 = it },
                            label = { Text("Text Field 4") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = text5,
                            onValueChange = { text5 = it },
                            label = { Text("Text Field 5") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                // Build a JSON object with all the configuration parameters.
                val configJson = JSONObject().apply {
                    put("toggle1", toggle1)
                    put("toggle2", toggle2)
                    put("toggle3", toggle3)
                    put("text1", text1)
                    put("text2", text2)
                    put("text3", text3)
                    put("text4", text4)
                    put("text5", text5)
                }
                // Save the JSON as a string using one SharedPreferences key.
                editor.putString("AnalysisMonitoring", configJson.toString())
                editor.apply()
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


//fun getConfig(Name)