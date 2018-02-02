package io.github.thomas.dotters.noteapp.Adapters

/**
 * Created by Thomas on 07/01/2018.
 */


data class Note(val content: String, val date: String, val tag: String): ViewType {
    override fun getViewType() = AdapterConstants.NOTES
}