package fr.salleron.nicolas.findmycity.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import fr.salleron.nicolas.findmycity.R
import com.google.android.gms.maps.*
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.google.android.gms.maps.model.*
import fr.salleron.nicolas.findmycity.data.City
import fr.salleron.nicolas.findmycity.data.Difficulty
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.google.android.gms.maps.model.BitmapDescriptorFactory




/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : AppCompatActivity(), GoogleMap.OnMapClickListener, StreetViewPanorama.OnStreetViewPanoramaChangeListener {

    private var mStreetViewPanorama: StreetViewPanorama? = null
    private var mMap:GoogleMap? = null
    @Suppress("PrivatePropertyName")
    private val TAG = "GameActivity"
    private var viewNormal: View? = null
    private var streetViewPanoramaFragment: SupportStreetViewPanoramaFragment? = null
    private var mapFragment: SupportMapFragment? = null
    private lateinit var arrayCity : ArrayList<City>
    private var currentCity = 0
    private var currentColor = 0
    private var currentScore : Long = 0
    private var currentDifficulty: Int = 0
    private var currentLvl : Int = 0
    private val DISTANCE_EARTH : Long = 40075
    private val arrayColor = intArrayOf(R.color.md_red_800,
            R.color.md_pink_800,
            R.color.md_purple_800,
            R.color.md_deep_purple_800,
            R.color.md_indigo_800,
            R.color.md_blue_800,
            R.color.md_light_blue_800,
            R.color.md_cyan_800,
            R.color.md_teal_800,
            R.color.md_green_800,
            R.color.md_light_green_800,
            R.color.md_lime_800,
            R.color.md_yellow_800,
            R.color.md_amber_800,
            R.color.md_orange_800,
            R.color.md_deep_orange_800,
            R.color.md_brown_800,
            R.color.md_grey_800,
            R.color.md_blue_grey_800)

    private var apiClient : GoogleApiClient? = null
    private var dialog : FancyGifDialog.Builder? = null

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewNormal = layoutInflater.inflate(R.layout.activity_game,null)
        setContentView(viewNormal)

        /* Villes suivant la difficulté */
        currentDifficulty = intent.extras.getInt("DIFFICULTY")
        arrayCity = Difficulty(currentDifficulty).mapData

        /* Pour la streetView */

        streetViewPanoramaFragment = supportFragmentManager
                .findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment

        streetViewPanoramaFragment?.getStreetViewPanoramaAsync(({
            panorama -> panorama.setPosition(
                LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
            mStreetViewPanorama = panorama
            mStreetViewPanorama?.setOnStreetViewPanoramaChangeListener(this@GameActivity)
            if (savedInstanceState == null) {
                mStreetViewPanorama?.setPosition(
                        LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
            }
            mStreetViewPanorama?.isStreetNamesEnabled = false
            mStreetViewPanorama?.isUserNavigationEnabled = true
            mStreetViewPanorama?.isPanningGesturesEnabled = true
            mStreetViewPanorama?.isZoomGesturesEnabled = true

        }))

        /* Pour la map */

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync { map ->
            map.setOnMapClickListener(this@GameActivity)
            map.resetMinMaxZoomPreference()
            mMap = map
            mMap?.isBuildingsEnabled = false
            mMap?.isIndoorEnabled = false
            mMap?.isTrafficEnabled = false
        }

        /* play services */
        apiClient = GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this) {
                    Log.e(TAG, "Could not connect to Play games services")
                    finish()
                }.build()

        Snackbar.make(viewNormal!!, "Astuce : "+
                    arrayCity[currentCity].help,Snackbar.LENGTH_LONG)
                .setDuration(5000)
                .show()
    }

    override fun onStreetViewPanoramaChange(p0: StreetViewPanoramaLocation?) {
        Log.e(TAG,"onStreetViewPanoramaChange")
    }


    override fun onMapClick(p0: LatLng?) {

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
        val polyline = mMap?.addPolyline(polyLineOptions)
        polyline?.isGeodesic = true

        mMap?.addMarker(MarkerOptions()
                .position(LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
                .draggable(true))

        mMap?.addMarker(MarkerOptions()
                .position(p0)
                .draggable(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_account_circle_white_24dp)))




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
        currentScore += (DISTANCE_EARTH -
                Math.ceil((distanceBeetweenMarker[0] / 1000).toDouble()).toLong())
        Log.e(TAG,"CurrentScore :" + currentScore )
        Games.Leaderboards.submitScore(apiClient,
                getString(R.string.leaderboard_classement_test_1),
                currentScore/1000) //le score augmentait trop vite


        val dialog = FancyGifDialog.Builder(this@GameActivity)
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Next !")
                .setNegativeBtnText("Mon score !")
                .isCancellable(false)


        if (Math.ceil((distanceBeetweenMarker[0] / 1000).toDouble()) > 1000) {
            dialog.setTitle("Dommage !\nTu feras mieux la prochaine fois !\n" +
                    Math.ceil((distanceBeetweenMarker[0] / 1000).toDouble()) +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_fail)
        } else {
            dialog.setTitle("Bravo ! \nTu es super fort !\n" +
                    Math.ceil((distanceBeetweenMarker[0] / 1000).toDouble()) +
                    " km de l'objectif\n" +
                    "C'était : " + arrayCity[currentCity].name)
            dialog.setGifResource(R.drawable.gif_success)   //Pass your Gif here

        }

        dialog.OnPositiveClicked {

            currentCity += 1

            /* Changement de niveau */
            if (currentCity > (arrayCity.size + (-1))) {

                if ((currentDifficulty + 1) > 2) {
                    endOfGame()
                } else {    /* Passage au niveau suivant */
                    FancyGifDialog.Builder(this)
                            .setPositiveBtnBackground("#FF4081")
                            .setPositiveBtnText("Next !")
                            .isCancellable(false)
                            .setTitle("Le niveau " + "" + "est fini !")
                            .setMessage("Voulez-vous lancer le prochain niveau ?")
                            .setGifResource(R.drawable.gif_success)   //Pass your Gif here
                            .OnPositiveClicked {
                                Difficulty(currentDifficulty + 1).mapData
                                currentCity = 0
                                mStreetViewPanorama?.setPosition(
                                        LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
                                mMap?.clear()
                            }
                            .build()
                }
            } else {    /* On change de ville */
                currentCity %= arrayCity.size
                mStreetViewPanorama?.setPosition(
                        LatLng(arrayCity[currentCity].lat!!, arrayCity[currentCity].lng!!))
                Snackbar.make(viewNormal!!, "Astuce : "+
                        arrayCity[currentCity].help,Snackbar.LENGTH_LONG)
                        .setDuration(5000)
                        .show()

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
                .setPositiveBtnText("C'est fini !")
                .isCancellable(false)
                .setTitle("Le niveau " + "" + "est fini !")
                .setMessage("Voulez-vous lancer le prochain niveau ?")
                .setGifResource(R.drawable.gif_success)   //Pass your Gif here
                .OnPositiveClicked {
                    finish()
                }
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
}
