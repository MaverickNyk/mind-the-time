package com.mindthetime.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import com.mindthetime.R
import com.mindthetime.model.TransportMode

class TransportModeAdapter(context: Context, private val transportModes: List<TransportMode>) :
    ArrayAdapter<TransportMode>(context, 0, transportModes) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // This view is for the dropdown when it is collapsed.
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        // This view is for the items in the dropdown list.
        return createItemView(position, convertView, parent)
    }

    /**
     * Creates the view for an item, used by both the collapsed and dropdown states.
     */
    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val transportMode = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context)
            .inflate(R.layout.dropdown_item_with_icon, parent, false)

        val icon = view.findViewById<ImageView>(R.id.icon)
        val text = view.findViewById<TextView>(R.id.text)

        if (transportMode != null) {
            icon.setImageResource(transportMode.iconResId)
            text.text = transportMode.displayName
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions = mutableListOf<TransportMode>()

                if (constraint == null || constraint.isEmpty()) {
                    suggestions.addAll(transportModes)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (item in transportModes) {
                        if (item.displayName.lowercase().contains(filterPattern)) {
                            suggestions.add(item)
                        }
                    }
                }

                results.values = suggestions
                results.count = suggestions.size
                return results
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                clear()
                if (results != null && results.count > 0) {
                    addAll(results.values as List<TransportMode>)
                }
                notifyDataSetChanged()
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                // This is crucial for displaying the correct name after selection.
                return (resultValue as? TransportMode)?.displayName ?: ""
            }
        }
    }
}