package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.removeExcessScreen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.HeaderSection
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.ExcessPackage
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.VehicleReleaseStatus
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WaybillSectionExcessCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WithoutStickerPkg
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen.MenuAction
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen.MenuDropDown
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.withoutStickerScreen.ShowPackagePrintListBottomSheet


@Composable
fun RemoveExcessPkgScreen(
    onBackClick: () -> Unit,
    onMenuActionClick: (MenuAction) -> Unit,
    onSubmitClick: () -> Unit,
    onBottomClick: () -> Unit
) {
    var isExcessExpanded = remember { mutableStateOf(false) }
    var isWithoutStickerExpanded = remember { mutableStateOf(false) }
    var isMenuDropDown = remember { mutableStateOf(false) }

    val isButtonEnabled = !isExcessExpanded.value
    var showPrintPackageList = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = "TI - Vehicle Release - HR03 IA 8373", onBackClick = {
            onBackClick()
        }, onMenuIconClick = {
            isMenuDropDown.value = true
        }, notificationIconVis = false, menuIconVis = true)
        VehicleReleaseStatus(4)
        HeaderSection("Remove Excess Reconsolation")

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = MaterialTheme.dimens.large2,
                        end = MaterialTheme.dimens.large2,
                        bottom = MaterialTheme.dimens.spacingLarge10
                    )
            ) {
                item {
                    WaybillSectionExcessCard(
                        title = "Excess Packages",
                        count = 5,
                        isExpanded = isExcessExpanded.value,
                        onToggleExpand = {
                            isExcessExpanded.value = !isExcessExpanded.value

                        }
                    ) {
                        ExcessPackage(onSubmitClick = {
                            onSubmitClick()

                        })
                    }
                }

                item {
                    WaybillSectionExcessCard(
                        title = "Without Sticker Pkgs",
                        count = 2,
                        isExpanded = isWithoutStickerExpanded.value,
                        onToggleExpand = {
                            isWithoutStickerExpanded.value = !isWithoutStickerExpanded.value
                        }
                    ) {
                        WithoutStickerPkg()
                    }
                }
            }

            Box(
                modifier = Modifier
                    .height(MaterialTheme.dimens.spacingLarge9)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(buttonBackground)
            ) {
                SlideToBookButton(
                    btnText = "Slide To Update",
                    onBtnSwipe = {
                        onBottomClick()
                    },
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.dimens.extraLarge1,
                        vertical = MaterialTheme.dimens.spacingSmall3
                    ),
                    isEnabled = isButtonEnabled
                )
            }
        }
    }
    MenuDropDown(isMenuDropDown) { action ->
        when (action) {
            MenuAction.PrintSticker -> {
                showPrintPackageList.value = true
            }

            else -> {
                onMenuActionClick(action)
            }
        }
    }

    ShowPackagePrintListBottomSheet(showPrintPackageList)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RemoveExcessPkgScreenPreview() {
    RemoveExcessPkgScreen(
        onBackClick = {}, onMenuActionClick = {}, onSubmitClick = {}, onBottomClick = {}
    )
}
