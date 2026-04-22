package com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.scanScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.editTeamDock.CameraPreview
import com.tnd_psm.core.ui.component.editTeamDock.ScannerFrameOverlay
import com.tnd_psm.core.ui.theme.dimens

@Composable
fun TranshipmentScanScreen(onBackClick: () -> Unit) {
    ScanDockCamera(false, onBackClick, onTorchToggle = {})
}

@Composable
fun ScanDockCamera(
    isTorchOn: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onTorchToggle: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()

    ) {

        CameraPreview()
        // Overlay and UI above camera
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Top bar
            AppTopBar(
                title = "Scan Dock",
                notificationCount = 0,
                onBackClick = { onBackClick() },
                onNotificationClick = { /* Handle notification */ },
                onMenuIconClick = {},
                menuIconVis = false,
                modifier = Modifier.statusBarsPadding()
            )
            // Camera preview should be the lowest layer


            Box(modifier = Modifier.weight(1f)) {
                // Scanner overlay in the center below AppBar
                ScannerFrameOverlay()

                // Torch toggle aligned top end of camera area
                IconButton(
                    onClick = onTorchToggle,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(MaterialTheme.dimens.medium2)
                ) {
                    Icon(
                        painter = painterResource(
                            if (isTorchOn) R.drawable.ic_sun else R.drawable.ic_flash_off
                        ),
                        contentDescription = "Torch Toggle",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}