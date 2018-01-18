package fr.salleron.nicolas.findmycity.fragments

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.api.GoogleApiClient

import fr.salleron.nicolas.findmycity.R
import com.google.android.gms.games.Games
import android.provider.MediaStore
import com.google.android.gms.common.images.ImageManager
import com.squareup.picasso.Picasso
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.tasks.OnCompleteListener
import com.squareup.picasso.Transformation
import com.yarolegovich.lovelydialog.LovelyTextInputDialog
import fr.salleron.nicolas.findmycity.data.ListAdapter
import java.io.*
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ScoreFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ScoreFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScoreFragment : ListFragment(),GoogleApiClient.ConnectionCallbacks {


    private var mListener: OnFragmentInteractionListener? = null
    var apiClient: GoogleApiClient? = null
    var btnShowScore : Button? = null
    var btnShowAchievement : Button? = null
    var btnModificationProfil : Button? = null
    var imgUser : ImageView? = null
    var list : ListView? = null

    private val TAG = "ScoreFragment"

    private var tvScore: TextView? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater?.inflate(R.layout.fragment_score, container, false)
        /* API Client Game */
        apiClient = GoogleApiClient.Builder(activity)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .addConnectionCallbacks(this)
                .enableAutoManage(activity) {
                    Log.e(TAG, "Could not connect to Play games services")
                    activity.finish()
                }.build()



        btnShowScore =  v?.findViewById<Button>(R.id.btnScoreFragmentScore)
        btnShowScore?.setOnClickListener {
            if(apiClient!!.isConnected){
                showLeaderboard()
            }else{
                Toast.makeText(activity,"Je n'arrive pas à récupérer vos résultats...",Toast.LENGTH_SHORT).show()
            }

        }

        btnShowAchievement = v?.findViewById(R.id.btnAchievementFragmentScore)
        btnShowAchievement?.setOnClickListener {
            if(apiClient!!.isConnected){
                showAchievements()
             }else{
               Toast.makeText(activity,"Je n'arrive pas à récupérer vos résultats...",Toast.LENGTH_SHORT).show()
            }
        }

        btnModificationProfil = v?.findViewById(R.id.btnModificationProfileFragmentScore)
        btnModificationProfil?.setOnClickListener {
            LovelyTextInputDialog(activity,R.style.TintTheme)
                    .setTopColor(resources.getColor(R.color.colorPrimary))
                    .setTitle("Votre nouveau pseudo")
                    .setMessage("Quel est votre nouvel pseudo?")
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setConfirmButton(android.R.string.ok, LovelyTextInputDialog.OnTextInputConfirmListener {
                        text ->
                        if(text.length != 0){

                            val dir = activity.filesDir
                            File(dir,"_options.txt").delete()
                            val fos = activity.openFileOutput("_options.txt",Context.MODE_APPEND)
                            val pw = PrintWriter(fos)
                            pw.println(text)
                            tvScore?.text = text
                            pw.close()
                        }

                    }).show()

        }


        imgUser = v?.findViewById(R.id.profile)
        tvScore = v?.findViewById<TextView>(R.id.tvScore)

        /* La list */
        try {
            val fos = activity.openFileInput("_scores.txt").fd
            val fi = FileReader(fos)
            val adapter = ListAdapter(activity, R.layout.itemlistrow, fi.readLines())
            //val adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,fi.readLines())
            listAdapter = adapter
            list?.setOnItemClickListener { adapterView, _, i, l ->
                Log.e(TAG,"Item : "+i)
            }
        }catch (ignored :java.io.FileNotFoundException){ }
        
        return v
    }


    fun showAchievements() {
        startActivityForResult(
                Games.Achievements
                        .getAchievementsIntent(apiClient),
                1
        )
    }

    fun showLeaderboard() {
        startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_classement_test_1)), 0)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnected(p0: Bundle?) {
        val fos = activity.openFileInput("_options.txt")
        val fi = FileReader(fos.fd)
        try {
            tvScore?.text = fi.readLines()[0]
        }catch (e : Exception){
            tvScore?.text = Games.Players.getCurrentPlayer(apiClient).displayName
        }

        ImageManager.create(activity).
                loadImage(imgUser,Games.Players.getCurrentPlayer(apiClient).iconImageUri)

        YoYo.with(Techniques.ZoomIn)
                .duration(700)
                .playOn(imgUser)
        YoYo.with(Techniques.FadeIn)
                .duration(700)
                .playOn(tvScore)

    }

}//Required empty public constructor

