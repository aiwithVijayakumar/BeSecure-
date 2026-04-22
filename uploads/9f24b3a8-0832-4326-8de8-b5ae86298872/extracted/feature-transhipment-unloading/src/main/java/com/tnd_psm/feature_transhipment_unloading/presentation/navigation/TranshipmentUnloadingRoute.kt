package com.tnd_psm.feature_transhipment_unloading.presentation.navigation


import kotlinx.serialization.Serializable


@Serializable
data object TranshipmentUnloadingDashboardRoute

@Serializable
data class TranshipmentUnloadingStatusRoute(
    val status: String,
    val aboveBelow: Boolean,
    val typeEdit: String? = null
)

@Serializable
data object TranshipmentUnloadingPendingViewTallyRoute

@Serializable
data object TranshipmentUnloadingPendingEditRoute

@Serializable
data object TranshipmentUnloadingDockRoute

@Serializable
data object TranshipmentUnloadingDockScanRoute

@Serializable
data object TranshipmentUnloadingChangeTeamMemberRoute

@Serializable
data object TranshipmentUnloadingVehicleCheckRoute

@Serializable
data class TranshipmentUnloadingApprovalVehicleReleaseRoute(
    val vehicleReleaseMenu: String? = null
)

@Serializable
data object TranshipmentUnloadingVehicleVerificationRoute

@Serializable
data class TranshipmentUnloadingUpdateWithoutStickerRoute(
    val from: String
)

@Serializable
data object TranshipmentUnloadingUpdateShortPkgRoute

@Serializable
data object TranshipmentUnloadingRemoveExcessPkgRoute

@Serializable
data object TranshipmentUnloadingMarkDamageRoute

@Serializable
data object TranshipmentUnloadingUploadDamageExcessPhotoRoute

@Serializable
data object TranshipmentUnloadingNotificationRoute

@Serializable
data object TranshipmentUnloadingNotificationDetailRoute

@Serializable
data object TranshipmentUnloadingScanningRoute

@Serializable
data object TranshipmentUnloadingAddMarkDamageRoute

@Serializable
data object TranshipmentUnloadingScanningSummaryRoute

@Serializable
data object TranshipmentUnloadingTakeSinglePhotoRoute
