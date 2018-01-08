package fr.salleron.nicolas.bonrevoir

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class AccActivity : AppCompatActivity(), View.OnClickListener{

    val TAG = "AccActivity"
    var change : Boolean = false
    lateinit var btn0 : Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)
        btn0 = findViewById<Button>(R.id.lvl0)
        btn0.setOnClickListener(this)

    }


    override fun onClick(p0: View?) {
        Log.e(TAG,p0.toString())
        if (change)
            findViewById<TextView>(R.id.textView).setText("Bonjour !")
        else
            findViewById<TextView>(R.id.textView).setText("Au revoir !")
        change = !change
    }
}
