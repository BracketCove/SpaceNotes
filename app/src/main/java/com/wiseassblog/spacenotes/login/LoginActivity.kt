package com.wiseassblog.spacenotes.login

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.login.buildlogic.LoginInjector
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(), ILoginContract.View {
    lateinit var logic: ILoginContract.Logic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        logic = LoginInjector(this.applicationContext).provideLoginLogic(this)
        btn_auth_attempt.setOnClickListener { logic.event(LoginEvent.OnAuthButtonClick) }
        imb_toolbar_back.setOnClickListener { logic.event(LoginEvent.OnBackClick) }
    }


    override fun onResume() {
        super.onResume()
        logic.event(LoginEvent.OnStart)
    }

    override fun setLoginStatus(text: String) {
        lbl_login_status_display.text = text
    }

    override fun setAuthButton(text: String) {
        btn_auth_attempt.text = text
    }

    override fun showLoopAnimation() {
        imv_antenna_animation.setImageResource(
                resources.getIdentifier("antenna_loop", "drawable", this.packageName)
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

                logic.event(
                        LoginEvent.OnGoogleSignInResult(
                                LoginResult(
                                        requestCode,
                                        account
                                )
                        )
                )
            } catch (exception: Exception) {
                logic.event(
                        LoginEvent.OnGoogleSignInResult(
                                LoginResult(
                                        0,
                                        null
                                )
                        )
                )
            }

        }

    }
}
