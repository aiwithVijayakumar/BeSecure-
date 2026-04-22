package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.excessScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.ExcessPackage
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WaybillSectionExcessCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WithoutStickerPkg

@Composable
fun VehicleReleaseExcessScreen(
    onExcessPkgClick: () -> Unit
) {
    var isExcessExpanded = remember { mutableStateOf(false) }
    var isWithoutStickerExpanded = remember { mutableStateOf(false) }
    var isTabExcess = remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = MaterialTheme.dimens.large2,
                    end = MaterialTheme.dimens.large2,
                    bottom = MaterialTheme.dimens.spacingLarge10
                ) // add space for button
        ) {
            item {
                WaybillSectionExcessCard(
                    title = "Excess Packages",
                    count = 5,
                    isExpanded = isExcessExpanded.value,
                    isTabExcess = isTabExcess.value,
                    onToggleExpand = {
                        isExcessExpanded.value = !isExcessExpanded.value
                    }
                ) {
                    ExcessPackage(
                        isTabExcess = true, onTabExcessClick = {
                            onExcessPkgClick()
                        }
                    )
                }
            }

            item {
                WaybillSectionExcessCard(
                    title = "Without Sticker Pkgs",
                    count = 2,
                    isExpanded = isWithoutStickerExpanded.value,
                    isTabExcess = true,
                    onToggleExpand = {
                        isWithoutStickerExpanded.value = !isWithoutStickerExpanded.value
                    }
                ) {
                    WithoutStickerPkg()
                }
            }
        }

//        Box(
//            modifier = Modifier
//                .height(MaterialTheme.dimens.spacingLarge9)
//                .fillMaxWidth()
//                .align(Alignment.BottomCenter)
//                .background(buttonBackground)
//        ) {
//            SlideToBookButton(
//                btnText = "Slide To Proceed",
//                onBtnSwipe = {
//                    onItemClick(AppStrings.EXCESS)
//
//                },
//                modifier = Modifier.padding(
//                    horizontal = MaterialTheme.dimens.extraLarge1,
//                    vertical = MaterialTheme.dimens.spacingSmall3
//                ),
//                isEnabled = true
//            )
//        }
    }
}
