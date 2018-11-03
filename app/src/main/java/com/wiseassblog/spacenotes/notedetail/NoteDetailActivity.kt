package com.wiseassblog.spacenotes.notedetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.buildlogic.Injector
import com.wiseassblog.spacenotes.common.attachFragment
import com.wiseassblog.spacenotes.notelist.NoteListView

private const val VIEW = "NOTE_DETAIL"


class NoteDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_detail)



        val view = this.supportFragmentManager.findFragmentByTag(VIEW) as NoteDetailView?
                ?: NoteDetailView.newInstance(
                        Injector(this),
                        "28/10/2018"
                )

        attachFragment(supportFragmentManager, R.id.root_activity_detail, view, VIEW)
    }
}
