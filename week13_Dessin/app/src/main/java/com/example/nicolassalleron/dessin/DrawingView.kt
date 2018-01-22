package com.example.nicolassalleron.dessin

import android.annotation.SuppressLint
import android.view.View
import android.content.Context
import android.util.AttributeSet
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.graphics.Color
import android.util.TypedValue
import com.example.nicolassalleron.dessin.R

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {


    private var drawPath: Path? = null
    private var drawPaint: Paint? = null
    private var canvasPaint: Paint? = null
    private var paintColor = -0x1000000
    private var drawCanvas: Canvas? = null
    private var canvasBitmap: Bitmap? = null

    private var brushSize: Float = 0.toFloat()
    var lastBrushSize: Float = 0.toFloat()

    init {
        setupDrawing()
    }

    private fun setupDrawing() {
        brushSize = resources.getInteger(R.integer.medium_size).toFloat()
        lastBrushSize = brushSize

        drawPath = Path()
        drawPaint = Paint()
        drawPaint!!.color = paintColor
        drawPaint!!.isAntiAlias = true
        drawPaint!!.strokeWidth = brushSize
        drawPaint!!.style = Paint.Style.STROKE
        drawPaint!!.strokeJoin = Paint.Join.ROUND
        drawPaint!!.strokeCap = Paint.Cap.ROUND
        canvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        drawCanvas = Canvas(canvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(canvasBitmap!!, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath!!, drawPaint!!)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //detect user touch
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath!!.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> drawPath!!.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawCanvas!!.drawPath(drawPath!!, drawPaint!!)
                drawPath!!.reset()
            }
            else -> return false
        }
        this.invalidate()
        return true
    }

    fun setColor(newColor: String) {
        //set color
        this.invalidate()
        paintColor = Color.parseColor(newColor)
        drawPaint!!.color = paintColor
    }

    fun setBrushSize(newSize: Float) {
        //update size
        brushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, resources.displayMetrics)
        drawPaint!!.strokeWidth = brushSize
    }

    fun setErase(isErase: Boolean) {
        //set erase true or false
        if (isErase)
            drawPaint!!.color = -0x1
        else
            drawPaint!!.color = paintColor
    }

    fun startNew() {
        drawCanvas!!.drawColor(-0x1)
        this.invalidate()

    }
}