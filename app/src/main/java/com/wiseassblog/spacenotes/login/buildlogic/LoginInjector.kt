package com.wiseassblog.spacenotes.login.buildlogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.FirebaseApp
import com.wiseassblog.data.auth.FirebaseAuthRepositoryImpl
import com.wiseassblog.domain.DispatcherProvider
import com.wiseassblog.domain.UserServiceLocator
import com.wiseassblog.domain.interactor.AuthSource
import com.wiseassblog.domain.repository.IAuthRepository
import com.wiseassblog.spacenotes.login.ILoginContract
import com.wiseassblog.spacenotes.login.LoginActivity
import com.wiseassblog.spacenotes.login.LoginLogic

class LoginInjector(application: Application) : AndroidViewModel(application) {
    init {
        FirebaseApp.initializeApp(application)
    }

    //For user management
    private val auth: IAuthRepository by lazy {
        FirebaseAuthRepositoryImpl()
    }


    fun buildLoginLogic(view: LoginActivity): LoginLogic = LoginLogic(
            DispatcherProvider,
            UserServiceLocator(auth),
            view,
            AuthSource()
    ).also { view.setObserver(it) }
}