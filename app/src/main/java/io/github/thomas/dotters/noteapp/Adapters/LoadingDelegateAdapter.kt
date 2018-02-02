package io.github.thomas.dotters.noteapp.Adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.github.thomas.dotters.noteapp.R
import io.github.thomas.dotters.noteapp.inflate

/**
 * Created by Thomas on 07/01/2018.
 */
class LoadingDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = TurnsViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
    }

    class TurnsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.recycler_item_loading)) {
    }
}