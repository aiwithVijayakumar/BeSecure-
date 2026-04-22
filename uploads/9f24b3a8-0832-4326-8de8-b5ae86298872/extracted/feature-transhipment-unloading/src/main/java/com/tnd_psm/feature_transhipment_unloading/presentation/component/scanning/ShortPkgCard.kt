package com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.tnd_psm.core.ui.theme.Black
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.cardGoldColor
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings

@Composable
fun ShortPkgCard(
    wayBillId: String,
    modifier: Modifier = Modifier,
    onQuantityClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
//            .noRippleClickable {
//                onQuantityClick()
//            }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(MaterialTheme.dimens.borderThick)
            )
            .padding(
                horizontal = MaterialTheme.dimens.small3,
                vertical = MaterialTheme.dimens.spacingMicro
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = wayBillId,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium, color = Black
            ),
            modifier = Modifier.weight(1.3f)
        )

        Box(
            modifier = Modifier
                .padding(vertical = MaterialTheme.dimens.spacingSmall2)
                .weight(1f)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = cardGoldColor,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append("98")
                    }
                    withStyle(style = SpanStyle(color = GreenPrimary)) {
                        append("/100")
                    }
                },
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        Text(
            text = AppStrings.BANGALORE,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Medium,
                color = Black
            ),
            modifier = Modifier.weight(0.7f)
        )
    }
}