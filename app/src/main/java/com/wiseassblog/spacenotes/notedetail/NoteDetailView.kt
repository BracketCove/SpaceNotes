package com.wiseassblog.spacenotes.notedetail


import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.R.id.*
import com.wiseassblog.spacenotes.common.*
import com.wiseassblog.spacenotes.notedetail.buildlogic.NoteDetailInjector
import com.wiseassblog.spacenotes.notelist.NoteListEvent
import kotlinx.android.synthetic.main.fragment_note_detail.*


class NoteDetailView : Fragment(), INoteDetailContract.View {

    val event = MutableLiveData<NoteDetailEvent>()

    override fun setObserver(observer: Observer<NoteDetailEvent>) = event.observeForever(observer)

    override fun startListFeature() = com.wiseassblog.spacenotes.common.startListFeature(this.activity)

    override fun hideBackButton() {
        imb_toolbar_back.visibility = View.INVISIBLE
        imb_toolbar_back.isEnabled = false
    }

    override fun getTime(): String = getCalendarTime()

    override fun showConfirmDeleteSnackbar() {
        if (activity != null) {
            Snackbar.make(frag_note_detail, MESSAGE_DELETE_CONFIRMATION, Snackbar.LENGTH_LONG)
                    .setAction(MESSAGE_DELETE) { event.value = NoteDetailEvent.OnDeleteConfirmed }
                    .show()
        }
    }

    override fun showMessage(message: String) = makeToast(message)


    override fun restartFeature() = restartCurrentFeature()

    override fun getNoteBody(): String {
        return edt_note_detail_text.text.toString()
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

    override fun onStart() {
        super.onStart()
        event.value = NoteDetailEvent.OnBind
    }

    override fun onDestroy() {
        event.value = NoteDetailEvent.OnDestroy
        super.onDestroy()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        imb_toolbar_done.setOnClickListener { event.value = NoteDetailEvent.OnDoneClick }
        imb_toolbar_back.setOnClickListener { event.value = NoteDetailEvent.OnBackClick }
        imb_toolbar_delete.setOnClickListener { event.value = NoteDetailEvent.OnDeleteClick }

        val spaceLoop = frag_note_detail.background as AnimationDrawable
        spaceLoop.setEnterFadeDuration(1000)
        spaceLoop.setExitFadeDuration(1000)
        spaceLoop.start()

        super.onViewCreated(view, savedInstanceState)
    }


    companion object {
        @JvmStatic
        fun newInstance() =
                NoteDetailView()
    }
}
