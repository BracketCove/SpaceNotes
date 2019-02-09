package com.wiseassblog.spacenotes.notedetail

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.common.BOOLEAN_EXTRA_IS_PRIVATE
import com.wiseassblog.spacenotes.common.STRING_EXTRA_NOTE_ID
import com.wiseassblog.spacenotes.common.attachFragment
import com.wiseassblog.spacenotes.notedetail.buildlogic.NoteDetailInjector
import com.wiseassblog.spacenotes.notelist.NoteListActivity

private const val VIEW = "NOTE_DETAIL"


class NoteDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)

        //Elvis Operator val i:Intent = if intent is null,
        // assign i to Intent(this, NoteListActivity::class.java)
        val i: Intent = intent ?: Intent(this, NoteListActivity::class.java)

        //if intent is null, then it's time to gtfo
        if (intent == null) {
            Toast.makeText(this, "Application Restarted.", Toast.LENGTH_SHORT).show()
            startActivity(i)
        }

        val noteId = i.getStringExtra(STRING_EXTRA_NOTE_ID)
        val isPrivate = i.getBooleanExtra(BOOLEAN_EXTRA_IS_PRIVATE, true)

        val view = this.supportFragmentManager.findFragmentByTag(VIEW) as NoteDetailView?
                ?: NoteDetailView.newInstance()

        attachFragment(supportFragmentManager, R.id.root_activity_detail, view, VIEW)

        NoteDetailInjector(application)
                .buildNoteDetailLogic(view as NoteDetailView, noteId, isPrivate)
    }
}
