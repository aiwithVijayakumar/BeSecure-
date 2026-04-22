package com.tnd_psm.feature_transhipment_unloading.presentation.component.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.processCardComponent.EditAction
import com.tnd_psm.core.ui.component.processCardComponent.EditCardComponent
import com.tnd_psm.core.ui.component.processCardComponent.EditItem
import com.tnd_psm.core.ui.component.processCardComponent.ProgressStatus
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings

@Composable
fun EditSection(
    measuredHeight: MutableState<Int>,
    status: ProgressStatus,
    onAction: (EditAction) -> Unit
) {
    // Decide what buttons to show based on status
    val editItems = when (status) {
        ProgressStatus.PENDING -> listOf(
            EditItem(R.drawable.ic_view, AppStrings.VIEW, EditAction.OnViewClick),
            EditItem(R.drawable.ic_edit, AppStrings.EDIT, EditAction.OnEditClick),
            EditItem(R.drawable.ic_start, AppStrings.START, EditAction.OnStartClick)
        )

        ProgressStatus.PROGRESS -> listOf(
            EditItem(R.drawable.ic_start, AppStrings.INITIATE, EditAction.OnInitiateClick)
        )

        ProgressStatus.APPROVAL -> listOf(
            EditItem(R.drawable.ic_view, AppStrings.VIEW, EditAction.OnViewClick),
            EditItem(R.drawable.ic_start, AppStrings.VERIFY, EditAction.OnVerifyClick)

        )

        ProgressStatus.DOCUMENT_VERIFICATION_PENDING -> TODO()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(with(LocalDensity.current) {
                if (measuredHeight.value > 0) measuredHeight.value.toDp() else MaterialTheme.dimens.spacingExtraLarge3
            })
            .background(GreenPrimary),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        editItems.forEachIndexed { index, item ->
            EditCardComponent(image = item.iconRes, content = item.label) {
                onAction(item.action)
            }

            if (index != editItems.lastIndex) {
                Box(
                    modifier = Modifier
                        .height(MaterialTheme.dimens.spacingLarge1)
                        .width(MaterialTheme.dimens.borderThin)
                        .background(Color.White)
                )
            }
        }
    }
}