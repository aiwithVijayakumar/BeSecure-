package com.tnd_psm.feature_transhipment_unloading.presentation.component.vehicleRelease

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.CommonBorderImage
import com.tnd_psm.core.ui.component.CountCommon
import com.tnd_psm.core.ui.theme.BoxFillColor
import com.tnd_psm.core.ui.theme.Gray
import com.tnd_psm.core.ui.theme.Graytext
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.Red
import com.tnd_psm.core.ui.theme.White
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.ui.theme.unselectedBgColor
import com.tnd_psm.core.utils.noRippleClickable

@Composable
fun ContentExcess(onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(

                MaterialTheme.dimens.borderThin,
                color = Color(0XFFE5E5E5),
                RoundedCornerShape(MaterialTheme.dimens.extraSmall)
            )
            .noRippleClickable {
                onItemClick()

            }
            .padding(
                vertical = MaterialTheme.dimens.extraSmall,
                horizontal = MaterialTheme.dimens.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9876 54326721006",
            modifier = Modifier.weight(1.3f),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
        Text(
            text = "Bangalore",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
        )
        Box(modifier = Modifier.weight(0.7f)) {
            CommonBorderImage()
        }
    }
}

@Composable
fun RemoveButton(onRemoveClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable {
                onRemoveClick()

            }
            .background(GreenPrimary, RoundedCornerShape(MaterialTheme.dimens.extraSmall))
            .padding(MaterialTheme.dimens.small1),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_start),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))
        Text(
            text = "Remove",
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun BottomSheetExcess(
    onSubmitClick: () -> Unit = {}
) {
    // Mutable list to hold scanned package IDs
    val scannedPackages = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.dimens.medium2))

        Text(
            text = "Scan Package",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )

        CurvedPackageRow()

        // Scan Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    MaterialTheme.dimens.borderThin,
                    Gray,
                    RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                )
                .noRippleClickable {
                    // Simulate a new package scan (can be replaced with actual scan logic)
                    val newPkgId = "987654326725${100 + scannedPackages.size}"
                    scannedPackages.add(newPkgId)
                }
                .padding(vertical = MaterialTheme.dimens.small1)
        ) {
            Image(painter = painterResource(R.drawable.ic_scan), contentDescription = null)
            Spacer(modifier = Modifier.width(MaterialTheme.dimens.small1))
            Text(
                "Scan Package",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Normal,
                    color = Graytext
                )
            )
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

        // LazyColumn only shown if items exist
        if (scannedPackages.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(scannedPackages.size) { pkgId ->
                    Spacer(modifier = Modifier.height(MaterialTheme.dimens.borderThick))
                    PackageInfoCard(packageId = "987654326725000$pkgId")
                }
            }
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(MaterialTheme.dimens.small1))

        // Submit Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (scannedPackages.isNotEmpty()) GreenPrimary else unselectedBgColor,
                    shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                )
                .clickable(enabled = scannedPackages.isNotEmpty()) { onSubmitClick() }
                .padding(vertical = MaterialTheme.dimens.small1),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Submit",
                style = if (scannedPackages.isNotEmpty()) MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ) else MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun PackageInfoCard(
    packageId: String = "9876543267250001"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0xFFE0E0E0),
                shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall)
            ) // light grey
            .padding(
                horizontal = MaterialTheme.dimens.medium2,
                vertical = MaterialTheme.dimens.spacingMedium1
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = "Pkg:",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = packageId,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Bay",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
            Text(
                text = "Patna",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
        }
    }
}


@Composable
fun CurvedPackageRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dimens.spacingMedium1)

    ) {
        // Left green rounded box
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(
                    RoundedCornerShape(
                        topStart = MaterialTheme.dimens.small1,
                        bottomStart = MaterialTheme.dimens.small1
                    )
                )
                .background(Color(0xFF1ABC5E)) // green
                .padding(
                    horizontal = MaterialTheme.dimens.spacingMedium1,
                    vertical = MaterialTheme.dimens.small1
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "PKG: 9876 54326721004",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        }

        // Right orange arrow box
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(ArrowShape()) // custom arrow shape
                .background(Color(0xFFFFE4B5)) // light orange
                .padding(
                    horizontal = MaterialTheme.dimens.spacingMedium1,
                    vertical = MaterialTheme.dimens.small1
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Scan Short Pkg",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }

}

fun ArrowShape(): Shape = GenericShape { size, _ ->
    // Define a simple right arrow shape
    moveTo(0f, 0f)
    lineTo(size.width * 0.85f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(size.width * 0.85f, size.height)
    lineTo(0f, size.height)
    close()
}

@Composable
fun WithoutStickerPkg() {
    Column() {
        HorizontalDivider(
            Modifier.padding(vertical = MaterialTheme.dimens.extraSmall),
            color = Gray
        )
        repeat(2) {
            WithoutStickerItemPkg()
        }
    }
}

@Composable
fun WithoutStickerItemPkg() {
    Box(
        modifier = Modifier
            .padding(horizontal = MaterialTheme.dimens.small)
            .fillMaxWidth()
            .border(MaterialTheme.dimens.borderThin, Gray)
            .padding(MaterialTheme.dimens.small)
    ) {
        Column {
            Text(
                "Pkgs #1",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
            )
            HorizontalDivider(Modifier.padding(top = MaterialTheme.dimens.extraSmall), color = Gray)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MaterialTheme.dimens.small),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Text box on the left
                Box(
                    modifier = Modifier
                        .border(
                            MaterialTheme.dimens.borderThin,
                            Gray,
                            RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                        )
                        .background(
                            BoxFillColor.copy(0.5f),
                            RoundedCornerShape(MaterialTheme.dimens.extraSmall)
                        )
                        .padding(
                            start = MaterialTheme.dimens.small2,
                            end = MaterialTheme.dimens.spacingMedium14,
                            top = MaterialTheme.dimens.small2,
                            bottom = MaterialTheme.dimens.small2
                        )
                ) {
                    Text(
                        "SAMSUNG",
                        color = Graytext,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal)
                    )
                }

                // 3 images on the right
                Row(
                    modifier = Modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small)
                ) {
                    CommonBorderImage()
                }
            }

        }
    }
}

@Composable
fun WaybillSectionExcessCard(
    title: String,
    count: Int,
    isExpanded: Boolean,
    isTabExcess: Boolean = false,
    onToggleExpand: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(top = MaterialTheme.dimens.spacingMedium1)
            .fillMaxWidth()
            .noRippleClickable { onToggleExpand() }
            .clip(RoundedCornerShape(MaterialTheme.dimens.extraSmall)),
        colors = CardDefaults.cardColors(Color.White)
    ) {
        Column(modifier = Modifier.padding(vertical = MaterialTheme.dimens.small2)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MaterialTheme.dimens.small2)
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                )
                CountCommon(
                    count,
                    isExpanded,
                    isTabExcess,
                    bgcolor = Red,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = White
                    ),
                )
            }

            if (isExpanded) {
                content()
            }
        }
    }
}
