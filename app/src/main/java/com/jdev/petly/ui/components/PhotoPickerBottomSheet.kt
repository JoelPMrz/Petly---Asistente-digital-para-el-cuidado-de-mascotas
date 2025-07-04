package com.jdev.petly.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.jdev.petly.R
import com.jdev.petly.utils.createImageFile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoPickerBottomSheet(
    onImageSelected: (Uri) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val shouldLaunchCamera = remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
                onImageSelected(uri)
                Toast.makeText(context, context.getString(R.string.photo_taken), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, context.getString(R.string.photo_not_taken), Toast.LENGTH_SHORT).show()
        }
        onDismiss()
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uriGallery ->
        uriGallery?.let {
            onImageSelected(it)
            Toast.makeText(context, context.getString(R.string.selected_photo), Toast.LENGTH_SHORT).show()
            onDismiss()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted && shouldLaunchCamera.value) {
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                context,
                "com.example.petly.provider",
                file
            )
            cameraImageUri.value = uri
            cameraLauncher.launch(uri)
            shouldLaunchCamera.value = false
        } else {
            Toast.makeText(context, context.getString(R.string.camera_permission_denied), Toast.LENGTH_SHORT).show()
        }

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
            Text(text = stringResource(R.string.title_photoPickerBottomSheet), fontWeight = FontWeight.Medium)
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
                        modifier = Modifier.size(40.dp),
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            galleryLauncher.launch("image/*")
                        }
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(text = stringResource(R.string.gallery),fontWeight = FontWeight.Light, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(50.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconSquare(
                        icon = Icons.Rounded.CameraAlt,
                        sizeIcon = 40.dp,
                        modifier = Modifier.size(40.dp),
                        backgroundColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.primary,
                        onClick = {
                            val permissionCheck = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            )
                            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                val file = context.createImageFile()
                                val uri = FileProvider.getUriForFile(
                                    context,
                                    "com.example.petly.provider",
                                    file
                                )
                                cameraImageUri.value = uri
                                cameraLauncher.launch(uri)
                            } else {
                                shouldLaunchCamera.value = true
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    )
                    Spacer(Modifier.height(5.dp))
                    Text(text = stringResource(R.string.camera), fontWeight = FontWeight.Light, fontSize = 12.sp)
                }
            }
        }
    }
}

