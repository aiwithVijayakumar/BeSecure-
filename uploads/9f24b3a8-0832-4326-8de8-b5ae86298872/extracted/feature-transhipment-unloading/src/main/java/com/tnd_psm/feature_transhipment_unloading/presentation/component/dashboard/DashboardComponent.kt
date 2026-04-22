package com.tnd_psm.feature_transhipment_unloading.presentation.component.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import com.tnd_psm.core.data.model.TabContentData
import com.tnd_psm.core.data.model.VehicleGroupData
import com.tnd_psm.core.ui.component.dashboardComponent.TabBar
import com.tnd_psm.core.ui.component.dashboardComponent.VehicleArrivedReportedSummary
import com.tnd_psm.core.ui.component.processCardComponent.ProgressStatus
import com.tnd_psm.core.utils.stringFile.AppStrings


fun getTabContentDataUnLoading(status: ProgressStatus): TabContentData {
    return when (status) {
        ProgressStatus.PENDING -> TabContentData(
            status = status,
            cardTitle = AppStrings.VEHICLE_ARRIVED_REPORTED,
            vehicleCount = 7,
            packageCount = 7500,
            totalWeight = 105,
            aboveHourData = VehicleGroupData(4, 4000, "60"),
            belowHourData = VehicleGroupData(3, 99999, "45")
        )

        ProgressStatus.PROGRESS -> TabContentData(
            status = status,
            cardTitle = AppStrings.UNLOADING_PROCESS_BEGINS,
            vehicleCount = 7,
            packageCount = 7500,
            totalWeight = 105,
            aboveHourData = VehicleGroupData(8, 4000, "90"),
            belowHourData = VehicleGroupData(3, 787, "45")
        )

        ProgressStatus.APPROVAL -> TabContentData(
            status = status,
            cardTitle = AppStrings.AWAITING_APPROVAL,
            vehicleCount = 7,
            packageCount = 7500,
            totalWeight = 105,
            aboveHourData = VehicleGroupData(4, 4000, "60"),
            belowHourData = VehicleGroupData(3, 454657, "45")
        )

        ProgressStatus.DOCUMENT_VERIFICATION_PENDING -> TODO()
    }
}

@Composable
fun TabScreenUnLoading(onStatusClick: (ProgressStatus, isAboveOrBelow: Boolean) -> Unit) {
    val selectedTab = remember { mutableIntStateOf(0) }
    val tabs = listOf(AppStrings.PENDING, AppStrings.IN_PROGRESS, AppStrings.APPROVAL)
    val statuses = listOf(ProgressStatus.PENDING, ProgressStatus.PROGRESS, ProgressStatus.APPROVAL)

    TabBar(
        tabTitles = tabs,
        selectedIndex = selectedTab.intValue,
        onTabSelected = { selectedTab.intValue = it }
    )

    val currentStatus = statuses.getOrElse(selectedTab.intValue) { ProgressStatus.PENDING }
    val contentData = getTabContentDataUnLoading(currentStatus)

    VehicleArrivedReportedSummary(
        vehicleCount = contentData.vehicleCount,
        packageCount = contentData.packageCount,
        totalWeight = contentData.totalWeight,
        status = contentData.status,
        cardTitle = contentData.cardTitle,
        aboveHourData = contentData.aboveHourData,
        belowHourData = contentData.belowHourData,
        onStatusClick = onStatusClick
    )
}
