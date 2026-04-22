package com.tnd_psm.feature_transhipment_unloading.data.model.vehicleRelease

data class WayBillModel (
    val wbNumber: String,
    val pkgList: List<String>,
    val count: Int,
)
