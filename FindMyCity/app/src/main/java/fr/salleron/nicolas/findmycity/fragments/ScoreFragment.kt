package fr.salleron.nicolas.findmycity.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.images.ImageManager
import com.google.android.gms.games.Games
import com.yarolegovich.lovelydialog.LovelyTextInputDialog
import fr.salleron.nicolas.findmycity.R
import fr.salleron.nicolas.findmycity.adapter.ListAdapter
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

class ScoreFragment : ListFragment(),GoogleApiClient.ConnectionCallbacks {

    private var mListener: OnFragmentInteractionListener? = null
    private var apiClient: GoogleApiClient? = null
    private var btnShowScore : Button? = null
    private var btnShowAchievement : Button? = null
    private var btnModificationProfil : Button? = null
    private var imgUser : ImageView? = null
    private var list : ListView? = null

    @Suppress("PrivatePropertyName")
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

        btnShowScore =  v?.findViewById(R.id.btnScoreFragmentScore)
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
            @Suppress("DEPRECATION")
            LovelyTextInputDialog(activity,R.style.TintTheme)
                    .setTopColor(resources.getColor(R.color.colorPrimary))
                    .setTitle("Votre nouveau pseudo")
                    .setMessage("Quel est votre nouvel pseudo?")
                    .setIcon(R.drawable.ic_launcher_foreground)
                    .setConfirmButton(android.R.string.ok, {
                        text ->
                        if(text.isNotEmpty()){
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
        tvScore = v?.findViewById(R.id.tvScore)

        /* La list */
        try {
            val fos = activity.openFileInput("_scores.txt").fd
            val fi = FileReader(fos)
            val adapter = ListAdapter(activity, R.layout.itemlistrow, fi.readLines())
            //val adapter = ArrayAdapter<String>(activity,android.R.layout.simple_list_item_1,fi.readLines())
            listAdapter = adapter
            list?.setOnItemClickListener { _, _, i, _ ->
                Log.e(TAG,"Item : "+i)
            }
        }catch (ignored :java.io.FileNotFoundException){ }
        
        return v
    }



    private fun showAchievements() {
        startActivityForResult(
                Games.Achievements
                        .getAchievementsIntent(apiClient),
                1
        )
    }

    private fun showLeaderboard() {
        startActivityForResult(
                Games.Leaderboards.getLeaderboardIntent(apiClient,
                        getString(R.string.leaderboard_scores_des_joueurs)), 0)
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

