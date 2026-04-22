package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.SavedStateHandle
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.CommonDropDown
import com.tnd_psm.core.ui.component.FilterModel
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.component.scanning.PackageDetailHeader
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.SelectableViewUpLoad
import com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease.UploadPackage
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.withoutStickerScreen.ShowPackagePrintListBottomSheet


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UpLoadDamageExcessPhoto(
    savedStateHandle: SavedStateHandle,
    onBackClick: () -> Unit,
    onMenuActionClick: (MenuAction) -> Unit,
    onTakePhotoClick: (type: String, index: Int) -> Unit,
    onSlideToUpdate: () -> Unit,
) {
    val damageExpanded = remember { mutableStateOf(false) }
    val excessExpanded = remember { mutableStateOf(false) }
    val isMenuDropDown = remember { mutableStateOf(false) }
    val showPrintPackageList = remember { mutableStateOf(false) }


    val damagePackages = remember {
        mutableStateListOf(
            UploadPackage("1233444", "Bengaluru"),
            UploadPackage("1233445", "Chennai"),
            UploadPackage("1233446", "Mumbai")
        )
    }

    val excessPackages = remember {
        mutableStateListOf(
            UploadPackage("987654", "Delhi"),
            UploadPackage("654321", "Hyderabad"),
            UploadPackage("111222", "Kolkata")
        )
    }

    LaunchedEffect(savedStateHandle) {
        snapshotFlow { savedStateHandle.get<Int>("selectedImage") }
            .collect { image ->
                if (image != null) {
                    val type = savedStateHandle.get<String>("uploadType")
                    val index = savedStateHandle.get<Int>("packageIndex")
                    if (type != null && index != null) {
                        when (type) {
                            "damage" -> updateImageAtIndex(damagePackages, index, image)
                            "excess" -> updateImageAtIndex(excessPackages, index, image)
                        }
                    }
                    savedStateHandle.remove<Int>("selectedImage")
                    savedStateHandle.remove<String>("uploadType")
                    savedStateHandle.remove<Int>("packageIndex")
                }
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = MaterialTheme.dimens.small3)
        ) {
            stickyHeader {
                AppTopBar(
                    title = "Upload Damage/Excess Photo",
                    onBackClick = { onBackClick() },
                    onMenuIconClick = { isMenuDropDown.value = true },
                    notificationIconVis = false,
                    menuIconVis = true,
                    isNotificationCountVis = false
                )
            }

            item {
                PackageDetailHeader(
                    vehicleNo = "HR03 IA 8373",
                    totalWb = "10",
                    totalPkgs = 2000,
                    totalWeight = "12"
                )

                SelectableViewUpLoad(
                    title = "Damage Package",
                    expanded = damageExpanded.value,
                    packages = damagePackages,
                    onExpandToggle = { damageExpanded.value = !damageExpanded.value },
                    onItemClick = { index ->
                        savedStateHandle["uploadType"] = "damage"
                        savedStateHandle["packageIndex"] = index
                        onTakePhotoClick("damage", index)
                    }
                )

                SelectableViewUpLoad(
                    title = "Excess Package",
                    expanded = excessExpanded.value,
                    packages = excessPackages,
                    onExpandToggle = { excessExpanded.value = !excessExpanded.value },
                    onItemClick = { index ->
                        savedStateHandle["uploadType"] = "excess"
                        savedStateHandle["packageIndex"] = index
                        onTakePhotoClick("excess", index)
                    }
                )
            }
        }

        Box(
            modifier = Modifier
                .height(MaterialTheme.dimens.spacingLarge9)
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = "Slide To Update",
                onBtnSwipe = {
                    onSlideToUpdate()
                },
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.extraLarge1,
                    vertical = MaterialTheme.dimens.spacingSmall3
                ),
                isEnabled = true
            )
        }
    }

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

fun updateImageAtIndex(
    packages: SnapshotStateList<UploadPackage>,
    index: Int,
    image: Int?
) {
    val old = packages[index]
    packages[index] = old.copy(uploadedImage = image)
}


@Composable
fun MenuDropDown(
    isMenuDropDown: MutableState<Boolean>,
    onMenuActionClick: (MenuAction) -> Unit,
) {
    val filterOptions = listOf(
        FilterModel(img = R.drawable.ic_update_without_sticker, text = "Update Without Sticker"),
        FilterModel(img = R.drawable.ic_printer, text = "Print Sticker"),
        FilterModel(img = R.drawable.ic_remove_excess_pkg, text = "Remove Excess Pkgs"),
        FilterModel(img = R.drawable.ic_update_short_pkg, text = "Update Short Pkgs"),
        FilterModel(img = R.drawable.ic_add_dmg_pkg, text = "Add Damage Pkg"),
    )
    val selectedFilter = remember { mutableStateOf<String?>(null) }

    if (isMenuDropDown.value) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    end = MaterialTheme.dimens.medium2,
                    top = MaterialTheme.dimens.spacingLarge1
                ),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CommonDropDown(
                    filterOptions = filterOptions,
                    expanded = isMenuDropDown,
                    selectedItem = selectedFilter.value,
                    onItemSelected = { selected ->
                        selectedFilter.value = selected.text
                    }
                )
            }
        }
    }
    selectedFilter.value?.let { selected ->
        when (selected) {
            "Update Without Sticker" -> onMenuActionClick(MenuAction.UpdateWithoutSticker)
            "Remove Excess Pkgs" -> onMenuActionClick(MenuAction.RemoveExcessPkgs)
            "Update Short Pkgs" -> onMenuActionClick(MenuAction.UpdateShortPkgs)
            "Add Damage Pkg" -> onMenuActionClick(MenuAction.AddDamagePkg)
            "Print Sticker" -> onMenuActionClick(MenuAction.PrintSticker)
        }
        selectedFilter.value = null
    }

}

sealed class MenuAction {
    data object UpdateWithoutSticker : MenuAction()
    data object RemoveExcessPkgs : MenuAction()
    data object UpdateShortPkgs : MenuAction()
    data object AddDamagePkg : MenuAction()
    data object PrintSticker : MenuAction()
}
