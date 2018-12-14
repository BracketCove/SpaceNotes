package com.wiseassblog.spacenotes.notedetail

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.wiseassblog.spacenotes.login.LoginActivity
import com.wiseassblog.spacenotes.notelist.NoteListActivity

class NoteDetailNavigator(val activity: NoteDetailActivity?): INoteDetailContract.Navigator {
    override fun startListFeature() {
        activity?.startActivity(Intent(activity, NoteListActivity::class.java))
                .also { activity?.finish() }
    }
}