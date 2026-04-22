package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleCheck

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.core.utils.stringFile.AppStrings.NO_OF_CHAS
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck.CaptureVehicleCheckCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck.DropdownCHASelector
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck.SealVerificationCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck.VehicleCheckStatus
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.CaptureImage

@Composable
fun TranshipmentUnloadingVehicleCheckScreen(
    onBackClick: () -> Unit,
    onSuccessBottomSheet: () -> Unit
) {
    var selectedCHA by remember { mutableStateOf("4") }
    BackHandler {
        onBackClick()
    }
    Surface(color = Color(0xFFF5F5F5)) {
        VehicleCheckScreen(
            { onBackClick() },
            onSuccessBottomSheet = onSuccessBottomSheet,
            selectedCHA = selectedCHA,
            onCHASelected = { selectedCHA = it },
            onVehicleImagesClick = { /* mock */ },
            onStackingImagesClick = {
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VehicleCheckScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedCHA: String = "4",
    onCHASelected: (String) -> Unit,
    onSuccessBottomSheet: () -> Unit,
    onVehicleImagesClick: () -> Unit,
    onStackingImagesClick: () -> Unit,
) {

    var isVehicleVerified = remember { mutableStateOf(false) }

    // Seal card state
    var isSealAvailable by remember { mutableStateOf<Boolean?>(true) }
    var sealNumber by remember { mutableStateOf("1234") }
    var isSealBroken by remember { mutableStateOf<Boolean?>(false) }
    var isButtonEnabled by remember { mutableStateOf<Boolean?>(false) }
    var showBottomSheet = remember { mutableStateOf<Boolean>(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            // Block collapse
            value != SheetValue.Hidden
        }
    )
    Box(
        modifier = modifier
            .fillMaxSize()

    ) {
        LazyColumn(modifier = Modifier.padding(bottom = MaterialTheme.dimens.spacingLarge13)) {
            stickyHeader {
                AppTopBar(title = AppStrings.TI_VEHICLE_CHECK, onBackClick = {
                    onBackClick()
                })
                VehicleCheckStatus()
            }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.dimens.large2)
                ) {
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                    CaptureVehicleCheckCard(1, isVehicleVerified, doesLabelChange = false)

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

                    DropdownCHASelector(
                        title = NO_OF_CHAS,
                        selected = selectedCHA,
                        onSelected = onCHASelected
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

                    SealVerificationCard(
                        isSealAvailable = isSealAvailable,
                        onSealAvailableChanged = { isSealAvailable = it },
                        sealNumber = sealNumber,
                        onSealNumberChanged = {
                            if (sealNumber.isNotEmpty()) {
                                isButtonEnabled = true
                            } else isButtonEnabled = false
                        },
                        isSealBroken = isSealBroken,
                        onSealBrokenChanged = { isSealBroken = it },
                        onUploadImage = { /* upload action */ }
                    )

                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
                            .clickable {
                                isVehicleVerified.value = true
                            }
                            .background(Color.White)
                            .padding(MaterialTheme.dimens.small3)
                    ) {
                        CaptureImage(
                            labelText = AppStrings.CAPTURE_STACKING_IMAGES,
                            cameraText = "",
                            isCameraTextVisible = true,
                            isVerified = false, isCameraClick = {
                                isVehicleVerified.value = true
                            }
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.spacingLarge9)
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = AppStrings.SLIDE_TO_SUBMIT,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.dimens.extraLarge1,
                        vertical = MaterialTheme.dimens.spacingSmall3
                    ),
                isEnabled = isButtonEnabled == true,
                onBtnSwipe = {
                    onSuccessBottomSheet()
                }
            )
        }
    }


    if (showBottomSheet.value) {
        CommonBottomSheet(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            content = {
                BottomSheetSuccess(
                    message = AppStrings.SUCCESS_VEHICLE_CHECK_MESSAGE,
                    appreciation = AppStrings.WELL_DONE
                )
            },
            onDismissed = {
                showBottomSheet.value = false
            },
            heightFraction = 0.5f
        )
    }
}
