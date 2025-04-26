package com.example.petly.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.PermMedia
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.petly.R
import com.example.petly.utils.createImageFile
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerBottomSheet(
    onImageSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        "com.example.petly.provider",
        file
    )

    val cameraImageUri = remember { mutableStateOf(uri) }
    val shouldLaunchCamera = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            onImageSelected(cameraImageUri.value)
            Toast.makeText(context, "Foto realizada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Foto no realizada", Toast.LENGTH_SHORT).show()
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriGallery ->
        uriGallery?.let {
            onImageSelected(it)
            onDismiss()
            Toast.makeText(context, "Foto seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && shouldLaunchCamera.value) {
            cameraLauncher.launch(cameraImageUri.value)
            Toast.makeText(context, "Permiso de cámara aceptado", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
        onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp)
        ) {
            Text(text = stringResource(R.string.title_photoPickerBottomSheet))
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconSquare(
                        icon = Icons.Rounded.PermMedia,
                        sizeIcon = 40.dp,
                        modifier = Modifier.size(45.dp),
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            galleryLauncher.launch("image/*")
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(text = "Galería")
                }

                Spacer(modifier = Modifier.width(60.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconSquare(
                        icon = Icons.Rounded.CameraAlt,
                        sizeIcon = 40.dp,
                        modifier = Modifier.size(45.dp),
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                cameraLauncher.launch(cameraImageUri.value)
                                onDismiss()
                            } else {
                                shouldLaunchCamera.value = true
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(text = "Cámara")
                }
            }
        }
    }
}

