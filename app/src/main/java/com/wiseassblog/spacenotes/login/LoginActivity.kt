package com.wiseassblog.spacenotes.login

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.login.buildlogic.LoginInjector
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Q: Why did I decide to use an Activity as the View in this feature?
 * A: Since I want to be able to use the GoogleSignIn API, there is necessary tight coupling
 * with Activity in this feature. Further, since this feature is quite simple to begin with,
 * I didn't mind breaking SoC a little bit in exchange for the GoogleSignIn functionality.
 *
 */
class LoginActivity : AppCompatActivity(), ILoginContract.View {
    override fun setObserver(observer: Observer<LoginEvent<LoginResult>>) = event.observeForever(observer)

    override fun startListFeature() = com.wiseassblog.spacenotes.common.startListFeature(this)

    val event = MutableLiveData<LoginEvent<LoginResult>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Note: I cal setObserver within the LoginInjector function
        ViewModelProviders.of(this)
                .get(LoginInjector::class.java)
                .buildLoginLogic(this)

        btn_auth_attempt.setOnClickListener { event.value = LoginEvent.OnAuthButtonClick }
        imb_toolbar_back.setOnClickListener { event.value = LoginEvent.OnBackClick }
    }


    override fun onResume() {
        super.onResume()
        event.value = LoginEvent.OnStart
    }

    override fun setLoginStatus(text: String) {
        lbl_login_status_display.text = text
    }

    override fun setAuthButton(text: String) {
        btn_auth_attempt.text = text
    }

    override fun showLoopAnimation() {
        imv_antenna_animation.setImageResource(
                resources.getIdentifier("antenna_loop_fast", "drawable", this.packageName)
        )

        val satelliteLoop = imv_antenna_animation.drawable as AnimationDrawable
        satelliteLoop.start()
    }

    override fun setStatusDrawable(imageURL: String) {
        imv_antenna_animation.setImageResource(
                resources.getIdentifier(imageURL, "drawable", this.packageName)
        )

    }

    override fun startSignInFlow() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)

                event.value = LoginEvent.OnGoogleSignInResult(
                        LoginResult(
                                requestCode,
                                account
                        )
                )

            } catch (exception: Exception) {
                Log.d("Login", exception.message)
                event.value = LoginEvent.OnGoogleSignInResult(
                        LoginResult(
                                0,
                                null
                        )
                )
            }
        }
    }
}
