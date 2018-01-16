package fr.salleron.nicolas.findmycity.activities

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerTitleStrip
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import fr.salleron.nicolas.findmycity.R
import fr.salleron.nicolas.findmycity.adapter.OptionAdapter
import fr.salleron.nicolas.findmycity.fragments.AboutFragment
import fr.salleron.nicolas.findmycity.fragments.ScoreFragment
import java.util.ArrayList
import android.support.v4.content.ContextCompat
import android.view.WindowManager



/**
* Created by nicolassalleron on 15/01/2018.
*/
class ScoreAboutActivity : FragmentActivity(), AboutFragment.OnFragmentInteractionListener, ScoreFragment.OnFragmentInteractionListener {


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.option_viewpager)
        /* Personnaliser la toobar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Les options"
        toolbar.setTitleTextColor(resources.getColor(R.color.md_white_1000,theme))

        /* Changer la couleur de la status bar */
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        /* Ajout des fragments */

        val monScore = ScoreFragment()
        val findMyCityAbout = AboutFragment()
        val fragments = ArrayList<Fragment>()

        fragments.add(monScore)
        fragments.add(findMyCityAbout)

        //Mise en place de l'adaptateur
        val myPagerAdapter = OptionAdapter(supportFragmentManager, fragments)
        val myPager = findViewById<ViewPager>(R.id.options_viewpager) as ViewPager
        myPager.adapter = myPagerAdapter

        //Mise en place des titres
        val pts = findViewById<PagerTitleStrip>(R.id.pager_title_strip) as PagerTitleStrip
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pts.setTextColor(getColor(R.color.colorAccent))
        } else
            pts.setTextColor(resources.getColor(R.color.colorAccent,theme))
        pts.textSpacing = 200
        Log.e("TAG","END OK")
    }

    override fun onFragmentInteraction(uri: Uri) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}