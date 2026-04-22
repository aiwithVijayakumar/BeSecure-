package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.withoutStickerScreen

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.component.scanning.PackagePrintList
import com.tnd_psm.core.ui.component.scanning.PrintDialogScreen
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.getScannerIntent
import com.tnd_psm.core.utils.rememberScannerLauncher
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.AddButton
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.UpdateWithoutStickerCard


@Composable
fun UpdateWithoutStickerScreen(
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    val pkgText = remember { mutableStateOf("") }
    val remarkText = remember { mutableStateOf("") }
    val selectedImages = remember { mutableStateListOf<Uri>() }

    val context = LocalContext.current
    val scannerLauncher = rememberScannerLauncher { images, _ ->
        selectedImages.clear()
        selectedImages.addAll(images)
    }

    val isButtonEnable =
        pkgText.value.isNotEmpty() && remarkText.value.isNotEmpty() && selectedImages.isNotEmpty()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // No padding on AppTopBar
            AppTopBar(
                title = "Update without Sticker",
                onBackClick = { onBackClick() },
                notificationIconVis = false,
                menuIconVis = false
            )

            // Scrollable content with padding (if needed)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        start = MaterialTheme.dimens.large2,
                        end = MaterialTheme.dimens.large2,
                        bottom = MaterialTheme.dimens.spacingLarge9 // keep space for bottom bar
                    )
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))

                UpdateWithoutStickerCard(
                    pkgText = pkgText,
                    remarkText = remarkText,
                    selectedImages = selectedImages, // this is your mutableStateListOf<Uri>
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
                    },
                    onRemoveClick = { uri -> selectedImages.remove(uri) }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))
                AddButton(onAddClick = {

                })
            }
        }

        // Bottom fixed SlideToBookButton
        Box(
            modifier = Modifier
                .height(MaterialTheme.dimens.spacingLarge9)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = "Slide To Submit",
                onBtnSwipe = {
                    onButtonClick()
                },
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.extraLarge1,
                    vertical = MaterialTheme.dimens.spacingSmall3
                ),
                isEnabled = isButtonEnable
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowPackagePrintListBottomSheet(unitBottomSheet: MutableState<Boolean>) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            // Block collapse
            value != SheetValue.Hidden
        }
    )
    val printerBottomSheet = remember { mutableStateOf(false) }

    val isPrintSuccess = remember { mutableStateOf(false) }

    if (unitBottomSheet.value) {
        CommonBottomSheet(
            sheetState = sheetState,
            showBottomSheet = unitBottomSheet,
            content = {
                PackagePrintList("Unit Scanned: ", onPrintClick = {
                    printerBottomSheet.value = true
                })

            },
            onDismissed = {
                unitBottomSheet.value = false
            }, heightFraction = 0.77f
        )

    }
    if (printerBottomSheet.value) {
        CommonBottomSheet(
            sheetState = sheetState,
            showBottomSheet = printerBottomSheet,
            content = {
                if (isPrintSuccess.value == true) {
                    BottomSheetSuccess("Sticker Sent for Printing", R.drawable.ic_print_success)
                } else {
                    PrintDialogScreen("987654326711005") {
                        isPrintSuccess.value = true
                    }
                }
            },
            onDismissed = {
                printerBottomSheet.value = false
                isPrintSuccess.value = false
            }, headline = true
        )
    }
}
