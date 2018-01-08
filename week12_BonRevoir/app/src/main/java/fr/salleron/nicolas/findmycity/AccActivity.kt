package fr.salleron.nicolas.findmycity

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

class AccActivity : AppCompatActivity(), View.OnClickListener{

    val TAG = "AccActivity"
    var change : Boolean = false
    lateinit var btn0 : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)
        btn0 = findViewById<Button>(R.id.lvl0)
        var btn1 = findViewById<Button>(R.id.lvl1)
        var btn2 = findViewById<Button>(R.id.lvl2)
        var btnScore = findViewById<Button>(R.id.score)

        btn0.setOnClickListener(this)
        btn1.setOnClickListener(this)
        btn2.setOnClickListener(this)
        btnScore.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        Log.e(TAG,p0.toString())
        val btn = p0 as Button
        var txt = btn.text as String
        if (change)
            btn.text = txt.plus(" = hellooo ! changeVal : ".plus(change))
        else
            btn.text = txt.plus(" = hellooo ! changeVal : ".plus(change))
        change = !change


    }
}
