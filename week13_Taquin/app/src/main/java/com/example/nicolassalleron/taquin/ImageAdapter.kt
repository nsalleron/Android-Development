package com.example.nicolassalleron.taquin

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView

import java.util.ArrayList

/**
 * Created by nicolassalleron.
 */

class ImageAdapter(private val mContext: Context, img: Bitmap, private val m_size: Int) : BaseAdapter() {
    private val tab: Array<Bitmap?> = arrayOfNulls<Bitmap>(m_size*m_size)
    private val rannul: Int
    private val listeInts = ArrayList<Int>()
    private val finish = ArrayList<Int>()

    init {
        for (i in 0 .. m_size * m_size) {
            this.listeInts.add(i)
            this.finish.add(i)
        }
        cutBitmap(img)

        rannul = m_size * m_size - 1 // correspond à la position de la case blanche dans listeInts
        Log.e("rrr", listeInts.toString())
        randomizeArray(tab)
        Log.e("rrr", listeInts.toString())
    }

    fun cutBitmap(image: Bitmap) {
        val taille = (image.width / (m_size*2))
        Log.e("ImageAdapter",""+taille)
        var b = 0
        for (y in 0 .. (m_size - 1)) {
            Log.e("ImageAdapter","y: "+ y)
            for (x in 0 .. (m_size-1)) {
                Log.e("ImageAdapter","x: "+ x)
                tab[b] = Bitmap.createBitmap(image, x * taille, y * taille, taille, taille)
                b++
            }

        }
        tab[tab.size - 1] = Bitmap.createBitmap(taille, taille, Bitmap.Config.ALPHA_8)


    }

    private fun randomizeArray(tab: Array<Bitmap?>) {
        for (i in 0 .. 500 * m_size) {
            val n = (Math.random() * tab.size).toInt()
            put(n)
        }
    }

    override fun getCount(): Int {
        return tab.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = ImageView(mContext)
            when (m_size) {

                3 -> imageView.layoutParams = AbsListView.LayoutParams(350, 350)
                4 -> imageView.layoutParams = AbsListView.LayoutParams(260, 260)
                6 -> imageView.layoutParams = AbsListView.LayoutParams(170, 170)
            }

            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(1, 1, 1, 1)
        } else {
            imageView = (convertView as ImageView?)!!
        }

        imageView.setImageBitmap(tab[position])
        return imageView
    }

    fun put(position: Int) {
        //tester position possible !
        if (position % m_size == 0) {
            if (position - m_size >= 0 && position + m_size <= listeInts.size - 1) {
                //deplacement possible droite, bas, haut
                when (rannul) {
                    listeInts[position + 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position + 1]
                        tab[position + 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + 1]
                        listeInts[position + 1] = x
                    }
                    listeInts[position + m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position + m_size]
                        tab[position + m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + m_size]
                        listeInts[position + m_size] = x
                    }
                    listeInts[position - m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position - m_size]
                        tab[position - m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - m_size]
                        listeInts[position - m_size] = x
                    }
                }
            } else if (position - m_size <= 0) {
                if (listeInts[position + 1] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position + 1]
                    tab[position + 1] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position + 1]
                    listeInts[position + 1] = x
                } else if (listeInts[position + m_size] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position + m_size]
                    tab[position + m_size] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position + m_size]
                    listeInts[position + m_size] = x
                }
            } else if (position + m_size >= listeInts.size - 1) {

                if (listeInts[position + 1] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position + 1]
                    tab[position + 1] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position + 1]
                    listeInts[position + 1] = x
                } else if (listeInts[position - m_size] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position - m_size]
                    tab[position - m_size] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position - m_size]
                    listeInts[position - m_size] = x
                }

            }
        } else if (position % m_size == m_size - 1) {
            if (position - m_size >= 0 && position + m_size <= listeInts.size - 1) {
                //deplacement possible droite, bas, haut
                when (rannul) {
                    listeInts[position - 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position - 1]
                        tab[position - 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - 1]
                        listeInts[position - 1] = x
                    }
                    listeInts[position + m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position + m_size]
                        tab[position + m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + m_size]
                        listeInts[position + m_size] = x
                    }
                    listeInts[position - m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position - m_size]
                        tab[position - m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - m_size]
                        listeInts[position - m_size] = x
                    }
                }

            }
            if (position - m_size <= 0) {
                if (listeInts[position - 1] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position - 1]
                    tab[position - 1] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position - 1]
                    listeInts[position - 1] = x
                } else if (listeInts[position + m_size] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position + m_size]
                    tab[position + m_size] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position + m_size]
                    listeInts[position + m_size] = x
                }

            } else if (position + m_size >= listeInts.size - 1) {
                if (listeInts[position - 1] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position - 1]
                    tab[position - 1] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position - 1]
                    listeInts[position - 1] = x
                } else if (listeInts[position - m_size] == rannul) {
                    val img = tab[position]
                    tab[position] = tab[position - m_size]
                    tab[position - m_size] = img
                    val x = listeInts[position]
                    listeInts[position] = listeInts[position - m_size]
                    listeInts[position - m_size] = x
                }
            }
        } else {
            if (position - m_size >= 0 && position + m_size <= listeInts.size - 1) {
                //deplacement possible droite, bas, haut ,gauche
                when (rannul) {
                    listeInts[position - 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position - 1]
                        tab[position - 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - 1]
                        listeInts[position - 1] = x
                    }
                    listeInts[position + m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position + m_size]
                        tab[position + m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + m_size]
                        listeInts[position + m_size] = x
                    }
                    listeInts[position - m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position - m_size]
                        tab[position - m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - m_size]
                        listeInts[position - m_size] = x
                    }
                    listeInts[position + 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position + 1]
                        tab[position + 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + 1]
                        listeInts[position + 1] = x
                    }
                }

            } else if (position - m_size < 0) {
                when (rannul) {
                    listeInts[position - 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position - 1]
                        tab[position - 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - 1]
                        listeInts[position - 1] = x
                    }
                    listeInts[position + m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position + m_size]
                        tab[position + m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + m_size]
                        listeInts[position + m_size] = x
                    }
                    listeInts[position + 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position + 1]
                        tab[position + 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + 1]
                        listeInts[position + 1] = x
                    }
                }
            } else if (position + m_size > listeInts.size - 1) {
                when (rannul) {
                    listeInts[position - 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position - 1]
                        tab[position - 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - 1]
                        listeInts[position - 1] = x
                    }
                    listeInts[position - m_size] -> {
                        val img = tab[position]
                        tab[position] = tab[position - m_size]
                        tab[position - m_size] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position - m_size]
                        listeInts[position - m_size] = x
                    }
                    listeInts[position + 1] -> {
                        val img = tab[position]
                        tab[position] = tab[position + 1]
                        tab[position + 1] = img
                        val x = listeInts[position]
                        listeInts[position] = listeInts[position + 1]
                        listeInts[position + 1] = x
                    }
                }
            }
        }


    }

    fun finished(): Boolean {
        for (i in 0 until finish.size) {
            @Suppress("DEPRECATED_IDENTITY_EQUALS")
            if (finish[i] !== listeInts[i]) {
                return false
            }
        }
        return true
    }
}


