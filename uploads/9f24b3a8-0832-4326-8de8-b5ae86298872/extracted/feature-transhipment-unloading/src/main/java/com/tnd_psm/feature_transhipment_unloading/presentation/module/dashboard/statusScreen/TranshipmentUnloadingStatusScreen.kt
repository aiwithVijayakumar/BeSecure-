package com.tnd_psm.feature_transhipment_unloading.presentation.module.dashboard.statusScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.CommonNoDataFound
import com.tnd_psm.core.ui.component.FilterModel
import com.tnd_psm.core.ui.component.HeaderFilterBar
import com.tnd_psm.core.ui.component.SearchBarWithDropdown
import com.tnd_psm.core.ui.component.processCardComponent.EditAction
import com.tnd_psm.core.ui.component.processCardComponent.InfoStats
import com.tnd_psm.core.ui.component.processCardComponent.ProgressStatus
import com.tnd_psm.core.ui.component.processCardComponent.ScanningCardData
import com.tnd_psm.core.ui.component.processCardComponent.ToBeScanned
import com.tnd_psm.core.ui.component.processCardComponent.TypeEdit
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.presentation.component.TranshipmentUnloadingCard


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TranshipmentUnloadingStatusScreen(
    typeEdit: String,
    status: ProgressStatus,
    onBackClick: () -> Unit,
    onAction: (EditAction) -> Unit,
    isAboveOrBelow: Boolean,
) {
    val dummyCardList = remember { generateDummyScanCards(status) }
    val showBottomSheet = remember { mutableStateOf(false) }
    val titleText = when (status) {
        ProgressStatus.PENDING -> "TI-Pending"
        ProgressStatus.PROGRESS -> "TI - In Progress"
        ProgressStatus.APPROVAL -> "TI-Approval"
        ProgressStatus.DOCUMENT_VERIFICATION_PENDING -> TODO()
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            // Block collapse
            value != SheetValue.Hidden
        }
    )
    var searchText by remember { mutableStateOf("") }
    // Filter list by waybill
    val filteredList = remember(searchText, dummyCardList) {
        if (searchText.isBlank()) dummyCardList
        else dummyCardList.filter {
            it.waybill.contains(searchText, ignoreCase = true)
        }
    }
    val selectedFilter = remember {
        mutableStateOf(
            if (isAboveOrBelow) FilterModel(text = "ABOVE 1HR")
            else FilterModel(text = "BELOW 1HR")
        )
    }

    val editableCardId = remember { mutableStateOf<String?>(null) }
    // ✅ Launch bottom sheet trigger outside LazyColumn
    LaunchedEffect(typeEdit) {
        if (typeEdit == TypeEdit.VEHICLE_CHECK_SUCCESS.name
            || typeEdit == TypeEdit.VEHICLE_SCAN_SUCCESS.name
        ) {
            showBottomSheet.value = true
        }
    }

    if (showBottomSheet.value) {
        CommonBottomSheet(
            sheetState = sheetState,
            showBottomSheet = showBottomSheet,
            content = {
                if (typeEdit == TypeEdit.VEHICLE_CHECK_SUCCESS.name)
                    BottomSheetSuccess(
                        message = AppStrings.SUCCESS_VEHICLE_CHECK_MESSAGE,
                        appreciation = AppStrings.WELL_DONE
                    ) else if (typeEdit == TypeEdit.VEHICLE_SCAN_SUCCESS.name)
                    BottomSheetSuccess("Unloading Complete!\n Waiting for Team Leaders' approval")

            },
            onDismissed = {
                showBottomSheet.value = false
            },
            heightFraction = 0.5f
        )
    }

    LazyColumn {
        stickyHeader {
            AppTopBar(
                title = titleText,
                onBackClick = { onBackClick() },
                onMenuIconClick = {},
                notificationIconVis = false,
                menuIconVis = false
            )
            HeaderFilterBar(
                selectedFilter = selectedFilter.value,
                onFilterSelected = { selectedFilter.value = it }
            )
        }

        item {
            Box(
                modifier = Modifier.padding(
                    start = MaterialTheme.dimens.large2,
                    end = MaterialTheme.dimens.large2,
                    top = MaterialTheme.dimens.small3
                )
            ) {
                SearchBarWithDropdown(
                    searchText = searchText,
                    onSearchTextChange = { searchText = it })
            }

            Spacer(Modifier.height(MaterialTheme.dimens.small2))
        }

        items(filteredList) { cardData ->
            TranshipmentUnloadingCard(
                status = status,
                cardData = cardData,
                editableCardId = editableCardId,
                onAction = { action ->
                    onAction(action)
                }
            )
        }
    }
    if (filteredList.isEmpty()) {
        CommonNoDataFound()
    }
}


fun generateDummyScanCards(status: ProgressStatus): List<ScanningCardData> {
    return List(10) { index ->
        ScanningCardData(
            waybill = "#HR ${"03IA 807" + index}",
            duration = "${index + 1}hr ${index * 5}m",
            route = "DEL${index} - AGR${index} - LCK${index} - HYD${index}",
            currentStepIndexStatusTimeLine = if (index % 2 == 0) 0 else 1,
            infoStats = InfoStats(
                pkgCount = "${1000 + index * 10}",
                weight = "${10.0 + index}",
                assignedTo = "05${100 + index}",
                dock = "${(index % 4) + 1}"
            ),
            toBeScanned = ToBeScanned(
                pkg = "${500 + index * 20}",
                unit = "${5 + index}"
            ),
            isTeamLeaderActivityPending = if (status == ProgressStatus.PROGRESS) {
                (index + 1) % 3 == 0
            } else {
                false
            }, isApprovalPending = if (status == ProgressStatus.APPROVAL) {
                index == 2
            } else {
                false
            }

        )
    }
}


