package com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens

@Composable
fun RemoveIconText(
    onRemoveClick: () -> Unit,
    iconTint: Color = GreenPrimary,
    textColor: Color = GreenPrimary
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onRemoveClick()
            }
            .padding(vertical = MaterialTheme.dimens.spacingMedium1)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = null,
            colorFilter = ColorFilter.tint(
                iconTint
            )
        )
        Spacer(modifier = Modifier.width(MaterialTheme.dimens.extraSmall))
        Text(
            "REMOVE PHOTO",
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.Medium
            )
        )
    }
}