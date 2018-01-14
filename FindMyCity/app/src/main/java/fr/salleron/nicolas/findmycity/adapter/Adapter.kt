package fr.salleron.nicolas.findmycity.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Created by nicolassalleron on 13/01/2018.
 */
class Adapter constructor(fragmentManager: FragmentManager, ArrayFrag: ArrayList<Fragment>) : FragmentStatePagerAdapter(fragmentManager) {

    private var fragments = ArrayFrag

    /**
     * On fournit la list des fragment à afficher
     * @param fragmentManager le fragment manager
     * @param fragments la liste des fragments
     */

    override fun getItem(position: Int): Fragment { //La position des fragments dans la liste.
        return this.fragments[position]
    }

    override fun getCount(): Int {     //La taille total des fragments
        return this.fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {    //Titre de chaque fragment
        when (position) {
            0 -> return "Scores !"
            1 -> return "About !"
        }
        return null
    }
}
