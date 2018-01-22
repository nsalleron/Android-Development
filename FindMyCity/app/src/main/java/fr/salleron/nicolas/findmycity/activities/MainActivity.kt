package fr.salleron.nicolas.findmycity.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import com.shashank.sony.fancygifdialoglib.FancyGifDialog
import com.yarolegovich.lovelydialog.LovelyStandardDialog
import fr.salleron.nicolas.findmycity.R


class MainActivity : AppCompatActivity(), View.OnClickListener {

    @Suppress("PrivatePropertyName")
    private val TAG = "MainActivity"
    private var btn0: Button? = null
    private var btn1: Button? = null
    private var btn2: Button? = null
    private var options : Button? = null
    private var apiClient:GoogleApiClient? = null
    private var account = false
    private var internet = false
    private var networkstate = false
    private var allGood = false

    private var perms = arrayOf(Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE)
    private val permsRequestCode = 200


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, permsRequestCode)
        }else{
            account = true
            internet = true
            networkstate = true
            allGood = true
        }

        btn0 = findViewById(R.id.lvl0)
        btn1 = findViewById(R.id.lvl1)
        btn2 = findViewById(R.id.lvl2)
        options = findViewById(R.id.options)

        btn0?.setOnClickListener(this)
        btn1?.setOnClickListener(this)
        btn2?.setOnClickListener(this)
        options?.setOnClickListener(this)

        apiClient = GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this) {
                    LovelyStandardDialog(this@MainActivity)
                            .setTopColor(resources.getColor(R.color.colorPrimary,theme))
                            .setButtonsColorRes(R.color.md_deep_orange_500)
                            .setIcon(R.drawable.ic_launcher_foreground)
                            .setTitle("Il vous faut internet !")
                            .setMessage("Malheureusement, l'application ne peut fonctionner sans.")
                            .setPositiveButton(android.R.string.ok, {
                                finish()
                            })
                            .show()
                }.build()

        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(findViewById(R.id.imageView))
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(btn0)
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(btn1)
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(btn2)
        YoYo.with(Techniques.FadeIn)
                .duration(1000)
                .playOn(options)

        val fos = openFileOutput("_options.txt", Context.MODE_APPEND)
        fos.close()
    }




    override fun onClick(p0: View?) {
        if (btn0 === p0)
            this.launchGameActivityWithDifficulty(0,getString(R.string.modeNormal))
        if (btn1 === p0)
            this.launchGameActivityWithDifficulty(0,getString(R.string.modeGeocoding))
        if (btn2 === p0)
            this.launchGameActivityWithDifficulty(0,getString(R.string.modeChrono))
        if (options === p0){
            val intent = Intent(this,  ScoreAboutFragmentActivity::class.java)
            if(allGood){
                startActivity(intent)
            }else{
                checkIfPermsGranted()
            }
        }
    }

    private fun checkIfPermsGranted(){
        if(!internet || !account){
            FancyGifDialog.Builder(this)
                    .setPositiveBtnBackground("#FF4081")
                    .setPositiveBtnText("kill myself ?")
                    .setNegativeBtnText("kill myself ?")
                    .isCancellable(false)
                    .setTitle("Il manque des permissions")
                    .setGifResource(R.drawable.gif_16000)   //Pass your Gif here
                    .OnPositiveClicked { finish() }
                    .OnNegativeClicked { finish() }
                    .build()
        }
    }


    private fun launchGameActivityWithDifficulty(d: Int, mode : String){
        val intent = Intent(this,GameFragmentActivity::class.java).apply {
            putExtra("DIFFICULTY",d)
            putExtra("MODE",mode)
        }
        if(allGood){
            startActivity(intent)
        }else{
            checkIfPermsGranted()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permsRequestCode == requestCode) {
            account = grantResults[0] == PackageManager.PERMISSION_GRANTED
            internet = grantResults[1] == PackageManager.PERMISSION_GRANTED
            networkstate = grantResults[2] == PackageManager.PERMISSION_GRANTED
        }
        allGood = account == true && internet == true && networkstate == true

    }
}
