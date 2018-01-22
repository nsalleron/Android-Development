package com.example.nicolassalleron.taquin

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button


class MainActivity : AppCompatActivity() {

    //Image par défaut
    private var mImageFile = 1
    //Taille par défaut 3 x 3
    private var size = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val option = findViewById(R.id.menu_options) as Button
        option.setOnClickListener {

            val intent = Intent(this@MainActivity, Option::class.java)
            startActivityForResult(intent, OPTIONS)
        }
        val play = findViewById(R.id.menu_play) as Button
        play.setOnClickListener {
            val startGame = Intent(this@MainActivity, Game::class.java)
            startGame.putExtra("size", size)
            startGame.putExtra("image", mImageFile)
            startActivity(startGame)
        }


    }

    override fun onActivityResult(request_code: Int, result_code: Int, data: Intent) {
        super.onActivityResult(request_code, result_code, data)

        when (request_code) {
            OPTIONS -> if (result_code == Activity.RESULT_OK) {
                mImageFile = data.getIntExtra("bitmap", 0)
                size = data.getIntExtra("size", 0)
            }
        }
    }

    companion object {

        private val OPTIONS = 1
    }

}
