package fr.salleron.nicolas.dizainier

import android.annotation.SuppressLint
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*


class AccActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener{



    val TAG = "AccActivity"

    val unitBtn = arrayOf(R.id.uni_0,R.id.uni_1,R.id.uni_2,R.id.uni_3,R.id.uni_4,R.id.uni_5,R.id.uni_6,R.id.uni_7,R.id.uni_8,R.id.uni_9)
    val dizBtn = arrayOf(R.id.diz_0,R.id.diz_1,R.id.diz_2,R.id.diz_3,R.id.diz_4,R.id.diz_5,R.id.diz_6,R.id.diz_7,R.id.diz_8,R.id.diz_9)

    val unitBtnInst = arrayOfNulls<Button>(10)
    val dizBtnInst = arrayOfNulls<Button>(10)

    var currentU = 0
    var currentD = 0

    lateinit var seek : SeekBar
    lateinit var swtch : Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acc)

        seek = findViewById(R.id.seek)
        seek.max = 99
        seek.setOnSeekBarChangeListener(this)

        swtch = findViewById(R.id.switch1)
        swtch.isChecked = false
        swtch.setOnClickListener(this)

        findViewById<Button>(R.id.plus).setOnClickListener(this)
        findViewById<Button>(R.id.moins).setOnClickListener(this)
        findViewById<Button>(R.id.raz).setOnClickListener(this)
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
            btn?.setTextColor(Color.BLACK)
        }
        for (btn in unitBtnInst ){
            btn?.setBackgroundColor(Color.WHITE)
            btn?.setTextColor(Color.BLACK)
        }
    }


    override fun onClick(p0: View?) {
        if(p0 is Switch){
            var t = findViewById<TextView>(R.id.result)

            if(swtch.isChecked){
                t.text = Integer.toHexString(currentD*10+currentU)
            }else{
                t.text = Integer.toString(currentD*10+currentU)
            }
            return
        }
        if(p0 is Button){
            if(p0.text == "+"){
                currentU += 1
                if(currentU > 9){
                    currentD += 1
                    currentU = 0
                }
                if(currentD > 9)
                    currentD = 9

            }else if (p0.text == "-"){
                currentU -= 1
                if(currentU < 0){
                    currentU = 9
                    currentD -= 1
                }
                if (currentD < 0){
                    currentD =0
                }

            }else if (p0.text == "RAZ"){
                currentD = 0
                currentU = 0
            }
        }

        val btn = p0 as Button
        var txtValue = btn.text as String
        var txtUorD = btn.contentDescription as String?
        var result = 0

        if(txtUorD.equals("u")) {
            currentU = Integer.parseInt(txtValue)
        }else{
            if ( txtValue.equals("+") || txtValue.equals("-") || txtValue.equals("RAZ") ){ }else{
                currentD = Integer.parseInt(txtValue)
            }

        }

        result = currentD*10 + currentU

        if(result == 42){
            findViewById<TextView>(R.id.result).setTextColor(Color.RED)
        }else{
            findViewById<TextView>(R.id.result).setTextColor(Color.BLACK)
        }

        reset()

        unitBtnInst[currentU]?.setBackgroundColor(Color.BLUE)
        dizBtnInst[currentD]?.setBackgroundColor(Color.BLUE)

        //MAJ affiche
        seek.progress = result
        var t = findViewById<TextView>(R.id.result)
        if(swtch.isChecked){
            t.text = Integer.toHexString(currentD*10+currentU)
        }else{
            t.text = "$result"
        }

    }
    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        val unite = p1 % 10
        val dizaine = p1 / 10 % 10

        currentU = unite
        currentD = dizaine
        reset()
        unitBtnInst[unite]?.setBackgroundColor(Color.BLUE)
        dizBtnInst[dizaine]?.setBackgroundColor(Color.BLUE)
        unitBtnInst[unite]?.setTextColor(Color.WHITE)
        dizBtnInst[dizaine]?.setTextColor(Color.WHITE)



        val t = findViewById<TextView>(R.id.result)
        if(swtch.isChecked){
            t.text = Integer.toHexString(currentD*10+currentU)
        }else{
            t.text = "$dizaine$unite"
        }
    }
    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }
}
