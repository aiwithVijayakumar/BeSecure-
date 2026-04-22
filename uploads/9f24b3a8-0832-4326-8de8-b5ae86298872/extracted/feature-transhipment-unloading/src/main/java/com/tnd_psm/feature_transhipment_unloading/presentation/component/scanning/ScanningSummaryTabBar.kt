package com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.TabWithCountSelector

@Composable
fun ScanningSummaryTabBar(tabData: List<Pair<String, Int>>, selectedTab: MutableState<String>) {

    TabWithCountSelector(
        selectedTab = selectedTab.value,
        onTabSelected = {
            selectedTab.value = it
        },
        tabItems = tabData
    )

}