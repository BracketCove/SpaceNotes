package com.wiseassblog.spacenotes.notelist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.buildlogic.Injector
import com.wiseassblog.spacenotes.common.attachFragment

private const val VIEW = "NOTE_LIST"


class NoteListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_list)


        val view = this.supportFragmentManager.findFragmentByTag(VIEW) as Fragment?
                ?: NoteListView.newInstance(
                        Injector(this)
                )

        attachFragment(supportFragmentManager, R.id.root_activity_list, view, VIEW)
    }
}
