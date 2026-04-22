package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.ui.theme.Black
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.damageScreen.VehicleReleaseDamageScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.excessScreen.VehicleReleaseExcessScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.shortScreen.VehicleReleaseShortScreen

@Composable
fun TabScreen(onExcessPkgClick: () -> Unit) {
    var selectedTab = remember { mutableStateOf(AppStrings.SHORT) }

    val tabData = listOf(
        AppStrings.SHORT to 3,
        AppStrings.EXCESS to 3,
        AppStrings.DAMAGE to 1
    )

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(
                    vertical = MaterialTheme.dimens.spacingMedium4,
                    horizontal = MaterialTheme.dimens.spacingMedium8
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                AppStrings.VERIFY_PACKAGE_DETAILS,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        TabWithCountSelector(
            selectedTab = selectedTab.value,
            onTabSelected = { selectedTab.value = it },
            tabItems = tabData
        )

        when (selectedTab.value) {
            AppStrings.SHORT -> VehicleReleaseShortScreen()
            AppStrings.EXCESS -> VehicleReleaseExcessScreen(onExcessPkgClick = onExcessPkgClick)
            AppStrings.DAMAGE -> VehicleReleaseDamageScreen()
        }
    }
}


//@Composable
//fun TabScreen(navController: NavController) {
//    var selectedTab = remember { mutableStateOf(AppStrings.SHORT) }
//
//    val tabData = listOf(
//        AppStrings.SHORT to 3,
//        AppStrings.EXCESS to 3,
//        AppStrings.DAMAGE to 1
//    )
//
//    Column {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(GreenPrimary)
//                .padding(vertical = MaterialTheme.dimens.spacingMedium4, horizontal = MaterialTheme.dimens.spacingMedium8),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                AppStrings.VERIFY_PACKAGE_DETAILS,
//                style = MaterialTheme.typography.bodySmall.copy(
//                    fontWeight = FontWeight.Bold,
//                    color = Color.White
//                )
//            )
//        }
//    }
//
//    TabWithCountSelector(
//        selectedTab = selectedTab.value,
//        onTabSelected = {
//            selectedTab.value = it
//        },
//        tabItems = tabData
//    )
//
//    when (selectedTab.value) {
//        AppStrings.SHORT -> {
//            VehicleReleaseShortScreen(navController)
//        }
//
//        AppStrings.EXCESS -> {
//            VehicleReleaseExcessScreen(navController)
//        }
//
//        AppStrings.DAMAGE -> {
//            VehicleReleaseDamageScreen(navController = navController)
//        }
//    }
//}


@Composable
fun TabWithCountSelector(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    tabItems: List<Pair<String, Int>>
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(top = MaterialTheme.dimens.small1)
    ) {
        tabItems.forEach { (label, count) ->
            Column(
                modifier = Modifier
                    .weight(1f) // ⬅️ equally divide row into 3 parts
                    .noRippleClickable { onTabSelected(label) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selectedTab == label) GreenPrimary else Color.Black
                        ),
                    )
                    Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))
                    Box(
                        modifier = Modifier
                            .background(
                                if (selectedTab == label) GreenPrimary else Gray,
                                shape = RoundedCornerShape(50)
                            )
                            .padding(
                                horizontal = MaterialTheme.dimens.small,
                                vertical = MaterialTheme.dimens.borderThick
                            )
                    ) {
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.labelLarge.copy(color = if (selectedTab == label) Color.White else Black),
                        )
                    }
                }

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

                Box(
                    modifier = Modifier
                        .height(MaterialTheme.dimens.spacingMicro)
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.dimens.small2)
                        .background(
                            if (selectedTab == label) GreenPrimary else Color.Transparent,
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }
    }
}