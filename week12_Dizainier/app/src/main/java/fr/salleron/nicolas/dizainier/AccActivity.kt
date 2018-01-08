package fr.salleron.nicolas.findmycity

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*

class AccActivity : AppCompatActivity(), View.OnClickListener,SeekBar.OnSeekBarChangeListener{



    val TAG = "AccActivity"

    val unitBtn = arrayOf(R.id.uni_0,R.id.uni_1,R.id.uni_2,R.id.uni_3,R.id.uni_4,R.id.uni_5,R.id.uni_6,R.id.uni_7,R.id.uni_8,R.id.uni_9)
    val dizBtn = arrayOf(R.id.diz_0,R.id.diz_1,R.id.diz_2,R.id.diz_3,R.id.diz_4,R.id.diz_5,R.id.diz_6,R.id.diz_7,R.id.diz_8,R.id.diz_9)

    val unitBtnInst = arrayOfNulls<Button>(10)
    val dizBtnInst = arrayOfNulls<Button>(10)

    var currentU = 0
    var currentD = 0

    val seek = findViewById<SeekBar>(R.id.seek)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)

        seek.max = 99
        //seek.min = 0
        seek.setOnSeekBarChangeListener(this)

        for ((value,rID) in unitBtn.withIndex()){
            val tmp = findViewById<Button>(rID)
            tmp.setOnClickListener(this)
            unitBtnInst.set(value,tmp)
        }
        for ((value,rID) in dizBtn.withIndex()){
            val tmp = findViewById<Button>(rID)
            tmp.setOnClickListener(this)
            dizBtnInst.set(value,tmp)
        }

        reset()
    }

    fun reset(){
        for (btn in dizBtnInst ){
            btn?.setBackgroundColor(Color.WHITE)
        }
        for (btn in unitBtnInst ){
           btn?.setBackgroundColor(Color.WHITE)
        }
    }


    override fun onClick(p0: View?) {
        Log.e(TAG,p0.toString())
        val btn = p0 as Button
        var txtValue = btn.text as String
        var txtUorD = btn.contentDescription as String
        var result = 0

        if(txtUorD.equals("u")) {
            currentU = Integer.parseInt(txtValue)
        }else{
            currentD = Integer.parseInt(txtValue)
        }

        result = currentD*10 + currentU

        if(result == 42){
            findViewById<TextView>(R.id.tv_result).highlightColor = Color.RED
        }else{
            findViewById<TextView>(R.id.tv_result).highlightColor = Color.BLACK
        }

        reset()

        unitBtnInst[currentU -1 ]?.setBackgroundColor(Color.BLUE)
        dizBtnInst[currentD -1]?.setBackgroundColor(Color.BLUE)

        //MAJ affiche
        seek.progress = result
        var t = findViewById<TextView>(R.id.tv_result)
        t.text = ""+result

    }
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        val unite = p1 % 10 - 1
        val dizaine = p1 / 10 % 10 -1

        reset()
        unitBtnInst[unite]?.setBackgroundColor(Color.BLUE)
        dizBtnInst[dizaine]?.setBackgroundColor(Color.BLUE)


    }
    override fun onStartTrackingTouch(p0: SeekBar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStopTrackingTouch(p0: SeekBar?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
