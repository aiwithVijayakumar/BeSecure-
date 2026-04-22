package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.shortScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.component.NoDataFound
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.data.model.vehicleRelease.WayBillModel
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WaybillCardShort

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleReleaseShortScreen() {
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    val waybillList = listOf(
        WayBillModel("987654327836", listOf("987654327836001", "987654327836002"), 2),
        WayBillModel("123456", listOf("WB123456001", "WB123456002"), 2),
        WayBillModel("12", listOf("WB123456001", "WB123456002"), 2),
    )

    Box(modifier = Modifier.fillMaxSize()) {
        if (waybillList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(
                    start = MaterialTheme.dimens.large2,
                    end = MaterialTheme.dimens.large2,
                    bottom = MaterialTheme.dimens.spacingLarge10
                )
            ) {

                itemsIndexed(waybillList) { index, item ->
                    val isExpanded = expandedStates[index] ?: false
                    WaybillCardShort(
                        item = item,
                        isExpanded = isExpanded,
                        isTabShort = true,
                        onToggleExpand = {
                            val newExpanded = !isExpanded
                            expandedStates[index] = newExpanded
                        }
                    )
                }
            }
        } else {
            NoDataFound("No Short Package Found")
        }

        // === Slide Button ===
//        Box(
//            modifier = Modifier
//                .height(MaterialTheme.dimens.spacingLarge9)
//                .fillMaxWidth()
//                .align(Alignment.BottomEnd)
//                .background(buttonBackground)
//        ) {
//            SlideToBookButton(
//                btnText = "Slide To Proceed",
//                onBtnSwipe = {
//                    onItemClick(AppStrings.SHORT)
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

