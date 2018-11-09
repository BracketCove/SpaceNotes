package com.wiseassblog.spacenotes.notedetail


import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.buildlogic.Injector
import com.wiseassblog.spacenotes.common.*
import com.wiseassblog.spacenotes.notelist.NoteListActivity
import kotlinx.android.synthetic.main.fragment_note_detail.*


class NoteDetailView : Fragment(), INoteDetailContract.View {
    override fun hideBackButton() {
        imb_toolbar_back.visibility = View.INVISIBLE
        imb_toolbar_back.isEnabled = false
    }

    override fun getTime(): String = getCalendarTime()

    override fun showConfirmDeleteSnackbar() {
        if (activity != null) {
            Snackbar.make(frag_note_detail, MESSAGE_DELETE_CONFIRMATION, Snackbar.LENGTH_LONG)
                    .setAction(MESSAGE_DELETE) { logic.event(NoteDetailEvent.OnDeleteConfirmed) }
                    .show()
        }
    }

    override fun showMessage(message: String) {
        makeToast(message)
    }

    override fun restartFeature() {
        restartCurrentFeature()
    }

    override fun getNoteBody(): String {
        return edt_note_detail_text.text.toString()
    }

    override fun startListFeature() {
        val i = Intent(this.activity, NoteListActivity::class.java)
        this.activity?.finish()
        startActivity(i)
    }

    override fun setBackgroundImage(imageUrl: String) {
        imv_note_detail_satellite.setImageResource(
                        resources.getIdentifier(imageUrl, "drawable", context?.packageName)
        )

        val satelliteLoop = imv_note_detail_satellite.drawable as AnimationDrawable
        satelliteLoop.start()
    }


    override fun setDateLabel(date: String) {
        lbl_note_detail_date.text = date
    }

    override fun setNoteBody(content: String) {
        edt_note_detail_text.text = content.toEditable()
    }

    lateinit var logic: INoteDetailContract.Logic



    override fun onStart() {
        super.onStart()
        logic.event(NoteDetailEvent.OnStart)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logic.bind()

        imb_toolbar_done.setOnClickListener { logic.event(NoteDetailEvent.OnDoneClick) }
        imb_toolbar_back.setOnClickListener { logic.event(NoteDetailEvent.OnBackClick) }
        imb_toolbar_delete.setOnClickListener { logic.event(NoteDetailEvent.OnDeleteClick) }

        val spaceLoop = frag_note_detail.background as AnimationDrawable
        spaceLoop.setEnterFadeDuration(1000)
        spaceLoop.setExitFadeDuration(1000)
        spaceLoop.start()

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(injector: Injector, id: String, isPrivate: Boolean) =
                NoteDetailView().setLogic(injector, id, isPrivate)
    }

    private fun setLogic(injector: Injector, id: String, isPrivate: Boolean): Fragment {
        logic = injector.provideNoteDetailLogic(this, id, isPrivate)
        return this
    }
}
