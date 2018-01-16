package fr.salleron.nicolas.findmycity.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerTitleStrip
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import fr.salleron.nicolas.findmycity.R
import fr.salleron.nicolas.findmycity.adapter.GameAdapter
import fr.salleron.nicolas.findmycity.data.City
import fr.salleron.nicolas.findmycity.data.Difficulty
import fr.salleron.nicolas.findmycity.fragments.MapFragment
import fr.salleron.nicolas.findmycity.fragments.StreetFragment
import java.util.ArrayList

/**
* Created by nicolassalleron on 15/01/2018.
*/
class GameFragmentActivity : FragmentActivity(),
        StreetFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        GoogleMap.OnMapClickListener,
        StreetViewPanorama.OnStreetViewPanoramaChangeListener {

    @Suppress("PrivatePropertyName")
    private val TAG = "OLD_GameActivity"
    private var viewNormal: View? = null
    lateinit var arrayCity : ArrayList<City>
    private var currentCity = 0
    private var currentColor = 0
    private var currentDifficulty: Int = 0
    private var currentLvl : Int = 0
    private var currentScore : Int = 0
    private val arrayColor = intArrayOf(R.color.md_red_800, R.color.md_pink_800,
            R.color.md_purple_800, R.color.md_deep_purple_800, R.color.md_indigo_800,
            R.color.md_blue_800, R.color.md_light_blue_800, R.color.md_cyan_800,
            R.color.md_teal_800, R.color.md_green_800, R.color.md_light_green_800,
            R.color.md_lime_800, R.color.md_yellow_800, R.color.md_amber_800,
            R.color.md_orange_800, R.color.md_deep_orange_800, R.color.md_brown_800,
            R.color.md_grey_800, R.color.md_blue_grey_800)

    private var apiClient : GoogleApiClient? = null
    private var dialog : FancyGifDialog.Builder? = null
    private var streetViewFragment : StreetFragment? = null
    private var mapViewFragment : MapFragment? = null
    private var myPager : ViewPager? = null
    private var gameEnded = false

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Villes suivant la difficulté */
        currentDifficulty = intent.extras.getInt("DIFFICULTY")
        arrayCity = Difficulty(currentDifficulty).mapData
        @SuppressLint("InflateParams")
        viewNormal = layoutInflater.inflate(R.layout.game_viewpager,null)
        setContentView(viewNormal)

        /* Personnaliser la toobar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "FindMyCity"
        toolbar.setTitleTextColor(resources.getColor(R.color.md_white_1000,theme))

        /* Changer la couleur de la status bar */
        val window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        /* Ajout des fragments */

        mapViewFragment = MapFragment()
        streetViewFragment = StreetFragment()
        Log.e("GameFragmentActivity","EndDeclaration")
        val fragments = ArrayList<Fragment>()
        /* Référence interfragment */
        streetViewFragment?.mapFragmentInstance = mapViewFragment
        streetViewFragment?.gameFragmentActivity = this
        mapViewFragment?.streetFragmentInstance = streetViewFragment
        mapViewFragment?.gameFragmentActivity = this

        fragments.add(streetViewFragment!!)
        fragments.add(mapViewFragment!!)

        //Mise en place de l'adaptateur
        val myPagerAdapter = GameAdapter(supportFragmentManager, fragments)
        myPager = findViewById<ViewPager>(R.id.game_viewpager) as ViewPager
        myPager?.adapter = myPagerAdapter

        //Mise en place des titres
        val pts = findViewById<PagerTitleStrip>(R.id.pager_title_strip) as PagerTitleStrip
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pts.setTextColor(getColor(R.color.colorPrimary))
        } else
            pts.setTextColor(resources.getColor(R.color.colorPrimary,theme))
        pts.textSpacing = 200



        /* play services */
        apiClient = GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this) {
                    Log.e(TAG, "Could not connect to Play games services")
                    finish()
                }.build()

        Snackbar.make(viewNormal!!, "Astuce : "+
                arrayCity[currentCity].help, Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .show()
    }
    override fun onMapClick(p0: LatLng?) {
        if(gameEnded){
            endOfGame()
            return
        }



        /* Ligne entre les deux coordonnées */
        val points = ArrayList<LatLng>()
        val polyLineOptions = PolylineOptions()
        points.add(LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
        points.add(LatLng(p0!!.latitude,p0.longitude))
        polyLineOptions.width((7 * 1).toFloat())
        polyLineOptions.geodesic(true)
        currentColor = (currentColor+1)%arrayColor.size /*Changement de la couleur */
        polyLineOptions.color(arrayColor[currentColor])
        polyLineOptions.addAll(points)
        val polyline = mapViewFragment?.mMap?.addPolyline(polyLineOptions)
        polyline?.isGeodesic = true

        mapViewFragment?.mMap?.addMarker(MarkerOptions()
                .position(LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
                .draggable(true))

        mapViewFragment?.mMap?.addMarker(MarkerOptions()
                .position(p0)
                .draggable(false)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_account_circle_white_24dp)))

        showDialogAndNext(p0)
    }

    private fun showDialogAndNext(p0: LatLng?) {
        Log.e(TAG,"Current level of difficulty : "+ (currentDifficulty))
        val distanceBeetweenMarker = floatArrayOf(1F)
        Location.distanceBetween(
                arrayCity[currentCity].lat!!,
                arrayCity[currentCity].lng!!,
                p0!!.latitude,
                p0.longitude,
                distanceBeetweenMarker)

        /* Update du score */
        val score = currentLvl + currentScore
        currentScore = 0
        Log.e(TAG,"CurrentScore :" + currentLvl )
        Games.Leaderboards.submitScore(apiClient,
                getString(R.string.leaderboard_classement_test_1),
                score.toLong()) //le score augmentait trop vite


        val dialog = FancyGifDialog.Builder(this@GameFragmentActivity)
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Next !")
                .setNegativeBtnText("Mon score !")
                .isCancellable(false)

        val kilometer = Math.ceil((distanceBeetweenMarker[0] / 1000).toDouble())

        giveMeGifAndTitle(kilometer, dialog)

        dialog.OnPositiveClicked {

            currentCity += 1

            /* Changement de niveau */
            if (currentCity > (arrayCity.size + (-1))) {

                if ((currentDifficulty + 1) > 2) {
                    endOfGame()
                } else {    /* Passage au niveau suivant */
                    FancyGifDialog.Builder(this)
                            .setPositiveBtnBackground("#FF4081")
                            .setPositiveBtnText("Prochain niveau")
                            .setNegativeBtnText("Accueil")
                            .isCancellable(false)
                            .setTitle("Le niveau " + (currentDifficulty +1) + " est fini !\nLancer le prochain?")
                            .setGifResource(R.drawable.gif_50)   //Pass your Gif here
                            .OnPositiveClicked {
                                /* Augmentation de la difficulty */
                                currentDifficulty += 1
                                arrayCity = Difficulty(currentDifficulty).mapData

                                /* RAZ Villes, Map et update street */
                                currentCity = 0
                                streetViewFragment?.mStreetViewPanorama?.setPosition(
                                        LatLng(arrayCity[currentCity].lat!!,
                                                arrayCity[currentCity].lng!!))
                                mapViewFragment?.mMap?.clear()
                                myPager?.setCurrentItem(0,true)
                            }
                            .build()

                }
            } else {    /* On change de ville */
                currentCity %= arrayCity.size
                streetViewFragment?.mStreetViewPanorama?.setPosition(
                        LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))

                mapViewFragment?.mMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(0.0,0.0), 1.toFloat()))
                Snackbar.make(viewNormal!!, "Astuce : "+
                        arrayCity[currentCity].help,Snackbar.LENGTH_LONG)
                        .setDuration(5000)
                        .show()

                myPager?.setCurrentItem(0,true)


                /* Vérification des achievements */
                unlockAchievements()
            }

        }
        dialog.OnNegativeClicked {
            this.dialog = dialog
            this.showLeaderboard()
        }
        dialog.setMessage("trololo")
        dialog.build()
    }



    private fun unlockAchievements() {
        currentLvl += 1
        when (currentLvl) {
            1 -> Games.Achievements
                    .unlock(apiClient,
                            getString(R.string.achievement_bravo__vous_avez_dpass_1_niveau))
            3 -> Games.Achievements
                    .unlock(apiClient,
                            getString(R.string.achievement_alors_cest_2_avenue_de____))
            5 -> Games.Achievements
                    .unlock(apiClient,
                            getString(R.string.achievement_master_sar_reprsente))
            10 -> Games.Achievements
                    .unlock(apiClient,
                            getString(R.string.achievement_ppm_mon_ami))
        }
    }

    private fun endOfGame() {
        FancyGifDialog.Builder(this)
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Accueil")
                .setNegativeBtnText("Carte")
                .isCancellable(false)
                .setTitle("Vous avez fini mon jeu ! :)")
                .setGifResource(R.drawable.gif_firework)   //Pass your Gif here
                .OnPositiveClicked {
                    finish()
                }
                .OnNegativeClicked {gameEnded = true}
                .build()
    }

    private fun showLeaderboard() {
        startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_classement_test_1)), 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog?.build()
    }

    fun goToViewTwo(){
        myPager?.setCurrentItem(1,true)
    }

    private fun giveMeGifAndTitle(kilometer: Double, dialog: FancyGifDialog.Builder) {
        if (kilometer <= 50) {                                  //valide
            dialog.setTitle("Incroyable! \nTu vois de loin !\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_0)
        } else if (kilometer > 50 && kilometer <= 300) {        //valide
            dialog.setTitle("Bravo ! \nTu es super fort !\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_50)
        } else if (kilometer > 300 && kilometer <= 1000) {      //valide
            dialog.setTitle("Dommage !\nTu feras mieux la prochaine fois !\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_300)
        }else if (kilometer > 1000 && kilometer <= 2000) {      //valide
            dialog.setTitle("Arf !\nOn y était presque pourtant !\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_1000)
        } else if (kilometer > 2000 && kilometer <= 4000) {     //valide
            dialog.setTitle("Encore de la route!\nCourage!\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_2000)
        } else if (kilometer > 4000 && kilometer <= 8000) {     //valide
            dialog.setTitle("Heu...\nOn est ou là?!\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_4000)
        } else if (kilometer > 8000 && kilometer <= 16000) {    //valide
            dialog.setTitle("Ouah!\nTu es encore un peu loin\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_8000)
        } else if (kilometer > 16000) {   //valide
            dialog.setTitle("Whut !\nTu es trop loin !\n" +
                    kilometer +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_16000)
        }
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onStreetViewPanoramaChange(p0: StreetViewPanoramaLocation?) {
        currentScore += 1

    }


}