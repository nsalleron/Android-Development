package fr.salleron.nicolas.findmycity.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
import android.widget.Toast
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
import java.io.FileReader
import java.io.PrintWriter
import java.util.*

/**
* Created by nicolassalleron on 15/01/2018.
*/
class GameFragmentActivity : FragmentActivity(),
        StreetFragment.OnFragmentInteractionListener,
        MapFragment.OnFragmentInteractionListener,
        GoogleMap.OnMapClickListener,
        StreetViewPanorama.OnStreetViewPanoramaChangeListener,GoogleApiClient.ConnectionCallbacks{

    @Suppress("PrivatePropertyName")
    private val TAG = "GameFragmentActivity"
    private var viewNormal: View? = null
    lateinit var arrayCity : ArrayList<City>
    private var currentCity = 0
    private var currentColor = 0
    private var currentDifficulty: Int = 0
    private var currentLvl : Int = 0
    private var currentScore : Int = 0
    private var currentPlayer : String = ""
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
    private var mode = ""

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Villes suivant la difficulté */
        currentDifficulty = intent.extras.getInt("DIFFICULTY")
        mode = intent.extras.getString("MODE")

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
                .addConnectionCallbacks(this)
                .enableAutoManage(this) {
                    Log.e(TAG, "Could not connect to Play games services")
                    finish()
                }.build()

        if(mode == getString(R.string.modeChrono)){
            FancyGifDialog.Builder(this)
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("Go Go Go !")
                    .setNegativeBtnText("Accueil")
                    .isCancellable(false)
                    .setTitle("Prêt ?")
                    .setGifResource(R.drawable.gif_8000)
                    .OnPositiveClicked {
                        Thread(MyTimer()).start()
                    }
                    .OnNegativeClicked {
                        finish()
                    }
                    .build()
        }






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

        showDialogAndComputeScore(p0)
    }

    private fun showDialogAndComputeScore(p0: LatLng?) {
        Log.e(TAG,"Current level of difficulty : "+ (currentDifficulty))

        val dialog = FancyGifDialog.Builder(this@GameFragmentActivity)
                .setPositiveBtnBackground("#FF4081")
                .setPositiveBtnText("Next !")
                .setNegativeBtnText("Mon score !")
                .isCancellable(false)

        giveMeGifAndTitle(dialog,p0!!.latitude, p0.longitude)

        dialog.OnPositiveClicked {
            currentCity += 1
            /* Changement de niveau */
            if (currentCity > (arrayCity.size + (-1))) {

                if ((currentDifficulty + 1) > 2) {  //Fin de niveau
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
        if(!apiClient!!.isConnected)
            return
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
        if (mode == getString(R.string.modeChrono)){
            FancyGifDialog.Builder(this)
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("Accueil")
                    .setNegativeBtnText("Partager !")
                    .isCancellable(false)
                    .setTitle("C'est fini, score : $currentScore !")
                    .setGifResource(R.drawable.gif_firework)
                    .OnPositiveClicked {
                        finish()
                    }
                    .OnNegativeClicked {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello ! J'ai réalisé un score de "+currentScore + " sur "+getString(R.string.app_name))
                        sendIntent.type = "text/plain"
                        startActivity(sendIntent)
                    }
                    .build()
        }else{
            FancyGifDialog.Builder(this)
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("Accueil")
                    .setNegativeBtnText("Partager !")
                    .isCancellable(false)
                    .setTitle("Vous avez fini mon jeu ! :)")
                    .setGifResource(R.drawable.gif_firework)   //Pass your Gif here
                    .OnPositiveClicked {
                        finish()
                    }
                    .OnNegativeClicked {
                        val sendIntent = Intent()
                        sendIntent.action = Intent.ACTION_SEND
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello ! J'ai réalisé "+currentScore + " sur "+R.string.app_name)
                        sendIntent.type = "text/plain"
                        startActivity(sendIntent)
                    }
                    .build()
        }


    }

    private fun showLeaderboard() {
        if(apiClient!!.isConnected)
             startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_classement_test_1)), 0)
        else
            Toast.makeText(this,"Connexion à internet impossible",Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog?.build()
    }

    fun goToViewTwo(){
        myPager?.setCurrentItem(1,true)
    }

    private fun giveMeGifAndTitle(dialog: FancyGifDialog.Builder,lat : Double, lng : Double) {

        var pointReponse = 0
        if(mode == getString(R.string.modeNormal) || mode == getString(R.string.modeChrono)){

            val distanceBeetweenMarker = floatArrayOf(1F)
            Location.distanceBetween(
                    arrayCity[currentCity].lat!!,
                    arrayCity[currentCity].lng!!,
                    lat,
                    lng,
                    distanceBeetweenMarker)
            val kilometer = Math.ceil((distanceBeetweenMarker[0] / 1000).toDouble())

            if (kilometer <= 50) {                                  //valide
                dialog.setTitle("Incroyable! \nTu vois de loin !\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_0)
                pointReponse = 10
            } else if (kilometer > 50 && kilometer <= 300) {        //valide
                dialog.setTitle("Bravo ! \nTu es super fort !\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_50)
                pointReponse = 8
            } else if (kilometer > 300 && kilometer <= 1000) {      //valide
                dialog.setTitle("Dommage !\nTu feras mieux la prochaine fois !\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_300)
                pointReponse = 6
            }else if (kilometer > 1000 && kilometer <= 2000) {      //valide
                dialog.setTitle("Arf !\nOn y était presque pourtant !\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_1000)
                pointReponse = 4
            } else if (kilometer > 2000 && kilometer <= 4000) {     //valide
                dialog.setTitle("Encore de la route!\nCourage!\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_2000)
                pointReponse = 3
            } else if (kilometer > 4000 && kilometer <= 8000) {     //valide
                dialog.setTitle("Heu...\nOn est ou là?!\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_4000)
                pointReponse = 2
            } else if (kilometer > 8000 && kilometer <= 16000) {    //valide
                dialog.setTitle("Ouah!\nTu es encore un peu loin\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_8000)
                pointReponse = 1
            } else if (kilometer > 16000) {   //valide
                dialog.setTitle("Whut !\nTu es trop loin !\n" +
                        kilometer +
                        " km de l'objectif\n" +
                        "C'était : " + arrayCity[currentCity].name)
                dialog.setGifResource(R.drawable.gif_16000)
                pointReponse = 0
            }
        }else{
            val country = getCountry(lat,lng)
            Log.e(TAG,"pour element : "+currentCity +" = "+country);
            if (country == arrayCity[currentCity].country) {
                dialog.setTitle("Incroyable! \nBien joué !\n" +
                        "C'était bien : " + arrayCity[currentCity].country)
                dialog.setGifResource(R.drawable.gif_0)
                pointReponse = 7
            } else  {        //valide
                dialog.setTitle("Dommage  \nUne prochaine fois !\n" +
                        "C'était : " + arrayCity[currentCity].country)
                dialog.setGifResource(R.drawable.gif_4000)
                pointReponse = 4
            }

        }

        /* Update du score */
        currentScore = currentScore + currentLvl + currentCity + pointReponse //Progression niveau et étape dans le niveau
        Log.e(TAG,"CurrentScore :" + currentLvl )
        if(apiClient!!.isConnected){
            Games.Leaderboards.submitScore(apiClient,
                    getString(R.string.leaderboard_classement_test_1),
                    currentScore.toLong()) //le score augmentait trop vite avec les km
        }


    }

    fun getCountry(lat: Double, lng : Double) : String{

        if(isNetworkAvailable()){
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat,lng,1)
            if(addresses.size != 0)
                return addresses.get(0).countryName
            else
                return "même pas un pays ! :O"
        }else{
            return "ERROR: Internet non disponible"
        }

    }

    inner class MyTimer : java.lang.Runnable {

        override fun run() {
            this.runTimer()
        }

        fun runTimer() {
            var i = 60 * 5
            while (i > 0) {
                runOnUiThread {
                    findViewById<Toolbar>(R.id.toolbar).title = "Chrono : $i secondes !"
                }
                try {
                    i--
                    Thread.sleep(1000L)
                } catch (ignored: InterruptedException) { }

            }
            endOfGame()
        }

    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    override fun onStreetViewPanoramaChange(p0: StreetViewPanoramaLocation?) {
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnected(p0: Bundle?) {
        val fos = openFileInput("_options.txt")
        val fi = FileReader(fos.fd)
        try {
            currentPlayer = fi.readLines()[0]
        }catch (e : Exception){
            currentPlayer = Games.Players.getCurrentPlayer(apiClient).displayName
        }

    }

    private fun isNetworkAvailable() : Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }


    override fun onDestroy() {
        super.onDestroy()
        val fos = openFileOutput("_scores.txt",Context.MODE_APPEND)
        val pw = PrintWriter(fos)
        //Log.e(TAG, ""+currentLvl+";"+currentScore+";"+ Date().toString()+"\n")
        val date = Date()
        //Flemme de chercher nouvelle API
        @Suppress("DEPRECATION")
        pw.println(""+(currentDifficulty+1)+";"+currentScore+";"+ date.date +"/" +(date.month+1)
                +"/"+(date.year+1900)+ " à "+date.hours+":"+date.minutes+":"+date.seconds+";"+currentPlayer)
        Log.e(TAG, "Errors ? : " + pw.checkError())
        pw.close()
    }

}