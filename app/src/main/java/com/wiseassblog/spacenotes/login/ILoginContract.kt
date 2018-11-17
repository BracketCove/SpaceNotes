package com.wiseassblog.spacenotes.login

import com.wiseassblog.spacenotes.notedetail.NoteDetailEvent

interface ILoginContract {

    interface View {
        fun setLoginStatus(text: String)
        fun setAuthButton(text: String)
        fun showLoopAnimation()
        fun setStatusDrawable(imageURL: String)
        fun startSignInFlow()
        fun startListFeature()
    }

    interface Logic {
        fun event(event: LoginEvent)
    }

}

const val SIGN_OUT = "SIGN OUT"
const val SIGN_IN = "SIGN IN"
const val SIGNED_IN = "Signed In"
const val SIGNED_OUT = "Signed Out"
const val NETWORK_UNAVAILABLE = "Network Unavailable"
const val ERROR_AUTH = "An Error Has Occured"
const val RETRY = "RETRY"
const val ANTENNA_EMPTY = "antenna_empty"
const val ANTENNA_FULL = "antenna_full"

sealed class LoginEvent {
    object OnAuthButtonClick : LoginEvent()
    object OnBackClick : LoginEvent()
    object OnStart : LoginEvent()
    object OnSignInResult : LoginEvent()
    object OnDestroy : LoginEvent()
}