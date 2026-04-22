package com.tnd_psm.feature_transhipment_unloading.presentation.module.dashboard.dashboardScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tnd_psm.core.R
import com.tnd_psm.core.navigation.getFilterOptions
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.CommonDropDown
import com.tnd_psm.core.ui.component.FilterModel
import com.tnd_psm.core.ui.component.SearchBarWithDropdown
import com.tnd_psm.core.ui.component.dashboardComponent.ActivityItem
import com.tnd_psm.core.ui.component.dashboardComponent.ActivityPager
import com.tnd_psm.core.ui.component.processCardComponent.ProgressStatus
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.presentation.component.dashboard.TabScreenUnLoading

@Composable
fun TranshipmentUnloadingDashboardScreen(
    onStatusClick: (ProgressStatus, isAboveOrBelow: Boolean) -> Unit,
    onBackClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onActivityCardClick: (String) -> Unit,
    onFilterSelected: (FilterModel) -> Unit
) {
    val menuDropDown = remember { mutableStateOf(false) }
    val activityItems = listOf(
        ActivityItem(AppStrings.VEHICLE_CHECK, R.drawable.ic_vehicle_check),
        ActivityItem(AppStrings.HUB_ON_GO, R.drawable.ic_hub_on_go),
        ActivityItem(AppStrings.CROSS_DOCKING, R.drawable.ic_cross_docking),
        ActivityItem(AppStrings.STOCK_TAKE, R.drawable.ic_stock_take_transfer),
        ActivityItem(AppStrings.STOCK_TRANSFER, R.drawable.ic_stock_take_transfer),
    )

    val selectedFilter = remember { mutableStateOf<String?>(null) }
    var searchText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppTopBar(
            title = AppStrings.TRANSSHIPMENT_INBOUND,
            onBackClick = {
                onBackClick()
            },
            onMenuIconClick = {
                menuDropDown.value = true
            },
            notificationIconVis = true,
            menuIconVis = true,
            notificationCount = 2,
            onNotificationClick = {
                onNotificationClick()
            })

        LazyColumn(
            modifier = Modifier.padding(
                top = MaterialTheme.dimens.spacingMedium1,
                start = MaterialTheme.dimens.large2,
                end = MaterialTheme.dimens.large2
            )
        ) {
            item {
                SearchBarWithDropdown(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it })
                TabScreenUnLoading() { status, aboveBelow ->
                    onStatusClick(status, aboveBelow)
                }
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))
                ActivityPager(items = activityItems) {
                    onActivityCardClick(it)
                }
            }
        }
    }
    if (menuDropDown.value) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CommonDropDown(
                    filterOptions = getFilterOptions(),
                    expanded = menuDropDown,
                    selectedItem = selectedFilter.value,
                    onItemSelected = { selected ->
                        selectedFilter.value = selected.text
                        onFilterSelected(selected)
                    })
            }
        }
    }
}
