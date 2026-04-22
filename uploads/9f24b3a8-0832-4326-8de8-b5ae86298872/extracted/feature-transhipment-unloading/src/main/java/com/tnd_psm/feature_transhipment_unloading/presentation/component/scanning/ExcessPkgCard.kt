package com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WaybillSectionExcessCard
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WithoutStickerPkg

@Composable
fun ExcessPkgCard() {
    var isExcessExpanded = remember { mutableStateOf(false) }
    var isWithoutStickerExpanded = remember { mutableStateOf(false) }
    var isTabExcess = remember { mutableStateOf(true) }

    WaybillSectionExcessCard(
        title = "Excess Packages",
        count = 2,
        isExpanded = isExcessExpanded.value,
        isTabExcess = isTabExcess.value,
        onToggleExpand = {
            isExcessExpanded.value = !isExcessExpanded.value
        }
    ) {
        WithoutStickerPkg(

        )
    }
}