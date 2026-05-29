package com.example.starkfuturetest.data.resources

import android.content.Context
import androidx.annotation.StringRes
import com.example.starkfuturetest.core.resources.ResourcesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourcesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ResourcesRepository {

    override fun getString(@StringRes resId: Int): String =
        context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}