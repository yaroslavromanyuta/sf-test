package com.example.starkfuturetest.domain.model

data class Diagnostics(
    val faultCodes: List<String>,
    val warnings: List<DiagnosticWarning>,
)