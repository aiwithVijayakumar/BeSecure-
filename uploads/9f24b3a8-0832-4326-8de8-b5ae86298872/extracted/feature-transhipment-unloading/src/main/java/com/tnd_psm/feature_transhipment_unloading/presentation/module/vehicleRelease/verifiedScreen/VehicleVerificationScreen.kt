package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.verifiedScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings.SLIDE_TO_PROCEED
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.CaptureVehicleImageCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.VehicleReleaseStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleVerificationScreen(onBackClick: () -> Unit) {
    val isDialogDamageSuccessful = remember { mutableStateOf(true) }
    val isDialogSuccessVehicleRelease = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isContainerDamage = remember { mutableStateOf(false) }
    var isCameraClick = remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            AppTopBar(
                title = "TI - Vehicle Release - HR03 IA 8373",
                onBackClick = { onBackClick() }
            )
            VehicleReleaseStatus(4)
            CaptureVehicleImageCard(isContainerDamage, isCameraClick)
        }
        Box(
            modifier = Modifier
                .height(MaterialTheme.dimens.spacingLarge9)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = SLIDE_TO_PROCEED,
                onBtnSwipe = {
                    isDialogSuccessVehicleRelease.value = true
                },
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.extraLarge1,
                    vertical = MaterialTheme.dimens.spacingSmall3
                ),
                isEnabled = isContainerDamage.value
            )
        }

        if (isDialogDamageSuccessful.value) {
            CommonBottomSheet(
                isDialogDamageSuccessful,
                sheetState,
                content = { BottomSheetSuccess("Successfully updated!") }, onDismissed = {

                }
            )
        }
        if (isDialogSuccessVehicleRelease.value) {
            CommonBottomSheet(
                isDialogSuccessVehicleRelease,
                sheetState,
                content = {
                    BottomSheetSuccess(
                        "Successfully released the vehicle",
                        isVehicleDetail = true
                    )
                }, onDismissed = {}
            )
        }
    }
}

