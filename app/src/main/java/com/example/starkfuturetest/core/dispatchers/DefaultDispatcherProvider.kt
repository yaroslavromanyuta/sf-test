package com.example.starkfuturetest.core.dispatchers

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultDispatcherProvider @Inject constructor() : DispatcherProvider {
    override val main = Dispatchers.Main
    override val io = Dispatchers.IO
    override val default = Dispatchers.Default
}