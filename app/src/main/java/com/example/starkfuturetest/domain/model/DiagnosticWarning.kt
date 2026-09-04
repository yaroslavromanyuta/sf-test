package com.example.starkfuturetest.domain.model

data class DiagnosticWarning(
    val code: String,
    val message: String,
    val severity: WarningSeverity,
)