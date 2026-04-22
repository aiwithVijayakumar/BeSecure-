package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

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
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.CompactOutlinedTextField
import com.tnd_psm.core.ui.theme.BoxFillColor
import com.tnd_psm.core.ui.theme.Graytext
import com.tnd_psm.core.ui.theme.Red
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.ui.theme.textColor
import com.tnd_psm.core.utils.noRippleClickable
import com.tnd_psm.feature_transhipment_unloading.presentation.component.markDamage.RemoveIconText

@Composable
fun AddButton(onAddClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                MaterialTheme.dimens.borderThin,
                shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall),
                color = Color.Black
            )
            .clickable {
                onAddClick()
            }
            .padding(vertical = MaterialTheme.dimens.small1),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(painter = painterResource(R.drawable.ic_plus), contentDescription = null)
        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small))
        Text(
            "ADD MORE",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun UpdateWithoutStickerCard(
    pkgText: MutableState<String>,
    remarkText: MutableState<String>,
    selectedImages: List<Uri>,   // ✅ changed from Int to Uri
    onCameraClick: () -> Unit,
    onRemoveClick: (Uri) -> Unit = {} // ✅ optional remove callback
) {
    val isExpanded = remember { mutableStateOf(false) }
    val selectedPreviewIndex = remember { mutableStateOf(0) }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .background(Color.White)
            .padding(MaterialTheme.dimens.spacingMedium8)
    ) {
        // Pkgs field
        CompactOutlinedTextField(
            value = pkgText.value,
            onValueChange = { pkgText.value = it },
            label = {
                Text(
                    "Pkgs #",
                    style = MaterialTheme.typography.labelLarge.copy(color = Graytext)
                )
            },
            placeholder = { Text("TOTAL PACKAGES", style = MaterialTheme.typography.labelLarge) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.labelLarge,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium9))

        // Remarks field
        CompactOutlinedTextField(
            value = remarkText.value,
            onValueChange = { remarkText.value = it },
            label = {
                Text(
                    "Remarks",
                    style = MaterialTheme.typography.labelLarge.copy(color = Graytext)
                )
            },
            placeholder = { Text("ADD REMARKS", style = MaterialTheme.typography.labelLarge) },
            modifier = Modifier.fillMaxWidth(),
            textStyle = MaterialTheme.typography.labelLarge,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium9))

        // Upload photos row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Upload Photos",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )

            if (selectedImages.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1)) {
                    selectedImages.forEachIndexed { index, uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(MaterialTheme.dimens.borderThick)
                                .border(
                                    MaterialTheme.dimens.borderThin,
                                    color = BoxFillColor,
                                    RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                                )
                                .size(MaterialTheme.dimens.spacingLarge2)
                                .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
                                .background(Color.White)
                                .clickable {
                                    selectedPreviewIndex.value = index  // ✅ update preview index
                                    isExpanded.value = true
                                },
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                // camera icon if no image
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.medium2)
                        .noRippleClickable { onCameraClick() }
                )
            }
        }

        // Expanded preview
        if (isExpanded.value && selectedImages.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.small1)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_box_pointer),
                    contentDescription = null,
                    modifier = Modifier
                        .height(MaterialTheme.dimens.spacingExtraLarge5)
                        .fillMaxWidth()
                )
                AsyncImage(
                    model = selectedImages[selectedPreviewIndex.value],
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
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

                    // 👇 Fix crash: adjust index safely
                    if (selectedImages.isEmpty()) {
                        isExpanded.value = false
                        selectedPreviewIndex.value = 0
                    } else if (currentIndex >= selectedImages.size) {
                        selectedPreviewIndex.value = selectedImages.lastIndex
                    }
                },
                iconTint = Red,
                textColor = textColor
            )

            /*    RemoveIconText(
                    onRemoveClick = {
                        onRemoveClick(selectedImages[selectedPreviewIndex.value])
                                    }, // ✅ remove callback
                    iconTint = Red,
                    textColor = textColor
                )*/
        }
    }
}

/*@Composable
fun UpdateWithoutStickerCard(
    pkgText: MutableState<String>,
    remarkText: MutableState<String>,
    selectedImages: List<Int>,
    onCameraClick: () -> Unit
) {
    val isExpanded = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .background(Color.White)
            .padding(MaterialTheme.dimens.spacingMedium8)
    ) {
        CompactOutlinedTextField(
            value = pkgText.value,
            onValueChange = { pkgText.value = it },
            label = {
                Text(
                    "Pkgs #",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Graytext,
                        fontWeight = FontWeight.Normal
                    ),
                )
            },
            placeholder = {
                Text(
                    "TOTAL PACKAGES",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium9))
        CompactOutlinedTextField(
            value = remarkText.value,
            onValueChange = { remarkText.value = it },
            label = {
                Text(
                    "Remarks",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Graytext,
                        fontWeight = FontWeight.Normal
                    ),
                )
            },
            placeholder = {
                Text(
                    "ADD REMARKS",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal)
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Normal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.spacingMedium9))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    isExpanded.value = !isExpanded.value
                }
        ) {
            Text(
                text = "Upload Photos",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
            )
            if (selectedImages.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1)
                ) {
                    selectedImages.forEach { imageId ->
                        Image(
                            painter = painterResource(id = imageId),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(MaterialTheme.dimens.borderThick)
                                .border(
                                    MaterialTheme.dimens.borderThin,
                                    color = BoxFillColor,
                                    RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                                )
                                .size(MaterialTheme.dimens.spacingLarge2)
                                .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall))
                                .background(Color.White)
                        )
                    }
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = null,
                    modifier = Modifier
                        .size(MaterialTheme.dimens.medium2)
                        .noRippleClickable {
                            onCameraClick()
                        }
                )
            }
        }

        if (isExpanded.value) {
            if (selectedImages.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = MaterialTheme.dimens.small1)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_box_pointer),
                        contentDescription = null,
                        modifier = Modifier
                            .height(MaterialTheme.dimens.spacingExtraLarge5)
                            .fillMaxWidth()
                    )
                    Image(
                        painter = painterResource(id = selectedImages[0]),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .height(MaterialTheme.dimens.spacingExtraLarge3)
                            .fillMaxWidth()
                            .padding(
                                horizontal = MaterialTheme.dimens.extraSmall,
                                vertical = MaterialTheme.dimens.extraSmall
                            )
                            .align(Alignment.Center)
                    )
                }
                RemoveIconText(onRemoveClick = {}, iconTint = Red, textColor = textColor)
            }
        }
    }
}*/
