package com.wiseassblog.spacenotes.notelist


import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ListAdapter
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.spacenotes.R
import com.wiseassblog.spacenotes.buildlogic.Injector
import com.wiseassblog.spacenotes.common.BOOLEAN_EXTRA_IS_PRIVATE
import com.wiseassblog.spacenotes.common.STRING_EXTRA_NOTE_ID
import com.wiseassblog.spacenotes.login.LoginActivity
import com.wiseassblog.spacenotes.notedetail.NoteDetailActivity
import kotlinx.android.synthetic.main.fragment_note_list.*


class NoteListView : Fragment(), INoteListContract.View {

    override fun setToolbarTitle(title: String) {
        lbl_toolbar_title.text = title
    }

    override fun startUserAuthActivity() {
        val i = Intent(this.activity, LoginActivity::class.java)
        this.activity?.finish()
        startActivity(i)
    }

    override fun setAdapter(adapter: ListAdapter<Note, NoteListAdapter.NoteViewHolder>) {
        rec_list_activity.adapter = adapter
    }


    override fun showLoadingView() {
        rec_list_activity.visibility = View.INVISIBLE
        fab_create_new_item.hide()
        imv_satellite_animation.visibility = View.VISIBLE

        //set loading animation
        val satelliteLoop = imv_satellite_animation.drawable as AnimationDrawable
        satelliteLoop.start()
    }

    override fun showEmptyState() {
        rec_list_activity.visibility = View.INVISIBLE
        fab_create_new_item.show()
        imv_satellite_animation.visibility = View.VISIBLE

        val satelliteLoop = imv_satellite_animation.drawable as AnimationDrawable
        satelliteLoop.start()
    }


    override fun showList() {
        rec_list_activity.visibility = View.VISIBLE
        fab_create_new_item.show()
        imv_satellite_animation.visibility = View.INVISIBLE

        val satelliteLoop = imv_satellite_animation.drawable as AnimationDrawable
        satelliteLoop.stop()
    }

    override fun startDetailActivity(noteId: String, isPrivate: Boolean) {
        val i = Intent(this.activity, NoteDetailActivity::class.java)
        i.putExtra(STRING_EXTRA_NOTE_ID, noteId)
        i.putExtra(BOOLEAN_EXTRA_IS_PRIVATE, isPrivate)
        this.activity?.finish()
        startActivity(i)
    }

    lateinit var logic: INoteListContract.Logic

    override fun onStart() {
        super.onStart()
        logic.event(NoteListEvent.OnStart)

    }

    override fun onDestroy() {
        logic.event(NoteListEvent.OnDestroy)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logic.event(NoteListEvent.OnBind)

        imv_toolbar_auth.setOnClickListener { logic.event((NoteListEvent.OnLoginClick)) }
        fab_create_new_item.setOnClickListener { logic.event(NoteListEvent.OnNewNoteClick) }

        val spaceLoop = imv_space_background.drawable as AnimationDrawable
        spaceLoop.setEnterFadeDuration(1000)
        spaceLoop.setExitFadeDuration(1000)
        spaceLoop.start()
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(injector: Injector): Fragment = NoteListView()
                .setLogic(injector)
    }

    private fun setLogic(injector: Injector): Fragment {
        logic = injector.provideNoteListLogic(this)
        return this
    }


}
