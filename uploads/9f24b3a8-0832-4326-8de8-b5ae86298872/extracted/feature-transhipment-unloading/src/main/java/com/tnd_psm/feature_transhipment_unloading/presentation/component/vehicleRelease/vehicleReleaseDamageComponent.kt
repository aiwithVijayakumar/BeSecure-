package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.ui.component.CommonBorderImage
import com.tnd_psm.core.ui.component.CountCommon
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.Red
import com.tnd_psm.core.ui.theme.White
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.feature_transhipment_unloading.data.model.vehicleRelease.WayBillDamageModel

@Composable
fun WaybillCardDamage(
    item: WayBillDamageModel,
    isExpanded: Boolean,
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
                Column() {
                    Text(
                        "WB#",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        item.waybillNo,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                    )
                }
                Column {
                    Text(
                        "Bay",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        item.location,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                    )
                }
                CountCommon(
                    2,
                    isExpanded,
                    bgcolor = Red,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = White
                    ),
                )
            }


            if (isExpanded) {
                VerticalDivider(
                    Modifier.padding(vertical = MaterialTheme.dimens.extraSmall),
                    color = Gray
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            MaterialTheme.dimens.borderThin,
                            color = Color(0XFFE5E5E5),
                            RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                        )
                        .padding(
                            vertical = MaterialTheme.dimens.extraSmall,
                            horizontal = MaterialTheme.dimens.small
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "9876 54326721006",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                    )
                    CommonBorderImage()
                }

            }
        }
    }
}
