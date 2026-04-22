package com.tnd_psm.feature_transhipment_unloading.presentation.module.markDamage.markdamageScreen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.CommonScanCard
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.White
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.getScannerIntent
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.core.utils.rememberScannerLauncher
import com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage.GreenInfoMarkDamage
import com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage.PackageDamageCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkDamageScreen(
    onBackClick: () -> Unit,
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

            GreenInfoMarkDamage("Tally# ")

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.dimens.large2,
                        vertical = MaterialTheme.dimens.small3
                    )
            ) {
                CommonScanCard(onScanClick = {
                    isPackageScanned.add("${isPackageScanned.size + 1}")
                })

                /*lazy column scan item*/
                Column(
                    modifier = Modifier.padding(
                        top = MaterialTheme.dimens.small3,
                        bottom = MaterialTheme.dimens.spacingLarge10
                    )
                ) {
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

        if (selectedImages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable {
                        onBackClick()
                    }
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
                    .padding(
                        start = MaterialTheme.dimens.large2,
                        end = MaterialTheme.dimens.large2,
                        bottom = MaterialTheme.dimens.small3
                    )
                    .background(GreenPrimary)
                    .padding(vertical = MaterialTheme.dimens.small3)

            ) {
                Text(
                    "SUBMIT",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

