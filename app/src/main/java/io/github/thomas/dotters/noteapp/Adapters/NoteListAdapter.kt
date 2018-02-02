package io.github.thomas.dotters.noteapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import io.github.thomas.dotters.noteapp.R
import io.github.thomas.dotters.noteapp.inflate
import kotlinx.android.synthetic.main.note_list_item.view.*


/**
 * Created by Thomas on 07/01/2018.
 */

class NoteListAdapter( val listener: (Note) -> Unit, val  longListener: (View) -> Unit) : ViewTypeDelegateAdapter {



    override fun onCreateViewHolder(parent: ViewGroup) = TurnsViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as TurnsViewHolder
        holder.bind(item as Note, listener, longListener)



    }

    class TurnsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.note_list_item)) {

        fun bind(item: Note, listener: (Note) -> Unit,  longListener: (View) -> Unit) = with(itemView) {
            content_text.text = item.content
            date_text.text = item.date
            setOnClickListener{listener(item)}

//            setOnLongClickListener(object :  View.OnLongClickListener{
//                override fun onLongClick(v: View?): Boolean {
//                    if (v != null) {
//                        longListener(v)
//
//                        v.isSelected = true
//
//                    }
//
//                    return true
//                }
//
//            })
        }

    }

}