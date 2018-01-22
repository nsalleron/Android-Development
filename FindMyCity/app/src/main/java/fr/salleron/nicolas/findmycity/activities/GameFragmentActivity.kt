package fr.salleron.nicolas.findmycity.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.PagerTabStrip
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.model.*
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
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
 * Cette classe permet l'instanciation des deux fragments [StreetFragment] et [MapFragment]
 * C'est également elle qui gère l'affichage des scores et le changement de niveaux.
 * Implements plusieurs interfaces principalement :
 *  - [GoogleMap.OnMapClickListener] pour handler les touches sur la carte
 *  - [StreetViewPanorama.OnStreetViewPanoramaChangeListener] pour handler les changements de l'utilisateur, not used
 *  - [GoogleApiClient.ConnectionCallbacks] pour handler le retour info des serveurs Google.
 * @author Nicolas Salleron
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

    /**
     * - Récupération de la difficulter [currentDifficulty],
     * - Instantiation du layout [viewNormal],
     * - Personnalisation toolbar [toolbar] et de la barre de status [window]
     * - Démarrage des fragments [streetViewFragment],[mapViewFragment] et liaison interfragments.
     * - Mise en place du viewPager [myPager]
     * - Mise en place du TitleTabStrip [pts]
     * - Mise en place de l'API Google Games [apiClient]
     * - Mise en place ou non du mode de jeu chronomètre [mode]
     */
    @SuppressLint("ObsoleteSdkInt", "InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* Villes suivant la difficulté */
        currentDifficulty = intent.extras.getInt("DIFFICULTY")
        mode = intent.extras.getString("MODE")

        arrayCity = Difficulty(currentDifficulty).mapData

        viewNormal = layoutInflater.inflate(R.layout.game_viewpager, null)
        setContentView(viewNormal)


        /* Personnaliser la toobar */
        val toolbar = findViewById<Toolbar>(R.id.toolbar) as Toolbar
        toolbar.title = "FindMyCity"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setTitleTextColor(resources.getColor(R.color.md_white_1000,theme))
        }else{
            @Suppress("DEPRECATION")
            toolbar.setTitleTextColor(resources.getColor(R.color.md_white_1000))
        }

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
        val pts = findViewById<PagerTabStrip>(R.id.pager_title_strip) as PagerTabStrip
        pts.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
        @Suppress("DEPRECATION")
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> pts.setTextColor(resources.getColor(R.color.colorPrimary,theme))
            else -> pts.setTextColor(resources.getColor(R.color.colorPrimary))
        }
        pts.textSpacing = 200
        pts.drawFullUnderline = false
        pts.tabIndicatorColor = resources.getColor(R.color.colorPrimary,theme)

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
                .setDuration(10000)
                .show()
    }


    /**
     * Handle du clique sur la carte [mapViewFragment].
     * Calcul distrance entre les points.
     * Mise en place du Marker.
     * Affichage du score via apl [showDialogAndComputeScore].
     */
    override fun onMapClick(p0: LatLng?) {
        LovelyStandardDialog(this@GameFragmentActivity)
                .setTopColor(resources.getColor(R.color.colorPrimary,theme))
                .setButtonsColorRes(R.color.md_deep_orange_500)
                .setIcon(R.drawable.ic_launcher_foreground)
                .setTitle("Êtes vous certain ?")
                .setMessage("Cette décision n'est pas annulable !")
                .setPositiveButton(android.R.string.ok,{
                    if(gameEnded){
                        endOfGame()
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
                            .draggable(false)
                            .title(arrayCity[currentCity].name)
                            .snippet(arrayCity[currentCity].snippet))

                    mapViewFragment?.mMap?.addMarker(MarkerOptions()
                            .position(p0)
                            .draggable(false)
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_account_circle_white_24dp)))

                    showDialogAndComputeScore(p0)
                })
                .setNegativeButton(android.R.string.no,{})
                .show()
    }

    /**
     * Détermine le score et affiche une boite de dialog personnalisée en fonction de la distance.
     */
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
                            .OnNegativeClicked {
                                finish()
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
                        .setDuration(10000)
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


    /**
     * Déverrouillage des scores via [apiClient]
     */
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
                            getString(R.string.achievement_jsuis_sur_cest_l))
            10 -> Games.Achievements
                    .unlock(apiClient,
                            getString(R.string.achievement_ppm_mon_ami))
            15 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_pas_besoin_de_carte))

        }

        when {
            currentScore > 25 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_25_points))
            currentScore > 50 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_50_points))
            currentScore > 100 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_100_points))
            currentScore > 125 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_125_points))
            currentScore > 150 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_150_points))
            currentScore > 200 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_200_points))
            currentScore > 250 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_vous_avez_dpass_250_points))
            currentScore > 270 -> Games.Achievements
                    .unlock(apiClient,getString(R.string.achievement_omg_omg_omg))
        }
    }

    /**
     * Fonction appellé quand c'est la fin de partie.
     */
    private fun endOfGame() {
        if (mode == getString(R.string.modeChrono)){
            if(apiClient!!.isConnected)
                Games.Leaderboards.submitScore(apiClient,
                    getString(R.string.leaderboard_scores_en_mode_chronomtre),
                    currentScore.toLong()) //le score augmentait trop vite avec les km

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
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hello ! J'ai réalisé "+currentScore + " sur "+getString(R.string.app_name))
                        sendIntent.type = "text/plain"
                        startActivity(sendIntent)
                    }
                    .build()
        }


    }

    /**
     * Affiche le leaderboard [apiClient]
     */
    private fun showLeaderboard() {
        if(apiClient!!.isConnected)
             startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_scores_des_joueurs)), 0)
        else
            Toast.makeText(this,"Connexion à internet impossible",Toast.LENGTH_SHORT).show()
    }

    /**
     * Appelé au retour de l'affichage du leaderboard [showLeaderboard]
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        dialog?.build()
    }

    /**
     * setOnClickListener du bouton contenu dans [streetViewFragment]
     */
    fun goToViewTwo(){
        myPager?.setCurrentItem(1,true)
    }

    /**
     * Fonction qui permet de donner un gif et un titre au dialog de [showDialogAndComputeScore]
     * Elle met également à jour le score
     */
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
            Log.e(TAG, "pour element : $currentCity = $country")
            pointReponse = if (country == arrayCity[currentCity].country) {
                dialog.setTitle("Incroyable! \nBien joué !\n" +
                        "C'était bien : " + arrayCity[currentCity].country)
                dialog.setGifResource(R.drawable.gif_0)
                7
            } else  {        //valide
                dialog.setTitle("Dommage  \nUne prochaine fois !\n" +
                        "C'était : " + arrayCity[currentCity].country)
                dialog.setGifResource(R.drawable.gif_4000)
                4
            }

        }

        /* Update du score */
        currentScore += currentLvl + currentCity + pointReponse //Progression niveau et étape dans le niveau
        Log.e(TAG,"CurrentScore :" + currentLvl )
        if(apiClient!!.isConnected){
            Games.Leaderboards.submitScore(apiClient,
                    getString(R.string.leaderboard_scores_des_joueurs),
                    currentScore.toLong()) //le score augmentait trop vite avec les km
        }


    }

    /**
     * Retourne la country pour un couple lat/lng
     * @param lat la latitude
     * @param lng la longitude
     */
    private fun getCountry(lat: Double, lng : Double) : String{

        return if(isNetworkAvailable()){
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat,lng,1)
            if(addresses.size != 0)
                addresses[0].countryName
            else
                "même pas un pays ! :O"
        }else{
            "ERROR: Internet non disponible"
        }

    }

    /**
     * C'est moche de faire comme ça,
     * TODO Asynctask
     */
    inner class MyTimer : java.lang.Runnable {

        override fun run() {
            this.runTimer()
        }

        private fun runTimer() {
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
            runOnUiThread { endOfGame()}
        }

    }

    /**
     * Not used
     */
    override fun onFragmentInteraction(uri: Uri) {}
    /**
     * Not used
     */
    override fun onStreetViewPanoramaChange(p0: StreetViewPanoramaLocation?) {}
    /**
     * Not used
     */
    override fun onConnectionSuspended(p0: Int) {}
    /**
     * Charge le nom du joueur si ce dernier est contenu dans un fichier ou au retour de l'[apiClient]
     * TODO dans le cas ou c'est pas disponible, forcer le chargement local!
     */
    override fun onConnected(p0: Bundle?) {
        val fos = openFileInput("_options.txt")
        val fi = FileReader(fos.fd)
        currentPlayer = try {
            fi.readLines()[0]
        }catch (e : Exception){
            Games.Players.getCurrentPlayer(apiClient).displayName
        }

    }

    /**
     * Permet de check la connexion réseau via [ConnectivityManager]
     */
    private fun isNetworkAvailable() : Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting
    }


    /**
     * Sauvegarde du résultat vers le fichier _score.txt en [Context.MODE_APPEND]
     */
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