package com.example.nicolassalleron.taquin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast


import java.util.ArrayList

import com.example.nicolassalleron.taquin.R.layout.option

/**
 * Created by nicolassalleron.
 */

class Option : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    protected var m_Image = 1
    protected var m_Size: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(option)

        val spinner = findViewById(R.id.spinner) as Spinner
        spinner.onItemSelectedListener = this
        val categories = ArrayList<String>()
        categories.add("3 x 3")
        categories.add("4 x 4")
        categories.add("6 x 6")
        val dataAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = dataAdapter

        // récupération des images
        val tree = findViewById(R.id.imageView1) as ImageView
        tree.setOnClickListener {
            m_Image = 1
            Toast.makeText(this@Option, "Image 'tree' selectionnée", Toast.LENGTH_SHORT).show()
        }
        val bridge = findViewById(R.id.imageView2) as ImageView
        bridge.setOnClickListener {
            m_Image = 2
            Toast.makeText(this@Option, "Image 'bridge' selectionnée", Toast.LENGTH_SHORT).show()
        }
        val canyon = findViewById(R.id.imageView3) as ImageView
        canyon.setOnClickListener {
            m_Image = 3
            Toast.makeText(this@Option, "Image 'canyon' selectionnée", Toast.LENGTH_SHORT).show()
        }
        val frenchCity = findViewById(R.id.imageView4) as ImageView
        frenchCity.setOnClickListener {
            m_Image = 4
            Toast.makeText(this@Option, "Image 'frenchCity' selectionnée", Toast.LENGTH_SHORT).show()
        }
        val dog = findViewById(R.id.imageView5) as ImageView
        dog.setOnClickListener {
            m_Image = 5
            Toast.makeText(this@Option, "Image 'dog' selectionnée", Toast.LENGTH_SHORT).show()
        }
        val cat = findViewById(R.id.imageView6) as ImageView
        cat.setOnClickListener {
            m_Image = 6
            Toast.makeText(this@Option, "Image 'cat' selectionnée", Toast.LENGTH_SHORT).show()
        }

        // Bouton btnValidation
        val btnValidation = findViewById(R.id.button) as Button
        btnValidation.setOnClickListener {
            val data = Intent()
            data.putExtra("bitmap", m_Image)
            data.putExtra("size", m_Size)
            this@Option.setResult(Activity.RESULT_OK, data)
            this@Option.finish()
        }

    }

    //Selection de la taille
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = parent.selectedItemId.toInt()
        when (item) {
            0 -> m_Size = 3
            1 -> m_Size = 4
            2 -> m_Size = 6
        }
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}


