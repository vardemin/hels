package com.vardemin.hels

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
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
                HelsLog.d(titleState, bodyState)
            }
        ) {
            Text(text = "Log debug event")
        }
        Button(
            onClick = {
                HelsLog.i(titleState, bodyState)
            }
        ) {
            Text(text = "Log info event")
        }
    }
}