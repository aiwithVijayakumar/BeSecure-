package com.tnd_psm.feature_transhipment_unloading.presentation.module.markDamage.damageStickerScreen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.tnd_psm.core.R
import com.tnd_psm.core.ui.component.AppTopBar
import com.tnd_psm.core.ui.component.editTeamDock.SlideToBookButton
import com.tnd_psm.core.ui.theme.buttonBackground
import com.tnd_psm.core.ui.theme.dimens
import com.tnd_psm.core.utils.noRippleClickable

@Composable
fun TakeDamageStickerScreen(onBackClick: () -> Unit, onButtonClick: (List<Int>) -> Unit) {
    val imageList = remember { mutableStateListOf<Int>() }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AppTopBar(
            title = "Take Photos",
            onBackClick = {
                onBackClick()
            },
            onMenuIconClick = {},
            notificationIconVis = false,
            menuIconVis = false,
            backIcon = R.drawable.ic_close_black
        )


        // Thumbnails - aligned bottom-start with max height to avoid top bar
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = MaterialTheme.dimens.large2,
                    bottom = MaterialTheme.dimens.spacingLarge10,
                    top = MaterialTheme.dimens.spacingLarge5
                ) // avoid header
        ) {
            LazyColumn(
                modifier = Modifier.align(Alignment.BottomStart),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small1)
            ) {
                items(imageList) { image ->
                    Box(
                        modifier = Modifier
                            .padding(
                                top = MaterialTheme.dimens.small3,
                                end = MaterialTheme.dimens.small3
                            ) // give room for icon to render outside
                    ) {
                        Box(
                            modifier = Modifier.size(MaterialTheme.dimens.spacingLarge14)
                        ) {
                            // Thumbnail image
                            Image(
                                painter = painterResource(id = image),
                                contentDescription = null,
                                modifier = Modifier
                                    .matchParentSize()
                                    .clip(RoundedCornerShape(MaterialTheme.dimens.small1))
                                    .background(Color.White)
                                    .border(
                                        MaterialTheme.dimens.borderThin,
                                        Color.LightGray,
                                        RoundedCornerShape(MaterialTheme.dimens.small1)
                                    )
                            )

                            // Delete icon slightly outside top-right
                            Image(
                                painter = painterResource(R.drawable.ic_delete_circle),
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .size(MaterialTheme.dimens.spacingMedium12)
                                    .align(Alignment.TopEnd)
                                    .offset(
                                        x = MaterialTheme.dimens.small1,
                                        y = (-MaterialTheme.dimens.small1)
                                    )
                                    .clickable {
                                        imageList.remove(image)
                                    }
                            )
                        }
                    }
                }
            }
        }

        // Camera icon above the button
        Image(
            painter = painterResource(R.drawable.ic_camera_button),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = MaterialTheme.dimens.spacingLarge10)
                .noRippleClickable {
                    if (imageList.size < 2) {
                        val image = R.drawable.dummy_test
                        imageList.add(image)
                    } else {
                        Toast.makeText(
                            context,
                            "Only 2 photos allowed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.spacingLarge9)
                .align(Alignment.BottomCenter)
                .background(buttonBackground)
        ) {
            SlideToBookButton(
                btnText = "SLIDE TO UPLOAD",
                onBtnSwipe = {
                    onButtonClick(imageList.toList())

                },
                modifier = Modifier
                    .padding(
                        horizontal = MaterialTheme.dimens.extraLarge1,
                        vertical = MaterialTheme.dimens.spacingSmall3
                    )
                    .align(Alignment.Center),
                isEnabled = true
            )
        }
    }
}
