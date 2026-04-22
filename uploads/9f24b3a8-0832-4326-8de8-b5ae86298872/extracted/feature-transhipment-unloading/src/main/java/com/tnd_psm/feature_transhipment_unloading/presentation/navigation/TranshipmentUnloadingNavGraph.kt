package com.tnd_psm.feature_transhipment_unloading.presentation.navigation

import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.tnd_psm.core.navigation.NavStateConst
import com.tnd_psm.core.navigation.StockTakeBaseRoute
import com.tnd_psm.core.navigation.TranshipmentUnLoadingBaseRoute
import com.tnd_psm.core.navigation.handleDashboardMenuAction
import com.tnd_psm.core.ui.component.processCardComponent.EditAction
import com.tnd_psm.core.ui.component.processCardComponent.ProgressStatus
import com.tnd_psm.core.ui.component.processCardComponent.TypeEdit
import com.tnd_psm.core.ui.component.processCardComponent.VehicleReleaseMenu
import com.tnd_psm.core.utils.stringFile.AppStrings
import com.tnd_psm.feature_transhipment_unloading.presentation.module.dashboard.dashboardScreen.TranshipmentUnloadingDashboardScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.dashboard.statusScreen.TranshipmentUnloadingStatusScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.dockAssignmentScreen.TranshipmentUnloadingSelectDockScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.editScreen.TranshipmentUnloadingEditScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.scanScreen.TranshipmentScanScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.dock.teamMemberScreen.TeamMemberAssignmentScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.markDamage.markdamageScreen.MarkDamageScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.notification.notificationDetailScreen.NotificationDetailScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.notification.notificationScreen.NotificationsScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.scanning.TranshipmentUnloadingScanningScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.scanning.UnloadingScanningSummaryScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.tally.ViewUnloadingTallyScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleCheck.TranshipmentUnloadingVehicleCheckScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.addMarkDamage.AddMarkDamageScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.removeExcessScreen.RemoveExcessPkgScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.takeSinglePhotoScreen.TakeSinglePhotoScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.updateShortPkgScreen.UpdateShortPkgScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen.MenuAction
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.uploadDamagePhotoScreen.UpLoadDamageExcessPhoto
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.vehicleReleaseScreen.TranshipmentUnloadingVehicleReleaseScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.verifiedScreen.VehicleVerificationScreen
import com.tnd_psm.feature_transhipment_unloading.presentation.module.vehicleRelease.withoutStickerScreen.UpdateWithoutStickerScreen

