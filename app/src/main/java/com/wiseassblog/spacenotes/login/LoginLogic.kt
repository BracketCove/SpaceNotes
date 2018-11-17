package com.wiseassblog.spacenotes.login

import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.ServiceLocator
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.spacenotes.common.BaseLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class LoginLogic(dispatcher: DispatcherProvider,
                 locator: ServiceLocator,
                 val view: ILoginContract.View,
                 val authSource: AuthSource): BaseLogic(dispatcher, locator), CoroutineScope, ILoginContract.Logic {


    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker


    override fun event(event: LoginEvent) {
      when(event){
          is LoginEvent.OnStart -> onStart()
          is LoginEvent.OnBackClick -> onBackClick()
          is LoginEvent.OnLoginButtonClick -> onLoginButtonClick()
      }
    }

    private fun onLoginButtonClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onBackClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onStart() {

    }

}