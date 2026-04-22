package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleCheck

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
fun VehicleCheckStatus() {
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
            StatusTimelineVehicleCheck(isExpandableTimeLine, 2)
        }

    } else {
        Column(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth()
                .padding(
                    vertical = MaterialTheme.dimens.small1,
                    horizontal = MaterialTheme.dimens.large2
                )
        ) {
            PreviewTimelineVehicleCheck(isExpandableTimeLine)
        }
    }
}

@Composable
fun StatusTimelineVehicleCheck(isExpandableTimeLine: MutableState<Boolean>, isIndexSelected: Int) {
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
fun PreviewTimelineVehicleCheck(isExpandableTimeLine: MutableState<Boolean>) {
    val previewStatusList = listOf(
        TimelineData("ARRIVING", "12-AUG-2024", "9:30:02 AM", StatusType.ARRIVING),
        TimelineData("REPORTED", "12-AUG-2024", "10:05:02 AM", StatusType.REPORTED),
        TimelineData("VEHICLE VERIFIED", null, null, StatusType.VERIFIED),
    )


    PreviewTimelineCard(
        previewStatusList,
        isExpandableTimeLine,
        2,
        isPendingApproval = false,
        isLastItemCompleted = false, iconForStatusType = { type ->
            iconForStatusType(type as StatusType)
        }
    )
}