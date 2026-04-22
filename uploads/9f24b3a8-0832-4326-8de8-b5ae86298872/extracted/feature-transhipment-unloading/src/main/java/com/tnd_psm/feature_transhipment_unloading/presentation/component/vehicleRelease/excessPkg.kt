package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.HeaderExcess
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.dimens
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcessPackage(
    isTabExcess: Boolean = false,
    onSubmitClick: () -> Unit = {}, onTabExcessClick: () -> Unit = {}
) {
    val selectedIndex = remember { mutableIntStateOf(-1) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showBottomSheet = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column {
        HorizontalDivider(
            Modifier.padding(vertical = MaterialTheme.dimens.extraSmall),
            color = Gray
        )

        // Header row
        Box(Modifier.padding(horizontal = MaterialTheme.dimens.small)) {
            HeaderExcess()
        }


        repeat(5) { index ->
            Column(
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimens.small,
                        vertical = MaterialTheme.dimens.borderThin.plus(MaterialTheme.dimens.dividerThin)
                    )
            ) {
                if (isTabExcess) {
                    // In Tab Mode: just trigger navigation
                    ContentExcess {
                        onTabExcessClick()
                    }
                } else {
                    // Normal behavior: toggle selection
                    if (selectedIndex.value == index) {
                        RemoveButton {
                            coroutineScope.launch {
                                showBottomSheet.value = true
                                sheetState.show()
                            }
                        }
                    } else {
                        ContentExcess {
                            selectedIndex.value = index
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet.value) {
        CommonBottomSheet(showBottomSheet, sheetState, content = {
            BottomSheetExcess(onSubmitClick = {
                coroutineScope.launch {
                    sheetState.hide()
                    showBottomSheet.value = false
                    onSubmitClick()
                }
            })
        }, onDismissed = {

        }, heightFraction = 0.7f)
    }
}
