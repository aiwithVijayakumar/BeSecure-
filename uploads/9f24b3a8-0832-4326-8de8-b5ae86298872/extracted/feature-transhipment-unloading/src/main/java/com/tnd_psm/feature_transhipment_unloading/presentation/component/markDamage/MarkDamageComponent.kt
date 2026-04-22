package com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.theme.BoxFillColor
import com.tnd_psm.core.ui.theme.DividerColor
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.core.utils.stringFile.AppStrings

@Composable
fun PackageDamageCard(
    onCameraClick: () -> Unit,
    selectedImages: List<Uri>,
    isExpandable: MutableState<Boolean>,
    onRemoveClick: (Uri) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .padding(vertical = MaterialTheme.dimens.borderThick),

        ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(MaterialTheme.dimens.spacingMedium1)
            ) {
                Text(
                    text = "Pkg ID:  1236 3626 3727 004",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
            NotUploadMarkDamageImage(
                selectedImages,
                onCameraClick,
                isExpandable,
                onRemoveClick = { uri ->
                    onRemoveClick(uri)
                })
        }
    }
}


@Composable
fun NotUploadMarkDamageImage(
    selectedImages: List<Uri>,
    onCameraClick: () -> Unit,
    isExpand: MutableState<Boolean>, onRemoveClick: (Uri) -> Unit
) {
    val selectedPreviewIndex = remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .background(Color.White)

    ) {
        if (selectedImages.isEmpty()) {
            Text(
                AppStrings.DAMAGE_TYPE,
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.dimens.large2,
                    vertical = MaterialTheme.dimens.small2
                )
            )
            HorizontalDivider(
                color = DividerColor,
                thickness = MaterialTheme.dimens.borderThin,
                modifier = Modifier.padding()
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.dimens.large2,
                    end = MaterialTheme.dimens.large2,
                    top = MaterialTheme.dimens.small2,
                    bottom = if (selectedImages.isNotEmpty()) 0.dp else MaterialTheme.dimens.small2,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (selectedImages.isNotEmpty()) "Upload Photos" else "Box Damage",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = if (selectedImages.isNotEmpty()) FontWeight.Bold else FontWeight.Normal)
            )
            if (selectedImages.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1)
                ) {
                    selectedImages.forEachIndexed { index, imageId ->
                        BoxBoarderImage(imageId, modifier = Modifier, onImageClick = {
                            isExpand.value = !isExpand.value
                            selectedPreviewIndex.value = index
                        })

                    }
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = AppStrings.CAMERA,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.medium2)
                        .noRippleClickable {
                            onCameraClick()
                        }
                )
            }
        }
        if (isExpand.value && selectedImages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.small1)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_box_pointer),
                    contentDescription = null,
                    modifier = Modifier
                        .height(170.dp)
                        .fillMaxWidth()
                )
                AsyncImage(
                    model = selectedImages[selectedPreviewIndex.value],
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .padding(horizontal = MaterialTheme.dimens.small3)
                        .height(MaterialTheme.dimens.spacingExtraLarge3)
                        .fillMaxWidth()
                        .padding(MaterialTheme.dimens.extraSmall)
                        .align(Alignment.Center)
                )
            }
            RemoveIconText(
                onRemoveClick = {
                    val currentIndex = selectedPreviewIndex.value
                    onRemoveClick(selectedImages[currentIndex])

                    if (selectedImages.isEmpty()) {
                        isExpand.value = false
                        selectedPreviewIndex.value = 0
                    } else if (currentIndex >= selectedImages.size) {
                        selectedPreviewIndex.value = selectedImages.lastIndex
                    }
                },
            )
        }
    }
}

@Composable
fun BoxBoarderImage(
    uri: Uri, onImageClick: () -> Unit = {}, modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(MaterialTheme.dimens.borderThick) // ⬅️ Outer spacing between items
            .size(MaterialTheme.dimens.spacingLarge2)
            .clickable { onImageClick() }
            .border(
                width = MaterialTheme.dimens.borderThin,
                color = BoxFillColor,
                shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall)
            )
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .background(Color.White)

    ) {
        AsyncImage(
            model = uri,
            contentDescription = null,
            modifier = Modifier
                .size(MaterialTheme.dimens.spacingLarge2)
                .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun GreenInfoMarkDamage(
    docTallyText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GreenPrimary)
            .padding(
                horizontal = MaterialTheme.dimens.spacingMedium8,
                vertical = MaterialTheme.dimens.small1
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(painter = painterResource(R.drawable.ic_file), contentDescription = null)
            Spacer(Modifier.width(MaterialTheme.dimens.extraSmall))
            Text(
                text = buildAnnotatedString {
                    append(docTallyText)
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("8769 7867 3540")
                    }
                },
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.ic_vehicle_release),
                contentDescription = null
            )
            Spacer(Modifier.width(MaterialTheme.dimens.extraSmall))
            Text(
                text = buildAnnotatedString {
                    append("UP03 MS ")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("4269")
                    }
                },
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White)
            )
        }
    }
}
