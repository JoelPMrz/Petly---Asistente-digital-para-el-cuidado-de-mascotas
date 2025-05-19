package com.example.petly.ui.components

import android.app.TimePickerDialog
import android.widget.TimePicker
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.time.LocalTime

@Composable
fun BaseTimePicker(
    initialTime: LocalTime = LocalTime.now(),
    onDismissRequest: () -> Unit,
    onTimeSelected: (LocalTime) -> Unit
) {
    val context = LocalContext.current

    TimePickerDialog(
        context,
        { _: TimePicker, hour: Int, minute: Int ->
            onTimeSelected(LocalTime.of(hour, minute))
        },
        initialTime.hour,
        initialTime.minute,
        true
    ).apply {
        setOnCancelListener {
            onDismissRequest()
        }
    }.show()
}
