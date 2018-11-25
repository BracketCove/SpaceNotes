package com.wiseassblog.spacenotes.login

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
        fun event(event: LoginEvent<LoginResult>)
    }

}

internal const val SIGN_OUT = "SIGN OUT"
internal const val SIGN_IN = "SIGN IN"
internal const val SIGNED_IN = "Signed In"
internal const val SIGNED_OUT = "Signed Out"
internal const val ERROR_NETWORK_UNAVAILABLE = "Network Unavailable"
internal const val ERROR_AUTH = "An Error Has Occured"
internal const val RETRY = "RETRY"
internal const val ANTENNA_EMPTY = "antenna_empty"
internal const val ANTENNA_FULL = "antenna_full"

/**
 * This value is just a constant to denote our sign in request; It can be any int.
 * Would have been great if that was explained in the docs, I assumed at first that it had to
 * be a specific value.
 */
internal const val RC_SIGN_IN = 1337

sealed class LoginEvent<out T> {
    object OnAuthButtonClick : LoginEvent<Nothing>()
    object OnBackClick : LoginEvent<Nothing>()
    object OnStart : LoginEvent<Nothing>()
    data class OnGoogleSignInResult<out LoginResult>(val result: LoginResult) : LoginEvent<LoginResult>()
    object OnDestroy : LoginEvent<Nothing>()
}