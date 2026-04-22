package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.tnd_psm.core.ui.component.processCardComponent.PreviewTimelineCard
import com.tnd_psm.core.ui.component.processCardComponent.StatusTimelineCard
import com.tnd_psm.core.ui.component.processCardComponent.StatusType
import com.tnd_psm.core.ui.component.processCardComponent.TimelineData
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.presentation.component.iconForStatusType

@Composable
fun VehicleReleaseStatus(isIndexSelected: Int) {
    val isExpandableTimeLine = remember { mutableStateOf(false) }

    if (!isExpandableTimeLine.value) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .shadow(spotColor = Color.White, elevation = MaterialTheme.dimens.borderThin)
                .padding(
                    horizontal = MaterialTheme.dimens.large2,
                    vertical = MaterialTheme.dimens.small1
                )
                .noRippleClickable {
                    isExpandableTimeLine.value = !isExpandableTimeLine.value
                }) {
            StatusTimelineVehicle(isExpandableTimeLine, isIndexSelected)
        }

    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = MaterialTheme.dimens.small1,
                    horizontal = MaterialTheme.dimens.large2
                )
        ) {
            PreviewTimelineVehicle(isExpandableTimeLine)
        }
    }
}

@Composable
fun StatusTimelineVehicle(isExpandableTimeLine: MutableState<Boolean>, isIndexSelected: Int) {
    val statusList = listOf(
        TimelineData(title = AppStrings.ARRIVAL, type = StatusType.ARRIVING),
        TimelineData(title = AppStrings.REPORT, type = StatusType.REPORTED),
        TimelineData(title = AppStrings.VEHICLE_VERIFIED, type = StatusType.VERIFIED),
        TimelineData(title = AppStrings.UNLOADING, type = StatusType.UNLOADING),
        TimelineData(title = AppStrings.VEHICLE_RELEASED, type = StatusType.VEHICLE_RELEASED),
    )

    StatusTimelineCard(
        statusList,
        isExpandableTimeLine,
        isIndexSelected,
        isLastItemCompleted = false,
        iconForStatusType = { type -> iconForStatusType(type as StatusType) })
}

@Composable
fun PreviewTimelineVehicle(isExpandableTimeLine: MutableState<Boolean>) {
    val previewStatusList = listOf(
        TimelineData("ARRIVING", "12-AUG-2024", "9:30:02 AM", StatusType.ARRIVING),
        TimelineData("REPORTED", "12-AUG-2024", "10:05:02 AM", StatusType.REPORTED),
        TimelineData("VEHICLE VERIFIED", "12-AUG-2024", "10:10:02 AM", StatusType.VERIFIED),
        TimelineData("FIRST SCANNED", "12-AUG-2024", "10:15:02 AM", StatusType.FIRST_SCANNED),
        TimelineData("LAST SCANNED", "12-AUG-2024", "10:25:02 AM", StatusType.LAST_SCANNED),
        TimelineData("UNLOADING", null, null, StatusType.UNLOADING),
        TimelineData("VEHICLE RELEASED", null, null, StatusType.VEHICLE_RELEASED)
    )


    PreviewTimelineCard(
        previewStatusList,
        isExpandableTimeLine,
        6,
        isPendingApproval = true,
        isLastItemCompleted = false, iconForStatusType = { type ->
            iconForStatusType(type as StatusType)
        }
    )
}