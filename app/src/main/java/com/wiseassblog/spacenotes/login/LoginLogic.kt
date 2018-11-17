package com.wiseassblog.spacenotes.login

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.domainmodel.Result
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.spacenotes.common.BaseLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoginLogic(dispatcher: DispatcherProvider,
                 locator: ServiceLocator,
                 val view: ILoginContract.View,
                 val authSource: AuthSource) : BaseLogic(dispatcher, locator), CoroutineScope, ILoginContract.Logic {


    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker


    override fun event(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnStart -> onStart()
            is LoginEvent.OnBackClick -> onBackClick()
            is LoginEvent.OnAuthButtonClick -> onAuthButtonClick()
        }
    }

    private fun onAuthButtonClick() = launch {
        val authResult = authSource.getCurrentUser(locator)

        when (authResult) {
            is Result.Value -> {
                if (authResult.value == null) view.startSignInFlow()
                else signUserOut()
            }

            is Result.Error -> renderErrorState()
        }

    }

    private suspend fun signUserOut()  {
        val signOutResult = authSource.signOutCurrentUser(locator)

        when (signOutResult){
            is Result.Value -> renderNullUser()
            is Result.Error -> renderErrorState()
        }

    }

    private fun onBackClick() {
        view.startListFeature()
    }

    private fun onStart() = launch {
        val authResult = authSource.getCurrentUser(locator)

        when (authResult) {
            is Result.Value -> {
                if (authResult.value == null) renderNullUser()
                else renderActiveUser()
            }

            is Result.Error -> renderErrorState()
        }
    }

    private fun renderActiveUser() {
        view.showLoopAnimation()
        view.setAuthButton(SIGN_OUT)
        view.setLoginStatus(SIGNED_IN)
    }

    private fun renderNullUser() {
        view.setStatusDrawable(ANTENNA_FULL)
        view.setAuthButton(SIGN_IN)
        view.setLoginStatus(SIGNED_OUT)
    }

    private fun renderErrorState() {
        //TODO handle different types of errors
        view.setStatusDrawable(ANTENNA_EMPTY)
        view.setAuthButton(RETRY)
        view.setLoginStatus(NETWORK_UNAVAILABLE)

    }

}