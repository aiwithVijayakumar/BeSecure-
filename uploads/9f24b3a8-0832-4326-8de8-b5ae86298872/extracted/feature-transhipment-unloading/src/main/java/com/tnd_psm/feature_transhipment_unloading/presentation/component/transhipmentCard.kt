package com.tnd_psm.feature_transhipment_unloading.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.zIndex
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.processCardComponent.EditAction
import com.tnd_psm.core.ui.component.processCardComponent.HeadingView
import com.tnd_psm.core.ui.component.processCardComponent.InfoSectionDetail
import com.tnd_psm.core.ui.component.processCardComponent.PreviewTimelineCard
import com.tnd_psm.core.ui.component.processCardComponent.ProgressStatus
import com.tnd_psm.core.ui.component.processCardComponent.RouteSection
import com.tnd_psm.core.ui.component.processCardComponent.ScanningCardData
import com.tnd_psm.core.ui.component.processCardComponent.StatusTimelineCard
import com.tnd_psm.core.ui.component.processCardComponent.StatusType
import com.tnd_psm.core.ui.component.processCardComponent.TeamLeaderActivityPending
import com.tnd_psm.core.ui.component.processCardComponent.TimelineData
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.cardGoldColor
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.feature_transhipment_unloading.presentation.component.dashboard.EditSection

@Composable
fun TranshipmentUnloadingCard(
    onAction: (EditAction) -> Unit,
    status: ProgressStatus,
    cardData: ScanningCardData,
    editableCardId: MutableState<String?>,
) {
    TranshipmentUnLoadingCardItem(
        data = cardData,
        status = status,
        onAction = onAction,
        editableCardId = editableCardId
    )
}

@Composable
fun TranshipmentUnLoadingCardItem(
    data: ScanningCardData,
    onAction: (EditAction) -> Unit,
    status: ProgressStatus,
    editableCardId: MutableState<String?>,
) {
    val measuredHeight = remember { mutableIntStateOf(0) }
    val isExpandableTimeLine = remember { mutableStateOf(false) }

    val isEditable = editableCardId.value == data.waybill

    // Auto disable edit mode if timeline is expanded
    LaunchedEffect(isExpandableTimeLine.value) {
        if (isExpandableTimeLine.value) {
            editableCardId.value = null
        }
    }

    Card(
        modifier = Modifier
            .then(
                if (!data.isTeamLeaderActivityPending && !data.isApprovalPending) {
                    Modifier.noRippleClickable {
                        if (!isExpandableTimeLine.value) {
                            editableCardId.value = if (isEditable) null else data.waybill
                        }
                    }
                } else Modifier
            )
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.dimens.large2,
                vertical = MaterialTheme.dimens.spacingExtraSmall
            ),
        shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            if (!isEditable) {
                Box(
                    modifier = Modifier
                        .width(MaterialTheme.dimens.small)
                        .fillMaxHeight()
                        .background(cardGoldColor)
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    if (data.isTeamLeaderActivityPending && status == ProgressStatus.PROGRESS) {
                        TeamLeaderActivityPending()
                    }

                    if (data.isApprovalPending && status == ProgressStatus.APPROVAL) {
                        TeamLeaderActivityPending("Waiting for Approval")
                    }

                    HeadingView(data.waybill, data.duration)

                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (!isEditable) {
                            Column(
                                modifier = Modifier
                                    .onGloballyPositioned {
                                        measuredHeight.intValue = it.size.height
                                    }
                                    .fillMaxWidth()
                                    .padding(
                                        start = MaterialTheme.dimens.small2,
                                        end = MaterialTheme.dimens.small2,
                                        bottom = MaterialTheme.dimens.small2
                                    )
                            ) {
                                RouteSection(data.route)
                                Spacer(modifier = Modifier.height(MaterialTheme.dimens.extraSmall))
                                InfoSectionDetail(data.infoStats, data.toBeScanned)
                                Spacer(modifier = Modifier.height(MaterialTheme.dimens.extraSmall))

                                if (!isExpandableTimeLine.value) {
                                    Row(
                                        modifier = Modifier
                                            .background(Gray)
                                            .clickable {
                                                isExpandableTimeLine.value =
                                                    !isExpandableTimeLine.value
                                            }
                                    ) {
                                        StatusTimeline(
                                            isExpandableTimeLine,
                                            status,
                                            if (status == ProgressStatus.PENDING) data.currentStepIndexStatusTimeLine else 1,
                                            isLastItemCompleted = status == ProgressStatus.PENDING
                                        )
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = MaterialTheme.dimens.small1)
                                    ) {
                                        PreviewTimeline(
                                            isExpandableTimeLine,
                                            status,
                                            if (status == ProgressStatus.PENDING) data.currentStepIndexStatusTimeLine else 1,
                                            isLastItemCompleted = status == ProgressStatus.PENDING
                                        )
                                    }
                                }
                            }
                        } else {
                            EditSection(measuredHeight, status, onAction)
                        }

                        if (data.isTeamLeaderActivityPending && status == ProgressStatus.PROGRESS || data.isApprovalPending && status == ProgressStatus.APPROVAL) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .background(Gray.copy(alpha = 0.5f))
                                    .zIndex(1f)
                                    .pointerInput(Unit) {}
                            )
                        }
