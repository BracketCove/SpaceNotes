package com.wiseassblog.spacenotes.common

import com.wiseassblog.domain.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Why use a base class? To both share implementation (properties and functions), and enforce a contract (interface) for all logic classes
 */
abstract class BaseLogic(val dispatcher: DispatcherProvider,
                         val locator: ServiceLocator) {

    protected lateinit var jobTracker: Job
}