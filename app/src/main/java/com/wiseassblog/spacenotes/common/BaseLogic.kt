package com.wiseassblog.spacenotes.common

import com.wiseassblog.domain.DispatcherProvider
import kotlinx.coroutines.Job

/**
 * Why use a base class? To both share implementation (properties and functions), and enforce a contract (interface) for all logic classes
 */
abstract class BaseLogic(val dispatcher: DispatcherProvider) {

    protected lateinit var jobTracker: Job
}