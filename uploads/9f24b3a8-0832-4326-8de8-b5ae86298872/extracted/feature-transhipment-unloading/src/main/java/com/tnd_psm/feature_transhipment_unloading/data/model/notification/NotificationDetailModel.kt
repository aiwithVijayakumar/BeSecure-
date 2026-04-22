package com.tnd_psm.feature_transhipment_unloading.data.model.notification


data class NotificationDetailModel(
    val title: String,
    val vehicles: List<VehicleInfo>,
    val status: GroupStatus
)

data class VehicleInfo(
    val number: String,
    val eta: String
)


enum class GroupStatus {
    EARLY, ON_TIME, DELAYED
}
