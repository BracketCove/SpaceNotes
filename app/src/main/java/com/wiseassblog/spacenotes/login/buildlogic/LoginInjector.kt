package com.wiseassblog.spacenotes.login.buildlogic

import android.content.Context
import com.google.firebase.FirebaseApp
import com.wiseassblog.data.auth.FirebaseAuthRepositoryImpl
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.UserServiceLocator
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.repository.IAuthRepository
import com.wiseassblog.spacenotes.login.ILoginContract
import com.wiseassblog.spacenotes.login.LoginActivity
import com.wiseassblog.spacenotes.login.LoginLogic
import com.wiseassblog.spacenotes.login.LoginNavigator

class LoginInjector(activityContext: Context) {
    init {
        FirebaseApp.initializeApp(activityContext)
    }

    //For user management
    private val auth: IAuthRepository by lazy {
        FirebaseAuthRepositoryImpl()
    }

    fun provideLoginLogic(view: LoginActivity): ILoginContract.Logic {
        return LoginLogic(
                DispatcherProvider,
                UserServiceLocator(auth),
                LoginNavigator(view),
                view,
                AuthSource()
        )
    }
}