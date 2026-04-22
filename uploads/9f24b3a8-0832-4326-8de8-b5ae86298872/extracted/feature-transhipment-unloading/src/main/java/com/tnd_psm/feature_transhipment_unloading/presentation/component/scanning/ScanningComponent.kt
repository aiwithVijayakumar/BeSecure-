package com.tnd_psm.feature_transhipment_unloading.presentation.component.scanning

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.scanning.CommonHighlightValueText
import com.tnd_psm.core.ui.component.scanning.InfoCardRow
import com.tnd_psm.core.ui.theme.Graytext
import com.tnd_psm.core.ui.theme.GreenPrimary
import com.tnd_psm.core.ui.theme.GreyDivider
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.data.model.scanning.ScannedPackageModel

@Composable
fun CounterRow(pkgBottomSheet: MutableState<Boolean>, unitBottomSheet: MutableState<Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CommonHighlightValueText("Pkgs Scanned: ", "603", "1200") {
            pkgBottomSheet.value = true

        }
        CommonHighlightValueText("Unit Scanned: ", "1", "12") {
            unitBottomSheet.value = true
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ScanPackageImgNText(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(top = MaterialTheme.dimens.spacingExtraLarge5) // Push it slightly down from top
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_scan),
                contentDescription = "Barcode",
                tint = Graytext,
                modifier = Modifier.size(MaterialTheme.dimens.spacingLarge5)
            )
            Spacer(modifier = Modifier.height(MaterialTheme.dimens.extraSmall))
            Text(
                "Scan Packages", style = MaterialTheme.typography.bodySmall.copy(
                    color = Graytext, fontWeight = FontWeight.Normal
                )
            )
        }
    }
}


@Composable
fun DetailedInfoCardScannedPackage(
    scannedPkgList: ScannedPackageModel,
    onDamageChecked: (Boolean) -> Unit = {},
    onQuantityClick: () -> Unit,
) {
    val markDamage = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.dimens.borderThick)
            .background(Color.White, shape = RoundedCornerShape(MaterialTheme.dimens.extraSmall))

    ) {
        // Top Row
        InfoCardRow(
            isWrongBay = scannedPkgList.isWrongBay,
            scannedPkgList.wbNo,
            modifier = Modifier.fillMaxWidth(),
            onQuantityClick = {
                onQuantityClick()
            },
            isScanningCompleted = scannedPkgList.isScanningCompleted,
            isError = scannedPkgList.isDamageError
        )


        // Bottom Row
        if (scannedPkgList.showExtendedView) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.dimens.spacingSmall3,
                        MaterialTheme.dimens.spacingSmall1
                    )
                    .border(
                        MaterialTheme.dimens.borderThin,
                        GreyDivider,
                        shape = RoundedCornerShape(MaterialTheme.dimens.small)
                    )
                    .padding(horizontal = MaterialTheme.dimens.small1),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Pkg: ${scannedPkgList.pkgNo}",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = markDamage.value,
                        onCheckedChange = { markDamage.value = !markDamage.value },
                        colors = CheckboxDefaults.colors(
                            checkedColor = GreenPrimary,
                            checkmarkColor = Color.White
                        )
                    )
                    Text(
                        text = AppStrings.MARK_DAMAGE,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}
