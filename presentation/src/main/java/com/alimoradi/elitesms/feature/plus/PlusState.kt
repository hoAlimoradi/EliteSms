package com.alimoradi.elitesms.feature.plus

data class PlusState(
    val upgraded: Boolean = false,
    val upgradePrice: String = "",
    val upgradeDonatePrice: String = "",
    val currency: String = ""
)