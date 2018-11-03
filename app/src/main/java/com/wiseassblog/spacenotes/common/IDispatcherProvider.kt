package com.wiseassblog.spacenotes.common

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

object DispatcherProvider {
    fun provideUIContext(): CoroutineContext {
        return Dispatchers.Main
    }

    fun provideIOContext(): CoroutineContext {
        return Dispatchers.IO
    }
}