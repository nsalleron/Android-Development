package com.example.nicolassalleron.dessin

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout

import java.util.UUID

import android.provider.MediaStore
import android.app.AlertDialog
import android.app.Dialog
import android.view.View.OnClickListener
import android.widget.Toast
import com.example.nicolassalleron.dessin.R

@SuppressLint("Registered")
class MainActivity : AppCompatActivity(), OnClickListener {

    private var drawView: DrawingView? = null
    private var currPaint: ImageButton? = null
    private val brushSmall = 10.toFloat()
    private val brushMedium = 20.toFloat()
    private val brushLarge = 30.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        drawView = findViewById(R.id.drawing) as DrawingView

        val paintLayout = findViewById(R.id.paint_colors) as LinearLayout
        currPaint = paintLayout.getChildAt(5) as ImageButton
        @Suppress("DEPRECATION")
        currPaint!!.setImageDrawable(resources.getDrawable(R.drawable.paint_pressed))


        drawView!!.setBrushSize(brushMedium)
        findViewById(R.id.draw_btn).setOnClickListener(this)
        findViewById(R.id.erase_btn).setOnClickListener(this)
        findViewById(R.id.new_btn).setOnClickListener(this)
        findViewById(R.id.save_btn).setOnClickListener(this)
    }

    @Suppress("DEPRECATION")
    fun paintClicked(view: View) {
        drawView!!.setErase(false)
        drawView!!.setBrushSize(drawView!!.lastBrushSize)
        //use chosen color
        if (view !== currPaint) {
            //update color
            val imgView = view as ImageButton
            val color = view.getTag().toString()
            drawView!!.setColor(color)

            imgView.setImageDrawable(resources.getDrawable(R.drawable.paint_pressed))
            currPaint!!.setImageDrawable(resources.getDrawable(R.drawable.paint))
            currPaint = view
        }
    }

    override fun onClick(view: View) {

        if (view.id == R.id.draw_btn) {

            val brushDialog = Dialog(this)
            brushDialog.setTitle("Taille du crayon")
            brushDialog.setContentView(R.layout.brush_chooser)

            val smallBtn = brushDialog.findViewById(R.id.small_brush) as ImageButton
            smallBtn.setOnClickListener {
                drawView!!.setBrushSize(brushSmall)
                drawView!!.lastBrushSize = brushSmall
                drawView!!.setErase(false)
                brushDialog.dismiss()
            }

            val mediumBtn = brushDialog.findViewById(R.id.medium_brush) as ImageButton
            mediumBtn.setOnClickListener {
                drawView!!.setBrushSize(brushMedium)
                drawView!!.lastBrushSize = brushMedium
                drawView!!.setErase(false)
                brushDialog.dismiss()
            }

            val largeBtn = brushDialog.findViewById(R.id.large_brush) as ImageButton
            largeBtn.setOnClickListener {
                drawView!!.setBrushSize(brushLarge)
                drawView!!.lastBrushSize = brushLarge
                drawView!!.setErase(false)
                brushDialog.dismiss()
            }

            brushDialog.show()
        } else if (view.id == R.id.erase_btn) {

            val brushDialog = Dialog(this)
            brushDialog.setTitle("Taille de la gomme ")
            brushDialog.setContentView(R.layout.brush_chooser)

            val smallBtn = brushDialog.findViewById(R.id.small_brush) as ImageButton
            smallBtn.setOnClickListener {
                drawView!!.setErase(true)
                drawView!!.setBrushSize(brushSmall)
                brushDialog.dismiss()
            }
            val mediumBtn = brushDialog.findViewById(R.id.medium_brush) as ImageButton
            mediumBtn.setOnClickListener {
                drawView!!.setErase(true)
                drawView!!.setBrushSize(brushMedium)
                brushDialog.dismiss()
            }
            val largeBtn = brushDialog.findViewById(R.id.large_brush) as ImageButton
            largeBtn.setOnClickListener {
                drawView!!.setErase(true)
                drawView!!.setBrushSize(brushLarge)
                brushDialog.dismiss()
            }
            brushDialog.show()
        } else if (view.id == R.id.new_btn) {

            AlertDialog.Builder(this)
                    .setTitle("Nouveau dessin !")
                    .setMessage("Le nouveau dessin supprimera l'ancien !")
                    .setNegativeButton("Annuler") { dialog, _ -> dialog.cancel() }
                    .setPositiveButton("D'accord !") { dialog, _ ->
                        drawView!!.startNew()
                        dialog.dismiss()
                    }
                    .show()
        } else if (view.id == R.id.save_btn) {

            AlertDialog.Builder(this)
                    .setTitle("Sauvegarder dessin")
                    .setMessage("Le sauvegarder vers la galerie")
                    .setPositiveButton("Oui") { _, _ ->
                        //save drawing
                        drawView!!.isDrawingCacheEnabled = true
                        val imgSaved = MediaStore.Images.Media.insertImage(
                                contentResolver, drawView!!.drawingCache,
                                UUID.randomUUID().toString() + ".png", "drawing")
                        if (imgSaved != null) {
                            val savedToast = Toast.makeText(applicationContext,
                                    "Dessins sauvegardés dans la galerie !", Toast.LENGTH_SHORT)
                            savedToast.show()
                        } else {
                            val unsavedToast = Toast.makeText(applicationContext,
                                    "Problème, l'image ne peut être sauvegarder", Toast.LENGTH_SHORT)
                            unsavedToast.show()
                        }
                        drawView!!.destroyDrawingCache()
                    }
                    .setNegativeButton("Annuler") { dialog, _ -> dialog.cancel() }
                    .show()
        }
    }
}
