package com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.takeSinglePhotoScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable

@Composable
fun TakeSinglePhotoScreen(onBackClick: () -> Unit, onImageCaptured: (Int) -> Unit) {
    val selectedImage = remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(
            title = "Take Photo",
            onBackClick = { onBackClick() },
            onMenuIconClick = {},
            notificationIconVis = false,
            menuIconVis = false,
            backIcon = R.drawable.ic_close_black
        )

        // Show thumbnail if image is selected
        selectedImage.value?.let { image ->
            Box(
                modifier = Modifier
                    .padding(
                        start = MaterialTheme.dimens.large2,
                        top = 48.dp,
                        bottom = 64.dp
                    )
                    .align(Alignment.BottomStart)
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .padding(top = 12.dp, end = 12.dp)
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(MaterialTheme.dimens.small1))
                            .background(Color.White)
                            .border(
                                1.dp,
                                Color.LightGray,
                                RoundedCornerShape(MaterialTheme.dimens.small1)
                            )
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_delete_circle),
                        contentDescription = "Delete",
                        modifier = Modifier
                            .size(27.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = MaterialTheme.dimens.small1, y = (-8).dp)
                            .clickable {
                                selectedImage.value = null
                            }
                    )
                }
            }
        }

        // Camera icon
        Image(
            painter = painterResource(R.drawable.ic_camera_button),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .noRippleClickable {
                    if (selectedImage.value == null) {
                        selectedImage.value = R.drawable.dummy_test
                    } else {
                        Toast.makeText(context, "Only 1 photo allowed", Toast.LENGTH_SHORT).show()
                    }
                }
        )

        // Slide to upload button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = "SLIDE TO UPLOAD",
                onBtnSwipe = {
                    selectedImage.value?.let {
                        onImageCaptured(it)
                    }
                },
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.dimens.extraLarge1, vertical = 11.dp)
                    .align(Alignment.Center),
                isEnabled = selectedImage.value != null
            )
        }
    }
}
