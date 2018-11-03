package com.wiseassblog.spacenotes.notedetail


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.wiseassblog.domain.domainmodel.ColorType
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.buildlogic.Injector
import com.wiseassblog.spacenotes.common.MESSAGE_DELETE
import com.wiseassblog.spacenotes.common.MESSAGE_DELETE_CONFIRMATION
import com.wiseassblog.spacenotes.notelist.NoteListActivity
import kotlinx.android.synthetic.main.fragment_note_detail.*
import kotlinx.android.synthetic.main.item_note.*


class NoteDetailView : Fragment(), INoteDetailContract.View {
    override fun showConfirmDeleteSnackbar() {
        if (activity != null) {
            Snackbar.make(frag_note_detail, MESSAGE_DELETE_CONFIRMATION, Snackbar.LENGTH_LONG)
                    .setAction(MESSAGE_DELETE) { logic.noteDetailEvent(NoteDetailEvent.OnDeleteConfirmed) }
        }
    }

    override fun showMessage(message: String) {
        showMessage(message)
    }

    override fun restartFeature() {
        restartFeature()
    }

    override fun getNoteBody(): String {
        return lbl_message.text.toString()
    }

    override fun startListFeature() {
        val i = Intent(this.activity, NoteListActivity::class.java)
        this.activity?.finish()
        startActivity(i)
    }

    override fun setBackgroundImage(color: ColorType) {
        backgroundImage.setImageResource(
                        resources.getIdentifier(color.color, "drawable", context?.packageName)
        )
    }


    override fun setDateLabel(date: String) {
        dateLabel.text = date
    }

    override fun setNoteBody(content: String) {
        noteBody.text = content
    }

    lateinit var logic: INoteDetailContract.Logic

    override fun onStart() {
        super.onStart()
        logic.noteDetailEvent(NoteDetailEvent.OnStart)
    }

    override fun onDestroy() {
        logic.clear()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(injector: Injector, id: String) =
                NoteDetailView().setLogic(injector, id)
    }

    private fun setLogic(injector: Injector, id: String): Fragment {
        logic = injector.provideNoteDetailLogic(this, id)
        return this
    }
}
