package io.github.thomas.dotters.noteapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import io.github.thomas.dotters.noteapp.Adapters.Note
import io.github.thomas.dotters.noteapp.Adapters.NoteRecyclerViewAdapter
import kotlinx.android.synthetic.main.note_editor.*
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.R.id.edit
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import android.text.Editable
import android.text.TextWatcher
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.EditText
import io.github.thomas.dotters.noteapp.R.string.app_name
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.view.MenuInflater




/**
 * Created by Thomas on 10/01/2018.
 */
class NoteEditorFragment : Fragment() {

    private var mContent: String? = null
    private var mDate: String? = null
    private var mTag:String? = null;

    private var mOldHash:Int? = null;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mContent = arguments.getString(ARG_CONTENT)
            mOldHash = mContent!!.hashCode()
            mDate = arguments.getString(ARG_DATE)
            mTag = arguments.getString(ARG_TAG)
        }

        activity.toolbar.title = ""
        (activity as MainActivity).toggle!!.isDrawerIndicatorEnabled = false

        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(true)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.note_editor, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setHasOptionsMenu(true)

        content_editor.setText(if (mContent != null) mContent else " ")

        content_editor.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //To fix word count
                val yourText = content_editor.getText().toString().replace(160.toChar().toString(), " ")
                if (yourText.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size > 10) {

                    var space = 0
                    var length = 0
                    for (i in 0..yourText.length - 1) {
                        if (yourText.get(i) == ' ') {
                            space++
                            if (space >= 10) {
                                length = i
                                break
                            }

                        }
                    }
                    if (length > 1) {
                        content_editor.getText().delete(length, yourText.length) // deleting last space
                        setCharLimit(content_editor, length - 1) //or limit edit text
                    }
                } else {
                    removeFilter(content_editor)
                }

                word_count_text.setText(countWords(content_editor.text.toString()).toString() + "/" + 10)
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }

            override fun afterTextChanged(s: Editable) {}
        })

    }



    private fun countWords(s: String): Int {
        val trim = s.trim()
        if (trim.isEmpty())
            return 0
        return s.split("\\s+".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray().size
    }

    private var filter: InputFilter? = null

    private fun setCharLimit(et: EditText, max: Int) {
        filter = InputFilter.LengthFilter(max)
        et.filters = arrayOf<InputFilter>(filter as InputFilter.LengthFilter)
    }

    private fun removeFilter(et: EditText) {
        if (filter != null) {
            et.filters = arrayOfNulls<InputFilter>(0)
            filter = null
        }
    }

    override fun onResume() {
        super.onResume()
        content_editor.requestFocus()
        (activity as AppCompatActivity).drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)


        val imgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.showSoftInput(content_editor, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun onSave(){
        if(mOldHash != null)
          Paper.book("content").delete(mOldHash.toString())

        if(content_editor.text.trim().length > 0) {

            val timezoneID = TimeZone.getDefault().getID()
            val sdf2 = SimpleDateFormat("dd-MM-yy")
            sdf2.timeZone = TimeZone.getTimeZone(timezoneID)
            mDate = sdf2.format(Calendar.getInstance().getTime())
            mContent = content_editor.text.toString()

            if (tag_editor.text.trim().isNotEmpty())
                mTag = tag_editor.text.toString()
            else
                mTag = "default"

            var note = Note(mContent!!, mDate!!, mTag!!)
            //TODO fix this to stop same content overwriting
            var mNewHash = mContent!!.hashCode()

            Paper.book("content").write(mNewHash.toString(), note)

        }

        (activity as MainActivity).shouldRefresh = true

        closeFragment()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.editor_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun closeFragment(){
        val imgr = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imgr.hideSoftInputFromWindow(content_editor.windowToken, 0)

        activity.supportFragmentManager.popBackStack();

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        Log.d("APP", id.toString())
        if (id == android.R.id.home) {
            activity.onBackPressed();
            if (activity.supportFragmentManager.backStackEntryCount > 0) {
               closeFragment()

                return true;
            }
        }

        if (id == R.id.toolbar_save){
            onSave()

            return true
        }

        if(id== R.id.toolbar_close){
            closeFragment()

            return true
        }


        return super.onOptionsItemSelected(item)

    }

    companion object {
        private val ARG_CONTENT = "CONTENT"
        private val ARG_DATE = "DATE"
        private val ARG_TAG = "TAG"



        fun newInstance(content: String, date: String, tag: String): NoteEditorFragment {
            val fragment = NoteEditorFragment()
            val args = Bundle()

            args.putString(ARG_CONTENT, content)
            args.putString(ARG_DATE, date)
            args.putString(ARG_TAG, tag)

            fragment.arguments = args
            return fragment
        }
    }
}
