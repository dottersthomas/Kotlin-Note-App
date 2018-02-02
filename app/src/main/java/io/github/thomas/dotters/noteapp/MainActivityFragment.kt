package io.github.thomas.dotters.noteapp

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import io.github.thomas.dotters.noteapp.Adapters.Note
import io.github.thomas.dotters.noteapp.Adapters.NoteListAdapter
import io.github.thomas.dotters.noteapp.Adapters.NoteRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import android.content.ContentValues.TAG
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.support.v4.widget.DrawerLayout
import android.view.*
import android.view.inputmethod.InputMethodManager
import io.paperdb.Paper
import kotlinx.android.synthetic.main.note_editor.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    private var mTag:String = "All Notes";



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mTag = arguments.getString(MainActivityFragment.ARG_TAG)
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        notes_list.layoutManager = LinearLayoutManager(context)

        initAdapter()
        (activity as AppCompatActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

        fragmentManager.addOnBackStackChangedListener {
            val backCount = fragmentManager.backStackEntryCount
            if (backCount == 0) {
                // block where back has been pressed. since backstack is zero.
                activity.toolbar.title = activity.getString(R.string.app_name)
                (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(false);
                (activity as AppCompatActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                (activity as MainActivity).toggle!!.isDrawerIndicatorEnabled = true

                val imgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imgr.hideSoftInputFromWindow(notes_list.windowToken, 0)

                if((activity as MainActivity).shouldRefresh){

                    reloadData()

                }

            }
        }

        if(savedInstanceState == null){

            doAsync {
                //var result = runLongTask()
                val data = mutableListOf<Note>()
                val keys =  Paper.book("content").allKeys
                for( i in 0..keys.size - 1) {

                    val readNote = Paper.book("content").read<Note>(keys[i])
                    data.add(readNote)

                }
                uiThread {
                    (notes_list.adapter  as NoteRecyclerViewAdapter).addNotes(data)
                }
            }

        }


    }

    fun reloadData(){
        doAsync {
            //var result = runLongTask()
            val data = mutableListOf<Note>()
            val keys =  Paper.book("content").allKeys
            for( i in 0..keys.size - 1) {

                val readNote = Paper.book("content").read<Note>(keys[i])
                data.add(readNote)

            }
            uiThread {
                //Clear adapter and begin to reload data
                (notes_list.adapter as NoteRecyclerViewAdapter).clearNotes()
                (notes_list.adapter  as NoteRecyclerViewAdapter).addNotes(data)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.add_note) {

            (activity as MainActivity).replaceFragmentSafely(NoteEditorFragment.newInstance("", "", ""), "Note", R.id.fragment, true)
            (notes_list.adapter as NoteRecyclerViewAdapter).setSelectedAtPosition(-1)

            val deleteItem = (activity as MainActivity).toolbar.menu.findItem(R.id.toolbar_delete)

            deleteItem.isVisible = false
            return true
        }
        if(id == R.id.toolbar_delete){
            val notePos = (notes_list.adapter as NoteRecyclerViewAdapter).getSelectedPosition()

            val note = (notes_list.adapter as NoteRecyclerViewAdapter).getNoteAtPosition(notePos)

            Paper.book("content").delete(note.content.hashCode().toString())

            (notes_list.adapter as NoteRecyclerViewAdapter).deleteNote(notePos)

            (notes_list.adapter as NoteRecyclerViewAdapter).setSelectedAtPosition(-1)

            val deleteItem = (activity as MainActivity).toolbar.menu.findItem(R.id.toolbar_delete)

            deleteItem.isVisible = false
        }


        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_main, menu)
        inflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun initAdapter() {
        if (notes_list.adapter == null) {
            notes_list.adapter = NoteRecyclerViewAdapter({v ->

                (notes_list.adapter as NoteRecyclerViewAdapter).setSelectedAtPosition(-1)

                (activity as MainActivity).replaceFragmentSafely(NoteEditorFragment.newInstance(v.content, v.date, v.tag), "Note", R.id.fragment, true)
                val deleteItem = (activity as MainActivity).toolbar.menu.findItem(R.id.toolbar_delete)

                deleteItem.isVisible = false

            }, {v->

                val viewPos = notes_list.getChildLayoutPosition(v)
                val note  = (notes_list.adapter as NoteRecyclerViewAdapter).getNoteAtPosition(viewPos)
                (notes_list.adapter as NoteRecyclerViewAdapter).setSelectedAtPosition(viewPos)

                val deleteItem = (activity as MainActivity).toolbar.menu.findItem(R.id.toolbar_delete)

                deleteItem.isVisible = true

                v.isSelected = true


            })
        }
    }

    companion object {

        private val ARG_TAG = "TAG"


        fun newInstance( tag: String): MainActivityFragment {
            val fragment = MainActivityFragment()
            val args = Bundle()


            args.putString(ARG_TAG, tag)

            fragment.arguments = args
            return fragment
        }
    }
}
