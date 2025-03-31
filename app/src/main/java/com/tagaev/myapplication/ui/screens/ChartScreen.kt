package com.tagaev.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.tagaev.myapplication.Variables.accChangesArray
import com.tagaev.myapplication.Variables.allPackets
import com.tagaev.myapplication.Variables.chatCanva
import com.tagaev.myapplication.Variables.countPacketsPerSecond
import com.tagaev.myapplication.Variables.currentScreen
import com.tagaev.myapplication.Variables.measureAverageX
import com.tagaev.myapplication.Variables.measureAverageY
import com.tagaev.myapplication.Variables.measureAverageZ
import com.tagaev.myapplication.Variables.measureMaxX
import com.tagaev.myapplication.Variables.measureMaxY
import com.tagaev.myapplication.Variables.measureMaxZ
import com.tagaev.myapplication.Variables.measureMinX
import com.tagaev.myapplication.Variables.measureMinY
import com.tagaev.myapplication.Variables.measureMinZ
import com.tagaev.myapplication.domain.runCommand
import com.tagaev.myapplication.ui.parts.DrawChart
import com.tagaev.myapplication.ui.theme.ColorButton1

@Composable
fun ChartScreen(
    items: List<Double>,
    logCat: List<String>,
    onMarkClicked: () -> Unit,
    onStartClicked: () -> Unit,
    onStopClicked: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().background(Color.DarkGray),
            horizontalArrangement = Arrangement.spacedBy(8.dp) // adds 8.dp space between items
        ) {
            item {
                Box(
                    Modifier.width(150.dp).height(60.dp)
                        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Min: \n${measureMinX.value.toInt()};${measureMinY.value.toInt()};${measureMinZ.value.toInt()}",
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = Color.White
                    )
                }
            }

            item {
                Box(
                    Modifier.width(150.dp).height(60.dp)
                        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Avg: \n${measureAverageX.value.toInt()};${measureAverageY.value.toInt()};${measureAverageZ.value.toInt()}",
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = Color.White
                    )
                }
            }

            item {
                Box(
                    Modifier.width(150.dp).height(60.dp)
                        .background(Color.Blue, shape = RoundedCornerShape(10.dp))
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Max: \n${measureMaxX.value.toInt()};${measureMaxY.value.toInt()};${measureMaxZ.value.toInt()}",
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = Color.White
                    )
                }
            }

            item {
                Box(
                    Modifier.width(150.dp)
                        .background(Color.Blue, shape = RoundedCornerShape(20.dp))
                        .padding(horizontal = 2.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text("Packets: \n${countPacketsPerSecond.value}/$allPackets",
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                        color = Color.White
                    )
                }
            }
        }
        // Top half: Chart placeholder
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
//            DynamicAccelerationChart()
            DrawChart(
                accelerationData = items,
                modifier = Modifier.fillMaxSize().background(Color.LightGray)
            )
        }

        HorizontalDivider()

        // Bottom half: LazyColumn + Buttons
        var textCommand by remember { mutableStateOf(TextFieldValue("")) }

        Row(modifier = Modifier
            .weight(1f)
            .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight()
                    .padding(1.dp)
                    .background(Color.Gray)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(10f)
                        .fillMaxSize()
                        .padding(1.dp)
                        .background(Color.DarkGray)
                ) {
                    items(logCat) { item ->
                        Text(text = item.toString(), modifier = Modifier.padding(vertical = 8.dp), color = Color.White)
                    }
                }
                Row(
                    modifier = Modifier
                        .weight(2f)
                        .fillMaxSize(),
                ) {
                    TextField(
                        modifier = Modifier.fillMaxSize().weight(5f),
                        value = textCommand,
                        onValueChange = {
                            textCommand = it
                        },
//                        label = { Text(text = "Your textCommand") },
                        placeholder = { Text(text = "Text command") },
                    )
                    Box(modifier = Modifier.fillMaxSize().weight(1f).background(ColorButton1).align(Alignment.CenterVertically).clickable {

                        chatCanva.add(runCommand(textCommand.text))

                    }) {
                        Text(modifier = Modifier.align(Alignment.Center), text = "OK", color = Color.White,
                            fontSize = TextUnit(12f, TextUnitType.Sp))
                    }
                }
            }


            LazyColumn(
                modifier = Modifier
                    .wrapContentWidth()
                    .fillMaxHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Button(onClick = onMarkClicked, colors = ButtonColors(
                        containerColor = Color.Red, contentColor = Color.Black,
                        disabledContentColor = Color.DarkGray, disabledContainerColor = Color.LightGray)) { Text("ALERT") }
                }
                item { Button(onClick = {}) { Text("ON/OFF \nConnect") } }
                item { Button(onClick = onStartClicked) { Text("Start rec") } }
                item { Button(onClick = onStopClicked) { Text("Stop rec") } }
                item { Button(onClick = {
                    currentScreen.value = Screen.ANALYSIS
                }) { Text("Analysis") } }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChartScreenPreview() {
    ChartScreen(
        items = accChangesArray,
        logCat = listOf("Test log cat"),
        onMarkClicked = {},
        onStartClicked = {},
        onStopClicked = {}
    )
}