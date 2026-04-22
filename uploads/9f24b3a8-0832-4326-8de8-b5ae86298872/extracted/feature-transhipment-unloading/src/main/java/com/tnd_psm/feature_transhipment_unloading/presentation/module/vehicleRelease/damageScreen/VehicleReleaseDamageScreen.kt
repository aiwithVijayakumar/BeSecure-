package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.damageScreen

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
import com.tnd_psm.feature_transhipment_unloading.data.model.vehicleRelease.WayBillDamageModel
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WaybillCardDamage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleReleaseDamageScreen() {
    val isDamageExpanded = remember { mutableStateMapOf<Int, Boolean>() }

//    val waybillDamageList = listOf<WayBillDamageModel>()
    val waybillDamageList = listOf(
        WayBillDamageModel("987654327836", "Patna"),
        WayBillDamageModel("987654327836", "Patna"),
        WayBillDamageModel("987654327836", "Patna"),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        if (waybillDamageList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = MaterialTheme.dimens.large2,
                        end = MaterialTheme.dimens.large2,
                        bottom = MaterialTheme.dimens.spacingLarge10
                    ) // add space for button
            ) {
                itemsIndexed(waybillDamageList) { index, item ->
                    val isExpanded = isDamageExpanded[index] ?: false

                    WaybillCardDamage(
                        item = item,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            isDamageExpanded[index] = !isExpanded
                        }
                    )
                }
            }
        } else {
            NoDataFound("No Damage Package Found")
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
//                    onItemClick(AppStrings.DAMAGE)
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