fun NavGraphBuilder.transUnloadingNavGraph(
    navHostController: NavHostController
) {
    navigation<TranshipmentUnLoadingBaseRoute>(startDestination = TranshipmentUnloadingDashboardRoute) {
        composable<TranshipmentUnloadingDashboardRoute> {
            TranshipmentUnloadingDashboardScreen(
                onStatusClick = { status, aboveBelow ->
                    navHostController.navigate(
                        TranshipmentUnloadingStatusRoute(
                            status = status.name,
                            aboveBelow = aboveBelow,
                            typeEdit = ""
                        )
                    )
                },
                onBackClick = {
                    navHostController.navigateUp()
                }, onNotificationClick = {
                    navHostController.navigate(
                        TranshipmentUnloadingNotificationRoute
                    )
                }, onActivityCardClick = { actionType ->
                    when (actionType) {
                        AppStrings.STOCK_TAKE -> { /* handle Stock Take */
                            navHostController.navigate(StockTakeBaseRoute)
                        }
                    }
                }, onFilterSelected = {
                    handleDashboardMenuAction(navHostController, it.text)
                }
            )
        }

        composable<TranshipmentUnloadingStatusRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TranshipmentUnloadingStatusRoute>()
            val status = try {
                ProgressStatus.valueOf(route.status)
            } catch (e: IllegalArgumentException) {
                ProgressStatus.PENDING
            }
            val savedStateHandle = backStackEntry.savedStateHandle

            val successSheetType = savedStateHandle.get<String?>(NavStateConst.SUCCESS_SHEET_TYPE)
            val rememberedTypeEdit = remember {
                successSheetType?.let {
                    savedStateHandle.remove<String>(NavStateConst.SUCCESS_SHEET_TYPE)
                    try {
                        TypeEdit.valueOf(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }

            TranshipmentUnloadingStatusScreen(
                typeEdit = rememberedTypeEdit?.name ?: "",
                status = status,
                onBackClick = { navHostController.navigateUp() },
                isAboveOrBelow = route.aboveBelow,
                onAction = { action ->
                    when (action) {
                        EditAction.OnViewClick -> {
                            navHostController.navigate(TranshipmentUnloadingPendingViewTallyRoute)
                        }

                        EditAction.OnEditClick -> {
                            navHostController.navigate(
                                TranshipmentUnloadingPendingEditRoute
                            )
                        }

                        EditAction.OnStartClick -> {
                            navHostController.navigate(
                                TranshipmentUnloadingVehicleCheckRoute

                            )
                        }

                        EditAction.OnVerifyClick -> {
                            navHostController.navigate(
                                TranshipmentUnloadingApprovalVehicleReleaseRoute(vehicleReleaseMenu = "")

                            )
                        }

                        EditAction.OnInitiateClick -> {
                            navHostController.navigate(TranshipmentUnloadingScanningRoute)
                        }

                        EditAction.OnAcknowledgeClick -> TODO()
                        EditAction.OnAcknowledgeNVerifyClick -> TODO()
                    }
                }
            )
        }

        composable<TranshipmentUnloadingPendingViewTallyRoute> {
            ViewUnloadingTallyScreen(navHostController)
        }


        composable<TranshipmentUnloadingPendingEditRoute> { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle

            val successSheetType = savedStateHandle.get<String?>(NavStateConst.SUCCESS_SHEET_TYPE)
            val rememberedTypeEdit = remember {
                successSheetType?.let {
                    savedStateHandle.remove<String>(NavStateConst.SUCCESS_SHEET_TYPE)
                    try {
                        TypeEdit.valueOf(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
            TranshipmentUnloadingEditScreen(
                onBackClick = { navHostController.navigateUp() },
                updated = rememberedTypeEdit != null,
                initialTypeEdit = rememberedTypeEdit,
                typeEditAction = { typeEditAction ->
                    when (typeEditAction) {
                        TypeEdit.ADD_WAY_BILL -> {}

                        TypeEdit.SELECT_DOCK -> navHostController.navigate(
                            TranshipmentUnloadingDockRoute
                        )

                        TypeEdit.CHANGE_TEAM_MEMBER -> navHostController.navigate(
                            TranshipmentUnloadingChangeTeamMemberRoute
                        )

                        TypeEdit.VEHICLE_CHECK_SUCCESS -> TODO()
                        TypeEdit.VEHICLE_SCAN_SUCCESS -> TODO()
                    }
                }
            )
        }


        composable<TranshipmentUnloadingDockRoute> {
            TranshipmentUnloadingSelectDockScreen(
                onBackClick = {
                    navHostController.navigateUp()
                },
                onMenuClick = {
                    navHostController.navigate(TranshipmentUnloadingDockScanRoute)
                },
                onButtonClick = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(NavStateConst.SUCCESS_SHEET_TYPE, TypeEdit.SELECT_DOCK.name)
                    navHostController.navigateUp()
                }
            )
        }

        composable<TranshipmentUnloadingChangeTeamMemberRoute> {
            TeamMemberAssignmentScreen(onBackClick = {
                navHostController.navigateUp()
            }, onButtonClick = {
                navHostController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(NavStateConst.SUCCESS_SHEET_TYPE, TypeEdit.CHANGE_TEAM_MEMBER.name)
                navHostController.navigateUp()
            })

        }

        composable<TranshipmentUnloadingDockScanRoute> {
            TranshipmentScanScreen(onBackClick = {
                navHostController.navigateUp()
            })
        }
        composable<TranshipmentUnloadingVehicleCheckRoute> {
            TranshipmentUnloadingVehicleCheckScreen(onBackClick = {
                navHostController.navigateUp()

            }, onSuccessBottomSheet = {
                navHostController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set(NavStateConst.SUCCESS_SHEET_TYPE, TypeEdit.VEHICLE_CHECK_SUCCESS.name)
                navHostController.navigateUp()

            })
        }

        composable<TranshipmentUnloadingApprovalVehicleReleaseRoute> { backStackEntry ->
            val savedStateHandle = backStackEntry.savedStateHandle

            val successSheetType = savedStateHandle.get<String?>(NavStateConst.SUCCESS_SHEET_TYPE)
            val rememberedVehicleReleaseMenu = remember {
                successSheetType?.let {
                    savedStateHandle.remove<String>(NavStateConst.SUCCESS_SHEET_TYPE)
                    try {
                        VehicleReleaseMenu.valueOf(it)
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }
            }
            TranshipmentUnloadingVehicleReleaseScreen(
                onBackClick = {
                    navHostController.navigateUp()
                },
                onButtonClick = {
                    navHostController.navigate(TranshipmentUnloadingVehicleVerificationRoute)
                },
                onExcessPkgClick = {
                    navHostController.navigate(TranshipmentUnloadingRemoveExcessPkgRoute)
                },
                vehicleReleaseMenu = rememberedVehicleReleaseMenu?.name ?: "",
                onMenuActionClick = { action ->
                    handleMenuAction(navHostController, action)
                })
        }

        composable<TranshipmentUnloadingVehicleVerificationRoute> { backStackEntry ->
            VehicleVerificationScreen(onBackClick = {
                navHostController.navigateUp()
            })
        }

        composable<TranshipmentUnloadingMarkDamageRoute> { backStackEntry ->
            MarkDamageScreen(
                onBackClick = { navHostController.navigateUp() },
            )
        }

        composable<TranshipmentUnloadingUpdateWithoutStickerRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<TranshipmentUnloadingUpdateWithoutStickerRoute>()
            UpdateWithoutStickerScreen(
                onBackClick = { navHostController.navigateUp() },
                onButtonClick = {
                    when (route.from) {
                        "VehicleRelease" -> {
                            navHostController.navigateUp()
                        }

                        "UnLoadingScanning" -> {
                            navHostController.navigate(
                                TranshipmentUnloadingUploadDamageExcessPhotoRoute
                            )
                        }
                    }

                }
            )
        }

        composable<TranshipmentUnloadingUpdateShortPkgRoute> { backStackEntry ->
            UpdateShortPkgScreen(
                onBackClick = { navHostController.navigateUp() },
                onButtonClick = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            NavStateConst.SUCCESS_SHEET_TYPE,
                            VehicleReleaseMenu.UPDATE_SHORT_PKGS.name
                        )
                    navHostController.navigateUp()
                },
                onMenuActionClick = { action ->
                    handleMenuAction(navHostController, action)
                })
        }
        composable<TranshipmentUnloadingRemoveExcessPkgRoute> { backStackEntry ->
            RemoveExcessPkgScreen(
                onBackClick = { navHostController.navigateUp() },
                onSubmitClick = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            NavStateConst.SUCCESS_SHEET_TYPE,
                            VehicleReleaseMenu.REMOVE_EXCESS_PKGS.name
                        )
                    navHostController.navigateUp()
                },
                onMenuActionClick = { action ->
                    handleMenuAction(navHostController, action)
                },
                onBottomClick = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            NavStateConst.SUCCESS_SHEET_TYPE,
                            VehicleReleaseMenu.REMOVE_EXCESS_PKGS.name
                        )
                    navHostController.navigateUp()
                }
            )
        }


        composable<TranshipmentUnloadingAddMarkDamageRoute> { backStackEntry ->
            AddMarkDamageScreen(
                onBackClick = { navHostController.navigateUp() },
                onButtonClick = {
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(
                            NavStateConst.SUCCESS_SHEET_TYPE,
                            VehicleReleaseMenu.ADD_DAMAGE_PKG.name
                        )
                    navHostController.navigateUp()
                }
            )
        }

        composable<TranshipmentUnloadingUploadDamageExcessPhotoRoute> { backStackEntry ->
            val navController = navHostController
            val savedStateHandle = backStackEntry.savedStateHandle

            UpLoadDamageExcessPhoto(
                savedStateHandle = savedStateHandle,
                onBackClick = { navController.navigateUp() },

                onTakePhotoClick = { _, _ ->
                    navController.navigate(
                        TranshipmentUnloadingTakeSinglePhotoRoute
                    )
                },
                onSlideToUpdate = {
                    navHostController.navigate(TranshipmentUnloadingScanningSummaryRoute) {
                        popUpTo(TranshipmentUnloadingUploadDamageExcessPhotoRoute) {
                            inclusive = true
                        }
                    }
                },
                onMenuActionClick = { action ->
                    handleMenuAction(navHostController, action)
                }
            )
        }


        composable<TranshipmentUnloadingScanningRoute> { backStackEntry ->

            TranshipmentUnloadingScanningScreen(
                onBackClick = { navHostController.navigateUp() },

                onButtonClick = {
                    navHostController.navigate(TranshipmentUnloadingScanningSummaryRoute)
                },
                onFilterSelected = { action ->
                    when (action.text) {
                        "Update Without Sticker" -> {
                            navHostController.navigate(
                                TranshipmentUnloadingUpdateWithoutStickerRoute(from = "UnLoadingScanning")
                            )
                        }
                        "Add Damage Pkg" -> {
                            navHostController.navigate(
                                TranshipmentUnloadingMarkDamageRoute
                            )
                        }
                    }
                },
                onDamageClick = {
                    navHostController.navigate(
                        TranshipmentUnloadingMarkDamageRoute
                    )
                },
            )
        }

        composable<TranshipmentUnloadingScanningSummaryRoute> { backStackEntry ->
            UnloadingScanningSummaryScreen(
                onBackClick = {
                    navHostController.navigateUp()
                }
            )
        }
        composable<TranshipmentUnloadingNotificationRoute> { backStackEntry ->
            NotificationsScreen(
                onBackClick = { navHostController.navigateUp() },
                onItemClick = {
                    navHostController.navigate(TranshipmentUnloadingNotificationDetailRoute)
                }
            )
        }
        composable<TranshipmentUnloadingNotificationDetailRoute> {
            NotificationDetailScreen(
                onBackClick = { navHostController.navigateUp() },

                )
        }

        composable<TranshipmentUnloadingTakeSinglePhotoRoute> {
            TakeSinglePhotoScreen(
                onBackClick = { navHostController.navigateUp() },
                onImageCaptured = { image ->
                    navHostController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selectedImage", image)
                    navHostController.navigateUp()
                })
        }
    }
}

fun handleMenuAction(
    navController: NavHostController,
    action: MenuAction
) {
    when (action) {
        MenuAction.UpdateWithoutSticker -> {
            navController.navigate(TranshipmentUnloadingUpdateWithoutStickerRoute(from = "VehicleRelease"))
        }

        MenuAction.RemoveExcessPkgs -> {
            navController.navigate(TranshipmentUnloadingRemoveExcessPkgRoute)
        }

        MenuAction.UpdateShortPkgs -> {
            navController.navigate(TranshipmentUnloadingUpdateShortPkgRoute)
        }

        MenuAction.AddDamagePkg -> {
            navController.navigate(TranshipmentUnloadingAddMarkDamageRoute)
        }

        MenuAction.PrintSticker -> {
            // You can handle showing a bottom sheet via state here if needed
        }
    }
}
