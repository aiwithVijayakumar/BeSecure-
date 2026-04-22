package com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.teamMemberScreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tnd_psm.core.R
import com.tnd_psm.core.data.model.dock.TeamMemberModel
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.EmployeeIdTextField
import com.tnd_psm.core.ui.component.editTeamDock.NoRoasterAvailableMsg
import com.tnd_psm.core.ui.component.editTeamDock.OtherRostersDropdown
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.component.editTeamDock.TeamMemberRoasterTab
import com.tnd_psm.core.ui.component.editTeamDock.TeamMemberSelectCard
import com.tnd_psm.core.ui.component.editTeamDock.TrianglePointUp
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable

@Composable
fun TeamMemberAssignmentScreen(
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    val team = listOf(
        TeamMemberModel("10892", "Ashish Dewan", "Available in 30 mins", true),
        TeamMemberModel("10768", "Sai M", "Available in 30 mins", true),
        TeamMemberModel("10798", "Suresh Malik", "Available in 1 hr 30 mins", true),
        TeamMemberModel("10098", "Ramu", "Available", false),
        TeamMemberModel("10029", "Komal", "Not Checked-In", false),
        TeamMemberModel("1002G9", "Komal", "Not Checked-In", false),
        TeamMemberModel("1002G9", "Komal", "Not Checked-In", false),
        TeamMemberModel("10324029", "Komal", "Not Checked-In", false),
        TeamMemberModel("21", "Komal", "Not Checked-In", false),
        TeamMemberModel("12", "Komal", "Not Checked-In", false),
        TeamMemberModel("10021G29", "Komal", "Not Checked-In", false),
        TeamMemberModel("100121S29", "Komal", "Not Checked-In", false),
        TeamMemberModel("10G121029", "Komal", "Not Checked-In", false),
        TeamMemberModel("10W0121Q29", "Komal", "Not Checked-In", false),
        TeamMemberModel("10G0229", "Komal", "Not Checked-In", false)
    )
    RosterScreen(team, {}, onBackClick, onButtonClick)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RosterScreen(
    team: List<TeamMemberModel>,
    onTabSelected: (Int) -> Unit,
    onBackClick: () -> Unit,
    onButtonClick: () -> Unit
) {
    var showDropdown by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }
    var selectedId by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val isStockTakeRosterSelected = remember { mutableStateOf(true) }
    val isOtherRosterSelected = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
            ) {
                AppTopBar(
                    title = "Edit Details",
                    notificationCount = 0,
                    onBackClick = { onBackClick() },
                    onNotificationClick = { /* Handle notification */ },
                    onMenuIconClick = {},
                )


                TeamMemberRoasterTab(
                    isFirstOptionSelected = isStockTakeRosterSelected.value,
                    onFirstItemSelected = {
                        isStockTakeRosterSelected.value = true
                        isOtherRosterSelected.value = false
                        selectedTab = 0
                    },
                    onSecondItemSelected = {
                        isStockTakeRosterSelected.value = false
                        isOtherRosterSelected.value = true
                        selectedTab = 1
                    },
                    onDropDownClick = {
                        if (isOtherRosterSelected.value) {
                            showDropdown = true
                            selectedTab = 1
                            Log.d(
                                "ChangeTeamMemberScreen",
                                "ChangeTeamMemberScreen: Clicked On dropdown"
                            )
                        }
                    }
                )
            }
        },
        containerColor = Gray,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (showDropdown && selectedTab == 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.dimens.medium2),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TrianglePointUp(
                                    GreenPrimary,
                                    modifier = Modifier.offset(x = (-45).dp)
                                )
                                OtherRostersDropdown(
                                    expanded = showDropdown,
                                    selectedOption = selectedFilter,
                                    onOptionSelected = {
                                        selectedFilter = it
                                        showDropdown = false
                                    },
                                    onClear = {
                                        selectedFilter = "All"
                                        showDropdown = false
                                    },
                                    onDismiss = { showDropdown = false }
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = MaterialTheme.dimens.spacingExtraLarge1 // reserve space so content doesn't go behind button
                        )
                    ) {
                        if (selectedTab == 0 && team.none { it.status == "Assigned" }) {
                            item { NoRoasterAvailableMsg() }
                        }

                        item { EmployeeIdTextField() }

                        item {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        start = MaterialTheme.dimens.medium2,
                                        bottom = MaterialTheme.dimens.small1
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_team_member),
                                    contentDescription = "Team Member",
                                    tint = GreenPrimary
                                )
                                Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))
                                Text(
                                    text = "Team Member",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                        items(team.size) { index ->
                            TeamMemberSelectCard(
                                memberId = team[index].id,
                                memberName = team[index].name,
                                statusText = team[index].status,
                                isSelected = team[index].id == selectedId,
                                isDay = team[index].isDay,
                                modifier = Modifier
                                    .noRippleClickable() {
                                        if (team[index].status == "Available") {
                                            selectedId = team[index].id
                                        }
                                    }
                                    .padding(
                                        horizontal = MaterialTheme.dimens.medium2,
                                        vertical = MaterialTheme.dimens.extraSmall
                                    )
                            )
                        }
                    }
                }

                // ✅ Overlay the fixed bottom button on top of scroll
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(MaterialTheme.dimens.spacingLarge9)
                        .background(buttonBackground)
                ) {
                    SlideToBookButton(
                        btnText = "SLIDE TO ASSIGN",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = MaterialTheme.dimens.extraLarge1,
                                vertical = MaterialTheme.dimens.spacingSmall3
                            ),
                        isEnabled = selectedId != null,
                        onBtnSwipe = {
                            onButtonClick()
                        },
                    )
                }
            }
        }
    )
}