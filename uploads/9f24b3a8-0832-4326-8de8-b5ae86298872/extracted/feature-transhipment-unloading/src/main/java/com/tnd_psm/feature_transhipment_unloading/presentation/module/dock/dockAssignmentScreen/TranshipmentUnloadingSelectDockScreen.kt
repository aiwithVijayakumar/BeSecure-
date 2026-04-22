package com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.dockAssignmentScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.data.model.dock.TeamMemberModel
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.editTeamDock.DockSelectCard
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.ui.theme.textColor
import com.tnd_psm.core.utils.noRippleClickable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranshipmentUnloadingSelectDockScreen(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onButtonClick: () -> Unit
) {

    var selectedId by remember { mutableStateOf<String?>(null) }

    val team = listOf(
        TeamMemberModel("1", "Ashish Dewan", "Not Available", true),
        TeamMemberModel("2", "Sai M", "Available", true),
        TeamMemberModel("3", "Suresh Malik", "Available in 1 hr 30 mins", true),
        TeamMemberModel("4", "Ramu", "Available", false),
        TeamMemberModel("5", "Komal", "Available", false)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Gray)
    ) {
        AppTopBar(
            title = "Select Dock",
            notificationCount = 0,
            onBackClick = { onBackClick() },
            onNotificationClick = { /* Handle notification */ },
            onMenuIconClick = {
                onMenuClick()
            },
            menuIcon = R.drawable.ic_qr,
            menuIconVis = true
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = MaterialTheme.dimens.spacingLarge1)
                .padding(
                    horizontal = MaterialTheme.dimens.large2,
                    vertical = MaterialTheme.dimens.spacingMedium1
                )
        ) {
            Spacer(modifier = Modifier.size(MaterialTheme.dimens.spacingMedium1))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_warehouse),
                    contentDescription = "Warehouse Icon",
                    modifier = Modifier.size(MaterialTheme.dimens.large2),
                    tint = GreenPrimary
                )
                Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))

                Text(
                    text = "Suggested Docks",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                    color = textColor
                )

            }
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium1))

            LazyColumn {
                items(team.size) { indx ->
                    DockSelectCard(
                        dockId = team[indx].id,
                        statusText = team[indx].status,
                        isSelected = team[indx].id == selectedId,
                        modifier = Modifier
                            .noRippleClickable {
                                if (team[indx].status == "Available")
                                    selectedId = team[indx].id
                            }
                    )
                }
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
                btnText = "SLIDE TO CONFIRM",
                onBtnSwipe = {
                    onButtonClick()
                },
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.extraLarge1,
                    vertical = MaterialTheme.dimens.spacingSmall3
                ),
                isEnabled = selectedId != null
            )

        }
    }
}
