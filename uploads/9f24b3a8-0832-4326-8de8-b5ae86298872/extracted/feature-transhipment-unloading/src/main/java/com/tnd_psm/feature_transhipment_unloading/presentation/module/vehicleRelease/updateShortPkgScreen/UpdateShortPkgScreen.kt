package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.updateShortPkgScreen

//import com.tnd_psm.feature_transhipment_unloading.presentation.navigation.TranshipmentUnloadingRoute
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.HeaderSection
import com.tnd_psm.core.ui.component.NoDataFound
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.data.model.vehicleRelease.WayBillModel
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.ScanCardShort
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.VehicleReleaseStatus
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.WaybillCardShort
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen.MenuAction
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen.MenuDropDown
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.withoutStickerScreen.ShowPackagePrintListBottomSheet

@Composable
fun UpdateShortPkgScreen(
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit,
    onMenuActionClick: (MenuAction) -> Unit,
) {
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }
    val scanInput = remember { mutableStateOf("") }
    var isMenuDropDown = remember { mutableStateOf(false) }

    val waybillList = listOf(
        WayBillModel("987654327836", listOf("987654327836001", "987654327836002"), 2),
        WayBillModel("123456", listOf("WB123456001", "WB123456002"), 2),
        WayBillModel("12", listOf("WB123456001", "WB123456002"), 2),
    )

    val matchedIndex = remember(scanInput.value) {
        waybillList.indexOfFirst { it.wbNumber == scanInput.value.trim() }
    }

    val isButtonEnabled = remember(scanInput.value) {
        matchedIndex != -1
    }
    var showPrintPackageList = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        AppTopBar(title = "TI - Vehicle Release - HR03 IA 8373", onBackClick = {
            onBackClick()
        }, onMenuIconClick = {
            isMenuDropDown.value = true
        }, notificationIconVis = false, menuIconVis = true)

        VehicleReleaseStatus(4)
        HeaderSection("Update Short Recon", true)

        ScanCardShort(
            value = scanInput.value,
            onValueChange = { scanInput.value = it },
            modifier = Modifier.padding(horizontal = MaterialTheme.dimens.large2)
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (waybillList.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = MaterialTheme.dimens.large2,
                        end = MaterialTheme.dimens.large2,
                        bottom =
                            MaterialTheme.dimens.spacingLarge10
                    )
                ) {
                    itemsIndexed(waybillList) { index, item ->
                        val isExpanded = expandedStates[index] ?: false
                        val isMatched = index == matchedIndex
                        WaybillCardShort(
                            item = item,
                            isExpanded = isExpanded,
                            isMatched = isMatched,
                            onToggleExpand = {
                                expandedStates[index] = !isExpanded
                            }
                        )
                    }
                }
            } else {
                NoDataFound("No Short Package Found")
            }

            SlideToBookButton(
                btnText = "Slide To Update",
                onBtnSwipe = {
                    onButtonClick()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        horizontal = MaterialTheme.dimens.extraLarge1,
                        vertical = MaterialTheme.dimens.spacingSmall3
                    ),
                isEnabled = isButtonEnabled
            )
        }
    }
    //Menu Dummy
    MenuDropDown(isMenuDropDown) { action ->
        when (action) {
            MenuAction.PrintSticker -> {
                showPrintPackageList.value = true
            }

            else -> {
                onMenuActionClick(action)
            }
        }
    }
    ShowPackagePrintListBottomSheet(showPrintPackageList)
}


