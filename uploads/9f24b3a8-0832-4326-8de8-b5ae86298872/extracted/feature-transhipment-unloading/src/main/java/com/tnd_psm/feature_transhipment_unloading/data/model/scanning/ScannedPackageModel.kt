package com.tnd_psm.feature_transhipment_unloading.data.model.scanning

data class ScannedPackageModel(
    var wbNo: String,
    var pkgNo: String,
    var bay: String,
    var totalPkgs: String,
    var scannedPkgs: String,
    var isWrongBay:Boolean,
    var isScanningCompleted:Boolean = false,
    var isDamageError:Boolean = false,
    var showExtendedView:Boolean = false
)
