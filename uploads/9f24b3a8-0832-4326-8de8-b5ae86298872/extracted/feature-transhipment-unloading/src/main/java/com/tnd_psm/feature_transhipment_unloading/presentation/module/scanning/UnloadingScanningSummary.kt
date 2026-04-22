package com.tnd_psm.feature_transhipment_unloading.presentation.module.scanning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.CommonScanCard
import com.tnd_psm.core.ui.component.HeaderExcess
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.component.scanning.PackageDetailHeader
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning.ExcessPkgCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning.ScanningSummaryTabBar
import com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning.ShortPkgCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.ContentExcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnloadingScanningSummaryScreen(
    onBackClick: () -> Unit
) {

    val selectedTab = remember { mutableStateOf(AppStrings.SHORT) }
    val showSuccessBottomSheet = remember { mutableStateOf(false) }
    val tabData = listOf(
        AppStrings.SHORT to 1,
        AppStrings.EXCESS to 1,
        AppStrings.DAMAGE to 1
    )
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            // Block collapse
            value != SheetValue.Hidden
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Gray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 64.dp)
        ) {
            AppTopBar(
                title = "TI - Unloading Summary", onBackClick = {
                    onBackClick()
                }, onMenuIconClick = {

                }, notificationIconVis = false, menuIconVis = false,
                isNotificationCountVis = false
            )
            PackageDetailHeader(
                vehicleNo = "HR03 IA 8373",
                totalWb = "10",
                totalPkgs = 2000,
                scannedPkgs = 100
            )
            ScanningSummaryTabBar(tabData, selectedTab)
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))
            Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.spacingMedium11)) {

                when (selectedTab.value) {
                    AppStrings.SHORT -> {
                        HeaderExcess()
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))
                        ShortPkgCard("9876 54326721006") { }

                    }

                    AppStrings.EXCESS -> {
                        ExcessPkgCard()
                    }

                    AppStrings.DAMAGE -> {
                        CommonScanCard(title = "Scan to Add Damage", onScanClick = {})
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))
                        HeaderExcess()
                        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))
                        ContentExcess { }
                    }

                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
                .padding(vertical = 11.dp)
        ) {
            SlideToBookButton(
                btnText = AppStrings.SLIDE_TO_PROCEED,
                isEnabled = true,
                onBtnSwipe = {
                    showSuccessBottomSheet.value = true
                },
                modifier = Modifier.padding(horizontal = MaterialTheme.dimens.extraLarge1)
            )
        }
        if (showSuccessBottomSheet.value) {
            CommonBottomSheet(
                sheetState = sheetState,
                showBottomSheet = showSuccessBottomSheet,
                content = {
                    BottomSheetSuccess("Unloading Complete!\n Waiting for Team Leaders' approval")
                },
                onDismissed = {
                    showSuccessBottomSheet.value = false
                }, heightFraction = 0.5f
            )

        }
    }
}


