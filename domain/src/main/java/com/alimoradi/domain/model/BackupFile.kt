package com.alimoradi.domain.model

data class BackupFile(
    val path: String,
    val date: Long,
    val messages: Int,
    val size: Long
)