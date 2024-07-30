package com.vardemin.hels.android

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.vardemin.hels.HelServer
import com.vardemin.hels.HelsLog
import com.vardemin.hels.android.ui.NetworkClient
import kotlin.random.Random
import kotlin.random.nextInt

@Composable
fun InputScreen() {
    var titleState by remember { mutableStateOf("Title") }
    var bodyState by remember { mutableStateOf("Body") }
    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = titleState,
            onValueChange = {
                titleState = it
            },
            label = {
                Text(text = "Event title")
            }
        )
        OutlinedTextField(
            value = bodyState,
            onValueChange = {
                bodyState = it
            },
            label = {
                Text(text = "Event body")
            }
        )
        Button(
            onClick = {
                HelsLog.d(titleState, bodyState, mapOf(
                    "Version" to "23.4.1",
                    "Code" to Random.nextInt(200..1000).toString()
                ))
            }
        ) {
            Text(text = "Log debug event")
        }
        Button(
            onClick = {
                HelsLog.i(titleState, bodyState, mapOf(
                    "Version" to "23.4.1",
                    "Code" to Random.nextInt(200..1000).toString()
                ))
            }
        ) {
            Text(text = "Log info event")
        }
        Button(
            onClick = {
                NetworkClient.makeCall()
            }
        ) {
            Text(text = "Make API request")
        }
        Button(
            onClick = {
                HelServer.stop()
            }
        ) {
            Text(text = "Stop")
        }
        Button(
            onClick = {
                HelServer.start()
            }
        ) {
            Text(text = "Start")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InputScreenPreview() {
    InputScreen()
}