package com.tnd_psm.feature_transhipment_unloading.presentation.module.notification.notificationDetailScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.GroupStatus
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.NotificationDetailModel
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.VehicleInfo
import com.tnd_psm.feature_transhipment_unloading.presentation.component.notification.ExpandableGroup

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NotificationDetailScreen(onBackClick: () -> Unit) {
    val groups = listOf(
        NotificationDetailModel(
            "2 Vehicles Arriving Early",
            listOf(
                VehicleInfo("KA 89JX 7468", "6:20 PM"),
                VehicleInfo("HR 28SD 8483", "6:25 PM")
            ),
            GroupStatus.EARLY
        ),
        NotificationDetailModel(
            "2 Vehicles On Time", listOf(
                VehicleInfo("KA 89JX 7468", "6:20 PM"),
                VehicleInfo("HR 28SD 8483", "6:25 PM")
            ), GroupStatus.ON_TIME
        ),
        NotificationDetailModel(
            "1 Vehicles Delayed", listOf(
                VehicleInfo("KA 89JX 7468", "6:20 PM"),
                VehicleInfo("HR 28SD 8483", "6:25 PM")
            ), GroupStatus.DELAYED
        )
    )

    var expandedIndex by remember { mutableIntStateOf(0) }
    LazyColumn {
        stickyHeader {
            AppTopBar(
                title = "Notifications", onBackClick = {
                    onBackClick()
                }, onMenuIconClick = {}, notificationIconVis = false, menuIconVis = false,
                isNotificationCountVis = true
            )
        }
        item {
            Column(modifier = Modifier.padding(MaterialTheme.dimens.medium2)) {
                groups.forEachIndexed { index, group ->
                    ExpandableGroup(
                        group = group,
                        expanded = expandedIndex == index,
                        onExpandToggle = {
                            expandedIndex = if (expandedIndex == index) -1 else index
                        }
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))
                }
            }
        }
    }
}

