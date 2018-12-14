package com.wiseassblog.spacenotes.login

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.wiseassblog.spacenotes.notelist.NoteListActivity

class LoginNavigator(val activity: LoginActivity?) : ILoginContract.Navigator {
    override fun startListFeature() {
        activity?.startActivity(
                Intent(
                        activity,
                        NoteListActivity::class.java
                )
        ).also { activity?.finish() }
    }
}