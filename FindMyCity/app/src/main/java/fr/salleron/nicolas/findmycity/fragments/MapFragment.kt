package fr.salleron.nicolas.findmycity.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import fr.salleron.nicolas.findmycity.R
import fr.salleron.nicolas.findmycity.activities.GameFragmentActivity

class MapFragment : Fragment() {

    var streetFragmentInstance: StreetFragment? = null
    var gameFragmentActivity: GameFragmentActivity? = null
    private var mListener: OnFragmentInteractionListener? = null
    var mMap: GoogleMap? = null
    private var mMapView: MapView? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater?.inflate(R.layout.fragment_map, container, false)

        val mMapView = v!!.findViewById<MapView>(R.id.map)
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume()
        MapsInitializer.initialize(activity.applicationContext)

        mMapView.getMapAsync { map ->
            map.setOnMapClickListener(gameFragmentActivity)
            map.resetMinMaxZoomPreference()
            mMap = map
            mMap?.isBuildingsEnabled = false
            mMap?.isIndoorEnabled = false
            mMap?.isTrafficEnabled = false
            mMap?.uiSettings?.isCompassEnabled = false
            mMap?.uiSettings?.isMyLocationButtonEnabled = false
            mMap?.uiSettings?.isIndoorLevelPickerEnabled = false
            mMap?.uiSettings?.isMapToolbarEnabled = false
            Log.e("MapFragment","getMapAsync")
        }

        this.mMapView = mMapView
        Log.e("MapFragment","return v")
        return v
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView?.onLowMemory()
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


}// Required empty public constructor