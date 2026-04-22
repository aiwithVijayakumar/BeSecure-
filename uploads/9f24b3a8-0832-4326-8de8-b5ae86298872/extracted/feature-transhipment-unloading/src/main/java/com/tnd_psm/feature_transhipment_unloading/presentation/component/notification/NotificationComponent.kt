package com.tnd_psm.feature_transhipment_unloading.presentation.component.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.theme.DividerColor
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.NotificationDetailModel
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.NotificationModel
import com.tnd_psm.feature_transhipment_unloading.data.model.notification.VehicleInfo


@Composable
fun NotificationCard(item: NotificationModel, onNotificationItemClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(MaterialTheme.dimens.spacingMicro)
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .background(Color.White)
            .fillMaxWidth()
            .noRippleClickable {
                onNotificationItemClick()
            }

    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .padding(MaterialTheme.dimens.small1),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.dimens.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    modifier = Modifier
                        .width(MaterialTheme.dimens.spacingMedium14)
                        .height(MaterialTheme.dimens.spacingMedium9),
                    contentScale = ContentScale.FillBounds
                )
                Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))
                Text(
                    text = item.title,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Notification Badge
            if (item.count > 0) {
                Box(
                    modifier = Modifier
                        .offset(
                            x = MaterialTheme.dimens.extraSmall,
                            y = (-MaterialTheme.dimens.extraSmall)
                        )
                        .background(Color(0xFFD50000), CircleShape)
                        .padding(
                            horizontal = MaterialTheme.dimens.small,
                            vertical = MaterialTheme.dimens.borderThick
                        )
                ) {
                    Text(
                        text = item.count.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}


@Composable
fun ExpandableGroup(
    group: NotificationDetailModel,
    expanded: Boolean,
    onExpandToggle: () -> Unit
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandToggle() }
                .clip(
                    RoundedCornerShape(
                        topStart = MaterialTheme.dimens.extraSmall,
                        topEnd = MaterialTheme.dimens.extraSmall
                    )
                )
                .background(if (expanded) GreenPrimary else Color.White)

        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.dimens.spacingMedium1,
                        vertical = MaterialTheme.dimens.spacingMedium3
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (expanded) Color.White else Color.Black
                    )
                )
                Image(
                    painter = painterResource(R.drawable.ic_status_up),
                    contentDescription = null,
                    Modifier.rotate(if (expanded) 180f else 0f),
                    colorFilter = ColorFilter.tint(if (expanded) Color.White else GreenPrimary)
                )
            }
        }

        if (expanded && group.vehicles.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(
                        RoundedCornerShape(
                            bottomStart = MaterialTheme.dimens.extraSmall,
                            bottomEnd = MaterialTheme.dimens.extraSmall
                        )
                    )
                    .background(Color.White)

            ) {
                Column(modifier = Modifier.padding(vertical = MaterialTheme.dimens.spacingMedium1)) {
                    group.vehicles.forEachIndexed { index, vehicle ->
                        VehicleItem(vehicle)
                        if (index < group.vehicles.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = MaterialTheme.dimens.small1),
                                color = DividerColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleItem(vehicle: VehicleInfo) {
    Column(modifier = Modifier.padding(horizontal = MaterialTheme.dimens.spacingMedium4)) {
        Row {
            Box(
                modifier = Modifier
                    .size(MaterialTheme.dimens.large2)
                    .clip(CircleShape)
                    .background(GreenPrimary)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_vehicle_release),
                    contentDescription = null,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.medium2)
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))
            Column {
                Text(
                    buildAnnotatedString {
                        append("Vehicle entered geo-fenced area & is ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Arriving Early.")
                        }
                    },
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = vehicle.number,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.ic_time),
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.spacingMedium8)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))
                        Text(
                            buildAnnotatedString {
                                append("ETA : ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(vehicle.eta)
                                }
                            },
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                        )
                    }
                }
            }
        }
    }
}
