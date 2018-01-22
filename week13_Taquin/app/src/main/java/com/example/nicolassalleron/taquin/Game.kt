package com.example.nicolassalleron.taquin


import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.Chronometer
import android.widget.GridView

/**
 * Created by nicolassalleron
 */

class Game : AppCompatActivity(), AdapterView.OnItemClickListener {
    //Image par défaut
    private var m_ImageFile = 1
    //Taille par défaut 3 x 3
    private var m_size = 3
    private var adapter: ImageAdapter? = null
    private var gridview: GridView? = null
    private var chronometer: Chronometer? = null
    private var hours: Int = 0
    private var minutes: Int = 0
    private var seconds: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game)
        val res = intent
        m_ImageFile = res.getIntExtra("image", 1)
        m_size = res.getIntExtra("size", 3)


        gridview = findViewById(R.id.GridView1) as GridView
        var img = BitmapFactory.decodeResource(this.resources, R.drawable.arbre)
        when (m_ImageFile) {
            1 -> img = BitmapFactory.decodeResource(this.resources, R.drawable.arbre)
            2 -> img = BitmapFactory.decodeResource(this.resources, R.drawable.pont)
            3 -> img = BitmapFactory.decodeResource(this.resources, R.drawable.canyon)
            4 -> img = BitmapFactory.decodeResource(this.resources, R.drawable.bordeaux)
            5 -> img = BitmapFactory.decodeResource(this.resources, R.drawable.chien)
            6 -> img = BitmapFactory.decodeResource(this.resources, R.drawable.chat)
        }


        adapter = ImageAdapter(baseContext, img, m_size)
        gridview!!.adapter = adapter
        gridview!!.numColumns = m_size
        gridview!!.onItemClickListener = this

        chronometer = findViewById(R.id.chronometer) as Chronometer
        chronometer!!.start()

    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        adapter!!.put(position)
        gridview!!.adapter = adapter
        if (adapter!!.finished()) {
            chronometer!!.stop()
            setScore()
            val alert = AlertDialog.Builder(this).setTitle("Victoire ! ").setMessage("Votre score : $hours : $minutes : $seconds")
                    .setPositiveButton(android.R.string.yes) { _, _ -> }.show()
            alert.setCanceledOnTouchOutside(false)

        }

    }

    private fun setScore() {
        val timeElapsed = SystemClock.elapsedRealtime() - chronometer!!.base //For example
        hours = (timeElapsed / 3600000).toInt()
        minutes = (timeElapsed - hours * 3600000).toInt() / 60000
        seconds = (timeElapsed - (hours * 3600000).toLong() - (minutes * 60000).toLong()).toInt() / 1000
    }


}
