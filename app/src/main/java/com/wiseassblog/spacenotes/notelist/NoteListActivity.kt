package com.wiseassblog.spacenotes.notelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.common.attachFragment
import com.wiseassblog.spacenotes.notelist.buildlogic.NoteListInjector

private const val VIEW = "NOTE_LIST"


class NoteListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)
        //A container basically just builds things and sets the feature in motion
        val view = this.supportFragmentManager.findFragmentByTag(VIEW)
                ?: NoteListView.newInstance()

        attachFragment(supportFragmentManager, R.id.root_activity_list, view, VIEW)

        NoteListInjector(application)
                .buildNoteListLogic(view as NoteListView)

    }
}
