package fr.salleron.nicolas.findmycity.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import fr.salleron.nicolas.findmycity.R
import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.games.Games
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    @Suppress("PrivatePropertyName")
    private val TAG = "MainActivity"
    private var btn0: Button? = null
    private var btn1: Button? = null
    private var btn2: Button? = null
    private var options : Button? = null
    private var apiClient:GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                    Log.e(TAG, "Could not connect to Play games services")
                    finish()
                }.build()
    }




    override fun onClick(p0: View?) {
        if (btn0 === p0)
            this.launchGameActivityWithDifficulty(0)
        if (btn1 === p0)
            this.launchGameActivityWithDifficulty(1)
        if (btn2 === p0)
            this.launchGameActivityWithDifficulty(2)
        if (options === p0){

            val intent = Intent(this,  ScoreAboutActivity::class.java)
            startActivity(intent)
            Log.e("LAUNCH","OK")
        }
    }

    private fun launchGameActivityWithDifficulty(d: Int){
        //do stuff with difficulty
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("DIFFICULTY",d)
        }
        startActivity(intent)
    }
}
