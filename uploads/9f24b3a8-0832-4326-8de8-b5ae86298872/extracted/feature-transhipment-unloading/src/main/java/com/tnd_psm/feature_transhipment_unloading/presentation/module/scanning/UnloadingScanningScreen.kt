package com.tnd_psm.feature_transhipment_unloading.presentation.module.scanning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.CommonDropDown
import com.tnd_psm.core.ui.component.CommonScanCard
import com.tnd_psm.core.ui.component.FilterModel
import com.tnd_psm.core.ui.component.HeaderExcess
import com.tnd_psm.core.ui.component.ScanPackageImgNText
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.component.scanning.CardPrintList
import com.tnd_psm.core.ui.component.scanning.PackageDetailHeader
import com.tnd_psm.core.ui.component.scanning.PackagePrintList
import com.tnd_psm.core.ui.component.scanning.PrintDialogScreen
import com.tnd_psm.core.ui.component.scanning.ScanPackageUI
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.data.model.scanning.ScannedPackageModel
import com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning.CounterRow
import com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning.DetailedInfoCardScannedPackage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranshipmentUnloadingScanningScreen(
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit,
    onFilterSelected: (FilterModel) -> Unit,
    onDamageClick: () -> Unit,

    ) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { value ->
            // Block collapse
            value != SheetValue.Hidden
        }
    )
    val showBottomSheet = remember { mutableStateOf(false) }
    val pkgBottomSheet = remember { mutableStateOf(false) }
    val unitBottomSheet = remember { mutableStateOf(false) }
    val printerBottomSheet = remember { mutableStateOf(false) }

    val scannedItems = remember { mutableStateListOf<ScannedPackageModel>() }
    val menuDropDown = remember { mutableStateOf(false) }
    val isPrintSuccess = remember { mutableStateOf(false) }

    val filterOptions = listOf(
        FilterModel(img = R.drawable.ic_update_without_sticker, text = "Update Without Sticker"),
        FilterModel(img = R.drawable.ic_printer, text = "Print Sticker"),
        FilterModel(img = R.drawable.ic_add_dmg_pkg, text = "Add Damage Pkg"),
    )
    val selectedFilter = remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            AppTopBar(title = "TI - Unloading Scanning", onBackClick = {
                onBackClick()
            }, menuIconVis = true, onMenuIconClick = {
                menuDropDown.value = true

            })
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
                                if (selected.text == "Print Sticker") {
                                    printerBottomSheet.value = true
                                } else {
                                    onFilterSelected(selected)
                                }
                            }
                        )
                    }
                }
            }

            // Vehicle Info Card
            PackageDetailHeader(
                vehicleNo = "HR03 IA 8373",
                totalWb = "10",
                totalPkgs = 2000,
                totalWeight = "12"
            )


            Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

            Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.large2)) {
                CommonScanCard(onScanClick = {
                    if (scannedItems.size == 0)
                        scannedItems.add(
                            ScannedPackageModel(
                                wbNo = "9876 5432 6711",
                                pkgNo = " 9876 5432 6721 001",
                                bay = "Bangalore",
                                totalPkgs = "100",
                                scannedPkgs = "5",
                                isWrongBay = false,
                                showExtendedView = true
                            )
                        ) else if (scannedItems.size == 1) {
                        scannedItems.add(
                            ScannedPackageModel(
                                wbNo = "9876 5432 6711",
                                pkgNo = " 9876 5432 6721 001",
                                bay = "Bangalore",
                                totalPkgs = "100",
                                scannedPkgs = "5",
                                isWrongBay = false,
                                isDamageError = true,

                                )
                        )
                    } else if (scannedItems.size == 2) {
                        scannedItems.add(
                            ScannedPackageModel(
                                wbNo = "9876 5432 6711",
                                pkgNo = " 9876 5432 6721 001",
                                bay = "Bangalore",
                                totalPkgs = "100",
                                scannedPkgs = "5",
                                isWrongBay = false,
                                isScanningCompleted = true
                            )
                        )
                    } else {
                        scannedItems.add(
                            ScannedPackageModel(
                                wbNo = "9876 5432 6712",
                                pkgNo = " 9876 5432 6721 011",
                                bay = "Patna",
                                totalPkgs = "100",
                                scannedPkgs = "5",
                                isWrongBay = true
                            )
                        )
                    }
                })

                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

                // Counters Row
                CounterRow(pkgBottomSheet, unitBottomSheet)
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small3))

                // Table Header
                HeaderExcess(
                    start = "WB #",
                    middle = "Pkgs",
                    end = "Bay"
                )
            }


            // Barcode Scan Icon & Label
            if (scannedItems.isEmpty()) {
                ScanPackageImgNText(modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.padding(
                        start = MaterialTheme.dimens.large2,
                        end = MaterialTheme.dimens.large2,
                        top = MaterialTheme.dimens.small3,
                        bottom = MaterialTheme.dimens.spacingLarge10
                    )
                ) {
                    items(scannedItems.size) { item ->
                        DetailedInfoCardScannedPackage(
                            scannedPkgList = scannedItems[item],
                            onQuantityClick = {
                                showBottomSheet.value = true
                            })
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
                .padding(vertical = MaterialTheme.dimens.spacingSmall3)
        ) {
            SlideToBookButton(
                btnText = AppStrings.MARK_AS_COMPLETE,
                isEnabled = scannedItems.isNotEmpty(),
                onBtnSwipe = {
                    onButtonClick()
                },
                modifier = Modifier.padding(horizontal = MaterialTheme.dimens.extraLarge1)
            )
        }
        if (pkgBottomSheet.value) {
            CommonBottomSheet(
                sheetState = sheetState,
                showBottomSheet = pkgBottomSheet,
                content = {

                    PackagePrintList("Pkg/Unit Scanned: ", onPrintClick = {
                        printerBottomSheet.value = true
                    })

                },
                onDismissed = {
                    pkgBottomSheet.value = false
                }, heightFraction = 0.77f
            )

        }

        if (unitBottomSheet.value) {
            CommonBottomSheet(
                sheetState = sheetState,
                showBottomSheet = unitBottomSheet,
                content = {
                    PackagePrintList("Unit Scanned: ", onPrintClick = {
                        printerBottomSheet.value = true
                    })

                },
                onDismissed = {
                    unitBottomSheet.value = false
                }, heightFraction = 0.77f
            )

        }
        if (showBottomSheet.value) {
            CommonBottomSheet(
                sheetState = sheetState,
                showBottomSheet = showBottomSheet,
                content = {
                    Column {
                        ScanPackageUI(onDamageClick = {
                            onDamageClick()
                        })
                        CardPrintList(onPrintClick = {
                            printerBottomSheet.value = true
                        })
                    }

                },
                onDismissed = {
                    showBottomSheet.value = false
                },
                heightFraction = 0.77f
            )
        }
        if (printerBottomSheet.value) {
            CommonBottomSheet(
                sheetState = sheetState,
                showBottomSheet = printerBottomSheet,
                content = {
                    if (isPrintSuccess.value == true) {
                        BottomSheetSuccess("Sticker Sent for Printing", R.drawable.ic_print_success)
                    } else {
                        PrintDialogScreen("987654326711005") {
                            isPrintSuccess.value = true
                        }
                    }
                },
                onDismissed = {
                    printerBottomSheet.value = false
                    isPrintSuccess.value = false
                },
            )
        }
    }
}
