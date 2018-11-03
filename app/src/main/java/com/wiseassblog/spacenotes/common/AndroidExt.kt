package com.wiseassblog.spacenotes.common

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.wiseassblog.spacenotes.notedetail.NoteDetailActivity
import com.wiseassblog.spacenotes.notedetail.NoteDetailView
import com.wiseassblog.spacenotes.notelist.NoteListActivity
import com.wiseassblog.spacenotes.notelist.NoteListView

internal fun Activity.attachFragment(manager: FragmentManager, containerId: Int, view: Fragment, tag:String) {
    manager .beginTransaction()
            .replace(containerId, view, tag)
            .commitNowAllowingStateLoss()
}

internal fun Fragment.showMessage(value: String) {
    Toast.makeText(activity, value, Toast.LENGTH_SHORT).show()
}

internal fun Fragment.restartFeature() {
    val i: Intent
    when (this){
        is NoteDetailView -> {
            i = Intent(this.activity, NoteDetailActivity::class.java)
        }

        //To Be Added

        else -> {
            i = Intent(this.activity, NoteListActivity::class.java)
        }
    }

    this.activity?.finish()
    startActivity(i)
}