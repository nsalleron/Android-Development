package fr.salleron.nicolas.findmycity.data

import android.content.ClipData
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import fr.salleron.nicolas.findmycity.R

/**
 * Created by nicolassalleron on 17/01/2018.
 */

class ListAdapter : ArrayAdapter<String> {

    constructor(context: Context, textViewResourceId: Int) : super(context, textViewResourceId) {}

    constructor(context: Context, resource: Int, items: List<String>) : super(context, resource, items) {}

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {

        var v = convertView

        if (v == null) {
            val vi: LayoutInflater
            vi = LayoutInflater.from(context)
            v = vi.inflate(R.layout.itemlistrow, null)
        }

        val p = getItem(position)

        if (p != null) {
            val tt1 = v!!.findViewById<TextView>(R.id.score)
            val tt2 = v.findViewById<TextView>(R.id.date)
            val tmp = p.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (tt1 != null) {
                tt1.text = "Niveau : "+tmp[0] + "\nScore  : " +tmp[1]
            }
            if (tt2 != null) {
                tt2.text = tmp[2]
            }
            if (tt2 != null) {
                tt2.text = "Joueur : " + tmp[3]
            }
        }

        return v
    }

}