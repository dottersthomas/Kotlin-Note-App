package io.github.thomas.dotters.noteapp.Adapters

/**
 * Created by Thomas on 07/01/2018.
 */
/**
 * Created by Thomas on 06/09/2017.
 */
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}