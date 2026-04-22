package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.addMarkDamage

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.CommonScanCard
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.getScannerIntent
import com.tnd_psm.core.utils.rememberScannerLauncher
import com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage.GreenInfoMarkDamage
import com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage.PackageDamageCard

@Composable
fun AddMarkDamageScreen(
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    val isPackageScanned = remember { mutableStateListOf<String>() }
    val isExpandable = remember { mutableStateOf(false) }

    val selectedImages = remember { mutableStateListOf<Uri>() }

    val context = LocalContext.current
    val scannerLauncher = rememberScannerLauncher { images, _ ->
        selectedImages.clear()
        selectedImages.addAll(images)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            AppTopBar(title = "Mark Damage", onBackClick = {
                onBackClick()
            }, onMenuIconClick = {}, notificationIconVis = false, menuIconVis = false)

            GreenInfoMarkDamage("Doc No. :")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.large2, vertical = 12.dp)
            ) {
                CommonScanCard(onScanClick = {
                    isPackageScanned.add("${isPackageScanned.size + 1}")
                })
                LazyColumn(modifier = Modifier.padding(top = 12.dp, bottom = 64.dp)) {
                    items(
                        isPackageScanned.size
                    ) { item ->
                        PackageDamageCard(
                            onCameraClick = {
                                getScannerIntent(
                                    context,
                                    onReady = { scannerLauncher.launch(it) },
                                    onError = { e ->
                                        Toast.makeText(
                                            context,
                                            "Error: ${e.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                )
                            }, selectedImages, isExpandable,
                            onRemoveClick = { uri ->
                                selectedImages.remove(uri)
                            })
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .height(62.dp)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = "Slide To Proceed",
                onBtnSwipe = {
                    onButtonClick()
                },
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.extraLarge1,
                    vertical = 11.dp
                ),
                isEnabled = selectedImages.isNotEmpty()
            )
        }
    }
}