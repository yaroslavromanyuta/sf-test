package com.example.starkfuturetest.core.resources

interface ResourcesRepository {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg args: Any): String
}