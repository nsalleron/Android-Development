package fr.salleron.nicolas.findmycity.activities


import android.location.Location
import android.media.Image
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import fr.salleron.nicolas.findmycity.R
import com.google.android.gms.maps.*
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener
import android.widget.LinearLayout
import com.google.android.gms.maps.model.*
import fr.salleron.nicolas.findmycity.data.City
import fr.salleron.nicolas.findmycity.data.Difficulty
import kotlinx.android.synthetic.main.activity_game.*
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.R.color.transparent
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.BitmapDescriptor




/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class GameActivity : AppCompatActivity(), GoogleMap.OnMapClickListener, StreetViewPanorama.OnStreetViewPanoramaChangeListener {

    private var mStreetViewPanorama: StreetViewPanorama? = null
    private var mMap:GoogleMap? = null
    private val TAG = "GameActivity"
    private var viewNormal: View? = null
    var streetViewPanoramaFragment: SupportStreetViewPanoramaFragment? = null
    var mapFragment: SupportMapFragment? = null
    private lateinit var arrayCity : ArrayList<City>
    private var currentCity = 0
    private var currentColor = 0
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewNormal = layoutInflater.inflate(R.layout.activity_game,null)
        setContentView(viewNormal)

        /* Villes suivant la difficulté */
        arrayCity = Difficulty(intent.extras.getInt("DIFFICULTY")).mapData

        /* Pour la streetView */

        streetViewPanoramaFragment = supportFragmentManager
                .findFragmentById(R.id.streetviewpanorama) as SupportStreetViewPanoramaFragment

        streetViewPanoramaFragment?.getStreetViewPanoramaAsync(({
            panorama -> panorama.setPosition(
                LatLng(arrayCity.get(currentCity).lat!!,arrayCity.get(currentCity).lng!!))
            mStreetViewPanorama = panorama
            mStreetViewPanorama?.setOnStreetViewPanoramaChangeListener(this@GameActivity)
            if (savedInstanceState == null) {
                mStreetViewPanorama?.setPosition(
                        LatLng(arrayCity.get(currentCity).lat!!,arrayCity.get(currentCity).lng!!))
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

            // Creates a draggable marker. Long press to drag.
            /*
            mMarker = map.addMarker(MarkerOptions()
                    .position(SYDNEY)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plusone_medium_off_client))
                    .draggable(true))
            */
        }





    }

    override fun onResume() {
        super.onResume()
        Toast.makeText(this.applicationContext,"Bonne chance dans votre recherche !",Toast.LENGTH_LONG).show()
    }

    override fun onStreetViewPanoramaChange(p0: StreetViewPanoramaLocation?) {
        Log.e(TAG,"onStreetViewPanoramaChange")
    }


    override fun onMapClick(p0: LatLng?) {

        val points = ArrayList<LatLng>()
        val polyLineOptions = PolylineOptions()

        points.add(LatLng(arrayCity.get(currentCity).lat!!, arrayCity.get(currentCity).lng!!))
        points.add(LatLng(p0!!.latitude,p0.longitude))
        polyLineOptions.width((7 * 1).toFloat())
        polyLineOptions.geodesic(true)
        currentColor = (currentColor+1)%arrayColor.size
        polyLineOptions.color(this.getResources().getColor(arrayColor[currentColor]))
        polyLineOptions.addAll(points)
        val polyline = mMap?.addPolyline(polyLineOptions)
        polyline?.setGeodesic(true)



        mMap?.addMarker(MarkerOptions()
                .position(LatLng(arrayCity.get(currentCity).lat!!, arrayCity.get(currentCity).lng!!))
                .draggable(true))

        mMap?.addMarker(MarkerOptions()
                .position(p0)
                .draggable(false)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)))


        var results = floatArrayOf(1F)
        Location.distanceBetween(
                arrayCity.get(currentCity).lat!!,
                arrayCity.get(currentCity).lng!!,
                p0.latitude,
                p0.longitude,
                results)

        val dialog = FancyGifDialog.Builder(this)

        if(Math.ceil((results[0]/1000).toDouble()) > 1000){
            dialog.setTitle("Dommage !")
            dialog.setMessage("Tu feras mieux la prochaine fois ! Tout n'est que recommencement !"  + "Tu n'es qu'à "+ Math.ceil((results[0]/1000).toDouble()) + " km de l'objectif  \n"+"C'était : "+arrayCity.get(currentCity).name)
            dialog.setNegativeBtnText("Carte")
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("Next !")
                    .setNegativeBtnBackground("#FFA9A7A8")
                    .setGifResource(R.drawable.gif_fail)   //Pass your Gif here
                    .isCancellable(true)
                    .OnPositiveClicked {
                        currentCity += 1
                        currentCity = currentCity%arrayCity.size
                        mStreetViewPanorama?.setPosition(
                                LatLng(arrayCity.get(currentCity).lat!!,arrayCity.get(currentCity).lng!!))
                    }
                    .OnNegativeClicked {
                        Toast.makeText(this.applicationContext,"OnNegativeClicked",Toast.LENGTH_LONG).show()
                    }
                    .build()
        }else{
            dialog.setTitle("Bravo ! ")
            dialog.setMessage("Tu es super fort ! \n Tu n'es qu'à "+ Math.ceil((results[0]/1000).toDouble()) + " km de l'objectif  \n"+"C'était : "+arrayCity.get(currentCity).name)
            dialog.setNegativeBtnText("Carte")
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("Next !")
                    .setNegativeBtnBackground("#FFA9A7A8")
                    .setGifResource(R.drawable.gif_success)   //Pass your Gif here
                    .isCancellable(true)
                    .OnPositiveClicked {
                        currentCity += 1
                        currentCity = currentCity%arrayCity.size
                        mStreetViewPanorama?.setPosition(
                                LatLng(arrayCity.get(currentCity).lat!!,arrayCity.get(currentCity).lng!!))
                    }
                    .OnNegativeClicked {
                        Toast.makeText(this.applicationContext,"OnNegativeClicked",Toast.LENGTH_LONG).show()
                    }
                    .build()
        }

    }

}
