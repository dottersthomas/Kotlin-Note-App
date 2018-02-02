package io.github.thomas.dotters.noteapp.Adapters

import android.graphics.Color
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup



/**
 * Created by Thomas on 07/01/2018.
 */
class NoteRecyclerViewAdapter(val listener: (Note) -> Unit, val longListener: (View) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var items: ArrayList<ViewType>
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private var selectedItem: Int = -1
    private val mSelectedView: View? = null

    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.NOTES, NoteListAdapter(listener, longListener))
        items = ArrayList()


    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, this.items[position])

        holder.itemView.setOnLongClickListener(object :  View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                if (v != null) {
                    longListener(v)
                   // notifyItemChanged(position)
                    v.isSelected = true

                }

                return true
            }

        })

        if(selectedItem == position){
            holder.itemView.setBackgroundColor(Color.parseColor("#F96E62"))
        }
        else holder.itemView.setBackgroundColor(Color.parseColor("#f7f7f7"))
    }

    override fun getItemViewType(position: Int): Int {
        return this.items.get(position).getViewType()
    }

    //Load categories by default.
    fun addNotes(note : List<Note>){
        items.addAll(note)
        notifyItemRangeChanged(0, items.size)
    }

    fun addSingleCategory(note : Note){

        items.add(note)
        notifyItemRangeChanged(0, items.size)

    }

    fun getNoteAtPosition(position: Int) :Note {
        return getNotes()[position]
    }

    fun setSelectedAtPosition(position: Int){
        notifyItemChanged(selectedItem)
        selectedItem = position
        notifyItemChanged(selectedItem)

    }

    fun getSelectedPosition(): Int{
        return selectedItem
    }

    fun clearNotes(){
        val size = items.size
        items.clear()
        notifyItemRangeRemoved(0, size)
    }

    fun deleteNote(position: Int){
        items.removeAt(position)
        notifyItemChanged(position)
        notifyItemRangeChanged(position, items.size + 1);
    }

    fun getNotes() : List<Note>{
        return items
                .filter { it.getViewType() == AdapterConstants.NOTES }
                .map { it as Note }
    }

    private fun getLastPosition() = if( items.lastIndex == -1) 0 else items.lastIndex
}