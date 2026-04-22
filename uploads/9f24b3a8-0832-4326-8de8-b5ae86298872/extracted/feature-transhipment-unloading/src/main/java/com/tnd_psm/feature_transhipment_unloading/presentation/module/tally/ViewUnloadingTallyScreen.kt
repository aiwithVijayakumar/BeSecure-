package com.tnd_psm.feature_transhipment_unloading.presentation.module.tally

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.tnd_psm.core.R
import com.tnd_psm.core.data.model.viewtally.TallyItemModel
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.CommonDropDown
import com.tnd_psm.core.ui.component.FilterModel
import com.tnd_psm.core.ui.component.scanning.PrintDialogScreen
import com.tnd_psm.core.ui.component.tally.CommonTallyCard
import com.tnd_psm.core.ui.component.tally.UnLoadingTallyTopView
import com.tnd_psm.core.ui.component.tally.ViewSummerySection
import com.tnd_psm.core.ui.theme.dimens


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewUnloadingTallyScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val menuDropDown = remember { mutableStateOf(false) }

    val filterOptions = listOf(
        FilterModel(img = R.drawable.ic_printer, text = "Print Tally")
    )
    val selectedFilter = remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            // Block collapse
            value != SheetValue.Hidden
        }
    )
    val printerBottomSheet = remember { mutableStateOf(false) }

    val isPrintSuccess = remember { mutableStateOf(false) }

    //
    Column(
        modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(Color(0XFFF1F1F1))
    ) {
        AppTopBar(
            title = "View Unloading Tally",
            onBackClick = {
                navController.navigateUp()
            },
            onMenuIconClick = { menuDropDown.value = true },
            notificationIconVis = false,
            menuIconVis = true
        )

        if (menuDropDown.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CommonDropDown(
                        filterOptions = filterOptions,
                        expanded = menuDropDown,
                        selectedItem = selectedFilter.value,
                        onItemSelected = { selected ->
                            selectedFilter.value = selected.text
                        }
                    )
                }
            }
        }
        when (selectedFilter.value) {
            "Print Tally" -> {
                selectedFilter.value = null
                printerBottomSheet.value = true
            }
        }
        // Summary Section
        ViewSummerySection(docNo = "Doc No: 8769 7867 3540", vehicleNo = "8373", vehNoVis = true)

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))
        // Table Header
        UnLoadingTallyTopView()
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small2))
        // Table Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.dimens.large2)
        ) {
            items(unloadingList) { item ->
                CommonTallyCard(item)
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small))
            }
        }
    }
    if (printerBottomSheet.value) {
        CommonBottomSheet(
            sheetState = sheetState,
            showBottomSheet = printerBottomSheet,
            content = {
                if (isPrintSuccess.value == true) {
                    BottomSheetSuccess("Document Sent for Printing", R.drawable.ic_print_success)
                } else {
                    PrintDialogScreen("", "TALLY") {
                        isPrintSuccess.value = true
                    }
                }
            },
            onDismissed = {
                printerBottomSheet.value = false
                isPrintSuccess.value = false
            },
            headline = true
        )
    }
}


// Sample Data
val unloadingList = listOf(
    TallyItemModel("9876 5432 6721", 20, 200, "Hyderabad"),
    TallyItemModel("9876 5432 6720", 10, 150, "Hyderabad"),
    TallyItemModel("9876 5432 6719", 20, 200, "Hyderabad"),
    TallyItemModel("9876 5432 6718", 10, 100, "Hyderabad"),
    TallyItemModel("9876 5432 6717", 25, 300, "Hyderabad"),
    TallyItemModel("9876 5432 6716", 25, 450, "Hyderabad"),
    TallyItemModel("9876 5432 6715", 20, 200, "Hyderabad"),
    TallyItemModel("9876 5432 6714", 25, 200, "Hyderabad"),
    TallyItemModel("9876 5432 6713", 25, 200, "Hyderabad"),
    TallyItemModel("9876 5432 6712", 25, 200, "Hyderabad")
)