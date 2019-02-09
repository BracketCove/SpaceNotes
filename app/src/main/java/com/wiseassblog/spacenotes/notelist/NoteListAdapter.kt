package com.wiseassblog.spacenotes.notelist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.wiseassblog.domain.domainmodel.Note
import com.wiseassblog.spacenotes.R
import kotlinx.android.synthetic.main.item_note.view.*


class NoteListAdapter(var event: MutableLiveData<NoteListEvent<Int>> = MutableLiveData()  ) : ListAdapter<Note, NoteListAdapter.NoteViewHolder>(NoteDiffUtilCallback()) {

    internal fun setObserver(observer: Observer<NoteListEvent<Int>>) = event.observeForever(observer)

    override fun onBindViewHolder(holder: NoteListAdapter.NoteViewHolder, position: Int) {
        getItem(position).let { note ->
            with(holder) {
                holder.content.text = note.contents
                holder.date.text = note.creationDate
                holder.square.setImageResource(R.drawable.gps_icon)
                holder.content.text = note.contents
                holder.itemView.setOnClickListener {
                    event.value = NoteListEvent.OnNoteItemClick(position)

                }
            }
        }
        holder.apply {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NoteViewHolder(
                inflater.inflate(R.layout.item_note, parent, false)
        )
    }

    class NoteViewHolder(private val root: View) : RecyclerView.ViewHolder(root) {
        var square: ImageView = root.imv_list_item_icon
        var dateIcon: ImageView = root.imv_date_and_time
        var content: TextView = root.lbl_message
        var date: TextView = root.lbl_date_and_time
        var loading: ProgressBar = root.pro_item_data
    }
}

