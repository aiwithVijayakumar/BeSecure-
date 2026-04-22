package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.CountCommon
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.White
import com.tnd_psm.core.ui.theme.cardGoldColor
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.data.model.vehicleRelease.WayBillModel


@Composable
fun WaybillCardShort(
    item: WayBillModel,
    isTabShort: Boolean = false,
    isExpanded: Boolean,
    isMatched: Boolean = false,
    onToggleExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(top = MaterialTheme.dimens.spacingMedium1)
            .fillMaxWidth()
            .clickable {
                onToggleExpand()
            }
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall)),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.dimens.small2)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        "WB#",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(item.wbNumber, style = MaterialTheme.typography.labelLarge)

                }
                CountCommon(
                    item.count,
                    isExpanded,
                    isTabShort,
                    bgcolor = if (isMatched) GreenPrimary else cardGoldColor,
                    style = if (isMatched) MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = White
                    ) else MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                )
            }


            if (isExpanded) {
                HorizontalDivider(
                    Modifier.padding(vertical = MaterialTheme.dimens.extraSmall),
                    color = Gray
                )
                Spacer(modifier = Modifier.padding(top = MaterialTheme.dimens.small1))
                Text(
                    "Pkgs/Unit",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                )
                item.pkgList.forEach { pkg ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1)
                    ) {
                        if (!isTabShort) {
                            Image(
                                painterResource(R.drawable.ic_tick),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(
                                    if (isMatched) GreenPrimary else Color.Gray
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.borderThick))
                        Text(pkg, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}


