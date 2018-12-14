package com.wiseassblog.spacenotes.notelist

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.wiseassblog.spacenotes.common.BOOLEAN_EXTRA_IS_PRIVATE
import com.wiseassblog.spacenotes.common.STRING_EXTRA_NOTE_ID
import com.wiseassblog.spacenotes.login.LoginActivity
import com.wiseassblog.spacenotes.notedetail.NoteDetailActivity

class NoteListNavigator(val activity: NoteListActivity?) : INoteListContract.Navigator {
    override fun startNoteDetailFeatureWithExtras(noteId: String, isPrivate: Boolean) {
        val i = Intent(activity, NoteDetailActivity::class.java)
        i.putExtra(STRING_EXTRA_NOTE_ID, noteId)
        i.putExtra(BOOLEAN_EXTRA_IS_PRIVATE, isPrivate)
        activity?.startActivity(i)
    }

    override fun startLoginFeature() {
        val i = Intent(activity, LoginActivity::class.java)
        activity?.startActivity(i)
                .also { activity?.finish() }
    }
}
