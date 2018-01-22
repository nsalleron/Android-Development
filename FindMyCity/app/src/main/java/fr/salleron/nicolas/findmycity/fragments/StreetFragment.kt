package fr.salleron.nicolas.findmycity.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import fr.salleron.nicolas.findmycity.R
import fr.salleron.nicolas.findmycity.activities.GameFragmentActivity

/**
 * Permet l'instiation de l'attribut [mStreetViewPanorama] et de la view [mStreetViewPanoramaView]
 * Le handler des touches sur la [mStreetViewPanorama] est [gameFragmentActivity]
 */
class StreetFragment : Fragment() {

    var mapFragmentInstance: MapFragment? = null
    var gameFragmentActivity: GameFragmentActivity? = null
    var mStreetViewPanorama: StreetViewPanorama? = null
    private var mStreetViewPanoramaView : StreetViewPanoramaView? = null
    private var mListener: OnFragmentInteractionListener? = null
    private var currentCity = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater?.inflate(R.layout.fragment_street, container, false)


        val streetViewPanoramaView = v!!.findViewById<StreetViewPanoramaView>(R.id.streetviewpanorama)
        streetViewPanoramaView.onCreate(savedInstanceState)
        streetViewPanoramaView.onResume()


        streetViewPanoramaView?.getStreetViewPanoramaAsync(({
            panorama -> panorama.setPosition(
                LatLng(gameFragmentActivity!!.arrayCity[currentCity].lat!!,
                        gameFragmentActivity!!.arrayCity[currentCity].lng!!))
            mStreetViewPanorama = panorama
            mStreetViewPanorama?.setOnStreetViewPanoramaChangeListener(gameFragmentActivity)
            if (savedInstanceState == null) {
                mStreetViewPanorama?.setPosition(
                        LatLng(gameFragmentActivity!!.arrayCity[currentCity].lat!!, gameFragmentActivity!!.arrayCity[currentCity].lng!!))
            }
            mStreetViewPanorama?.isStreetNamesEnabled = false
            mStreetViewPanorama?.isUserNavigationEnabled = true
            mStreetViewPanorama?.isPanningGesturesEnabled = true
            mStreetViewPanorama?.isZoomGesturesEnabled = true
            Log.e("StreetFragment","getStreetViewPanoramaAsync")
        }))

        mStreetViewPanoramaView = streetViewPanoramaView

        val imageButton = v.findViewById<ImageButton>(R.id.imgBtn)
        imageButton.setOnClickListener {
            gameFragmentActivity?.goToViewTwo()
        }

        Log.e("StreetFragment","return v")
        return v
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }


    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onResume() {
        super.onResume()
        mStreetViewPanoramaView?.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mStreetViewPanoramaView?.onLowMemory()
    }



}// Required empty public constructor