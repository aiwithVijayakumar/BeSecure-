package com.tnd_psm.feature_transhipment_unloading.presentation.module.notification.notificationScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.NotificationModel
import com.tnd_psm.feature_transhipment_unloading.presentation.component.notification.NotificationCard

@Composable
fun NotificationsScreen(onBackClick: () -> Unit, onItemClick: () -> Unit) {
    val notifications = listOf(
        NotificationModel("GEO FENCE", R.drawable.ic_geo_fence_notification, 6),
        NotificationModel("ARRIVED", R.drawable.ic_arrived_notification, 3),
        NotificationModel("UNLOADING COMPLETED", R.drawable.ic_unloading_completed_notification, 2),
        NotificationModel("LOADING COMPLETED", R.drawable.ic_loading_completed_notification, 5),
        NotificationModel("VEHICLE DOCKED", R.drawable.ic_vehicle_dock_notification, 3),
        NotificationModel("TASKS", R.drawable.ic_task_notification, 8)
    )

    Column {
        AppTopBar(
            title = "Notifications",
            onBackClick = {
                onBackClick()
            },
            onMenuIconClick = {},
            notificationIconVis = false,
            menuIconVis = false,
            isNotificationCountVis = true
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding()
                .padding(
                    horizontal = MaterialTheme.dimens.large2,
                    vertical = MaterialTheme.dimens.small3
                )
        ) {
            items(notifications) { item ->
                NotificationCard(item, onNotificationItemClick = {
                    onItemClick()
                })
            }
        }
    }
}


