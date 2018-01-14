package fr.salleron.nicolas.findmycity.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import fr.salleron.nicolas.findmycity.data.Difficulty
import fr.salleron.nicolas.findmycity.R

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var Btn0: Button? = null
    var Btn1: Button? = null
    var Btn2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Btn0 = findViewById(R.id.lvl0)
        Btn1 = findViewById(R.id.lvl1)
        Btn2 = findViewById(R.id.lvl2)

        Btn0?.setOnClickListener(this)
        Btn1?.setOnClickListener(this)
        Btn2?.setOnClickListener(this)
    }



    override fun onClick(p0: View?) {
        if (Btn0 === p0)
            this.launchGameActivityWithDifficulty(0)
        if (Btn1 === p0)
            this.launchGameActivityWithDifficulty(1)
        if (Btn2 === p0)
            this.launchGameActivityWithDifficulty(2)
    }

    fun launchGameActivityWithDifficulty(d: Int){
        //do stuff with difficulty
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("DIFFICULTY",d)
        }
        startActivity(intent)
    }
}
