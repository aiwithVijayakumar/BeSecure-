package com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.editScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.BottomSheetSuccess
import com.tnd_psm.core.ui.component.CommonBottomSheet
import com.tnd_psm.core.ui.component.editTeamDock.DockNTeamChangeCard
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.component.processCardComponent.TypeEdit
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranshipmentUnloadingEditScreen(
    onBackClick: () -> Unit,
    updated: Boolean,
    initialTypeEdit: TypeEdit?, // 👈 new param
    typeEditAction: (TypeEdit) -> Unit,
) {
    var showDockDialogSuccess = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(updated) {
        if (updated) {
            showDockDialogSuccess.value = true
        }
    }
    Box {
        Column {
            AppTopBar(
                title = "Edit Details",
                onBackClick = {
                    onBackClick()
                },
                notificationIconVis = false,
                menuIconVis = false
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = MaterialTheme.dimens.large2,
                        vertical = MaterialTheme.dimens.small3
                    )
            ) {

                DockNTeamChangeCard(
                    title = "Dock Range",
                    rangeText = "1-5",
                    placeholderText = "SELECT DOCK NUMBER",
                    onClick = {
                        typeEditAction(TypeEdit.SELECT_DOCK)
                    },
                    leadingIcon = R.drawable.ic_warehouse,
                    showTrailingInfoIcon = false,
                    dockSelected = if (initialTypeEdit == TypeEdit.SELECT_DOCK) "1" else null
                )
                Spacer(modifier = Modifier.size(MaterialTheme.dimens.small3))
                DockNTeamChangeCard(
                    title = "Team Member",
                    rangeText = "05198",
                    placeholderText = "SELECT TEAM MEMBER",
                    onClick = {
                        typeEditAction(TypeEdit.CHANGE_TEAM_MEMBER)
                    },
                    leadingIcon = R.drawable.ic_team_member,
                    showTrailingInfoIcon = true
                )
            }
        }
        Box(
            modifier = Modifier
                .height(MaterialTheme.dimens.spacingLarge9)
                .fillMaxWidth()
                .align(Alignment.BottomEnd)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = "BACK TO TASK PAGE",
                onBtnSwipe = { },
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.extraLarge1,
                    vertical = MaterialTheme.dimens.spacingSmall3
                ),
                isEnabled = true
            )
        }
        if (showDockDialogSuccess.value) {
            CommonBottomSheet(
                sheetState = sheetState,
                showBottomSheet = showDockDialogSuccess,
                content = {
                    BottomSheetSuccess(
                        message = when (initialTypeEdit) {
                            TypeEdit.ADD_WAY_BILL -> ""
                            TypeEdit.SELECT_DOCK -> "Dock number has been\n updated successfully "
                            TypeEdit.CHANGE_TEAM_MEMBER -> "Reassignment updated successfully!"
                            else -> ""
                        }
                    )
                },
                onDismissed = {
                    showDockDialogSuccess.value = false

                },
            )
        }
    }
}