//                        if () {
//                            Box(
//                                modifier = Modifier
//                                    .matchParentSize()
//                                    .background(Gray.copy(alpha = 0.5f))
//                                    .zIndex(1f)
//                                    .pointerInput(Unit) {}
//                            )
//                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusTimeline(
    isExpandableTimeLine: MutableState<Boolean>,
    status: ProgressStatus,
    pendingIndex: Int = 1,
    progressIndex: Int = 2,
    isLastItemCompleted: Boolean = false // 👈 NEW
) {
    val fullStatusList = listOf(
        TimelineData(title = "Arriving", type = StatusType.ARRIVING),
        TimelineData(title = "Reported", type = StatusType.REPORTED),
        TimelineData(title = "Vehicle Verified", type = StatusType.VERIFIED),
        TimelineData(title = "UnLoading", type = StatusType.UNLOADING),
        TimelineData(title = "Vehicle Released", type = StatusType.VEHICLE_RELEASED),
    )

    val (visibleList, currentStep) = when (status) {
        ProgressStatus.PENDING -> {
            val pendingList = fullStatusList.take(2)
            pendingList to pendingIndex
        }

        ProgressStatus.PROGRESS -> {
            val progressList = fullStatusList.take(3)
            progressList to progressIndex
        }

        else -> {
            fullStatusList to 4
        }
    }

    StatusTimelineCard(
        statusList = visibleList,
        isExpandableTimeLine = isExpandableTimeLine,
        currentIndex = currentStep,
        isLastItemCompleted = isLastItemCompleted // 👈 PASS IT
        , iconForStatusType = { type -> iconForStatusType(type as StatusType) }
    )
}


@Composable
fun PreviewTimeline(
    isExpandableTimeLine: MutableState<Boolean>,
    status: ProgressStatus,
    pendingIndex: Int = 1,
    progressIndex: Int = 2,
    approvalIndex: Int = 6,
    isLastItemCompleted: Boolean = false
) {
    val previewStatusList = listOf(
        TimelineData("ARRIVING", "12-AUG-2024", "9:30:02 AM", StatusType.ARRIVING),
        TimelineData(
            "REPORTED",
            date = if (pendingIndex == 1) "12-AUG-2024" else "",
            time = if (pendingIndex == 1) "10:05:02 AM" else "",
            StatusType.REPORTED
        ),
        TimelineData(
            "VEHICLE VERIFIED",
            if (status == ProgressStatus.APPROVAL) "12-AUG-2024" else "",
            if (status == ProgressStatus.APPROVAL) "10:10:02 AM" else "",
            StatusType.VERIFIED
        ),
        TimelineData("FIRST SCANNED", "12-AUG-2024", "10:15:02 AM", StatusType.FIRST_SCANNED),
        TimelineData("LAST SCANNED", "12-AUG-2024", "10:25:02 AM", StatusType.LAST_SCANNED),
        TimelineData("UNLOADING", null, null, StatusType.UNLOADING),
        TimelineData("VEHICLE RELEASE", null, null, StatusType.VEHICLE_RELEASED)
    )

    val (visiblePreviewList, currentStep) = when (status) {
        ProgressStatus.PENDING -> {
            val pendingList = previewStatusList.take(2) // ["Arrival", "Report","Vehicle Verified"]
            pendingList to pendingIndex // index 1 is "Report", which will be shown as the last step
        }

        ProgressStatus.PROGRESS -> {
            val progressList = previewStatusList.take(3) // ["Arrival", "Report"]
            progressList to progressIndex // index 1 is "Report", which will be shown as the last step
        }

        else -> {
            previewStatusList to approvalIndex // full list, and index 4 is the last step
        }
    }

    PreviewTimelineCard(
        visiblePreviewList,
        isExpandableTimeLine,
        currentStep,
        isPendingApproval = if (status == ProgressStatus.APPROVAL) true else false,
        isLastItemCompleted = isLastItemCompleted,
        iconForStatusType = { type -> iconForStatusType(type as StatusType) })

}

fun iconForStatusType(type: StatusType): Int? {
    return when (type) {
        StatusType.ARRIVING -> R.drawable.ic_arriving
        StatusType.REPORTED -> R.drawable.ic_reported
        StatusType.VERIFIED -> R.drawable.ic_vehicle_verified
        StatusType.UNLOADING -> R.drawable.ic_unloading
        StatusType.FIRST_SCANNED -> null
        StatusType.LAST_SCANNED -> null
        StatusType.VEHICLE_RELEASED -> R.drawable.ic_vehicle_release_pending
        StatusType.APPROVAL_PENDING -> null
        StatusType.SCANNING_PENDING -> null
        StatusType.DOCUMENT_NOT_RECEIVED -> TODO()
        StatusType.VERIFICATION_PENDING -> TODO()
        StatusType.DOCUMENT_RECEIVED -> TODO()
    }
}