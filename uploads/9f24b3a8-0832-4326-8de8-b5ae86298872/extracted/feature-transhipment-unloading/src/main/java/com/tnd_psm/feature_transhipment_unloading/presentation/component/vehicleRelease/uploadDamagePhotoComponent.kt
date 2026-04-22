package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.HeaderExcess
import com.tnd_psm.core.ui.component.IconTextSpaceBetween
import com.tnd_psm.core.ui.theme.BoxFillColor
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.White
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable

data class UploadPackage(
    val packageId: String,
    val location: String,
    val uploadedImage: Int? = null
)

@Composable
fun SelectableViewUpLoad(
    title: String,
    expanded: Boolean,
    packages: List<UploadPackage>,
    onExpandToggle: () -> Unit,
    onItemClick: (index: Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.dimens.large2,
                vertical = MaterialTheme.dimens.spacingMedium1
            )
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandToggle() }
                .background(GreenPrimary)
        ) {
            IconTextSpaceBetween(title, expanded)
        }

        if (expanded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.dimens.small1,
                        vertical = MaterialTheme.dimens.small1
                    )
                ) {
                    HeaderExcess(
                        color = GreenPrimary.copy(alpha = 0.1f),
                        end = "Upload",
                        isBlack = true
                    )

                    packages.forEachIndexed { index, pkg ->
                        Box(modifier = Modifier.padding(vertical = MaterialTheme.dimens.extraSmall)) {
                            ContentUploadPhoto(
                                onItemClick = { onItemClick(index) },
                                image = pkg.uploadedImage,
                                packageId = pkg.packageId,
                                location = pkg.location
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContentUploadPhoto(
    onItemClick: () -> Unit,
    image: Int?,
    packageId: String,
    location: String
) {
    Log.d("TAG", "ContentUploadPhoto: $image")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = MaterialTheme.dimens.borderThick)
            .background(White)
            .noRippleClickable { onItemClick() }
            .padding(
                vertical = MaterialTheme.dimens.small1,
                horizontal = MaterialTheme.dimens.small1
            )
            .padding(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = packageId,
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
        )
        Text(
            text = location,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
        )


        // ✅ Wrap Image in a Box to align it like text
        Box(
            modifier = Modifier
                .weight(0.7f)
                .size(MaterialTheme.dimens.spacingMedium14),
            contentAlignment = Alignment.CenterStart
        ) {
            if (image != null) {
                Box(
                    modifier = Modifier.border(
                        width = MaterialTheme.dimens.borderThin,
                        color = BoxFillColor,
                        shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                    )
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = "Camera",
                        modifier = Modifier.size(MaterialTheme.dimens.spacingMedium13)
                    )
                }
            } else {
                Box(
                    modifier = Modifier

                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = "Camera",
                        modifier = Modifier.size(MaterialTheme.dimens.medium2)
                    )
                }
            }
        }
    }
}