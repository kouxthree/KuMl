package com.ku.kuml.parts

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mlkit.vision.face.Face

class SunglassesView: View {
    private lateinit var mPaint: Paint
//    private lateinit var matrix: Matrix
    private var mFaces: List<Face>? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle ) {
        initialize()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initialize()
    }
    constructor(context: Context?) : super(context) {
        initialize()
    }

    private fun initialize() {
//        matrix = Matrix()
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = Color.RED //Color.MAGENTA
        //mPaint.alpha = 128
        mPaint.style = Paint.Style.FILL_AND_STROKE
    }

    fun setFaces(faces: List<Face>) {
        mFaces = faces
        invalidate()
        //postInvalidate()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //drawsomething(canvas)
        if(mFaces == null) return
        for (face in mFaces!!) {
            if(face == null) continue
//            matrix.postScale(width / 2000f, height / 2000f)
//            matrix.postTranslate(width / 2f, height / 2f)
            val saveCount = canvas.save()
//            canvas.concat(matrix)
            canvas.drawRect(face.boundingBox, mPaint)
            canvas.restoreToCount(saveCount)
        }
    }
    private fun drawsomething(canvas: Canvas) {
        val left = width * Math.random()
        val top = height * Math.random()
        val right = width * Math.random()
        val bottom = height * Math.random()
        val rect = android.graphics.Rect(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
//        matrix.postScale(width / 2000f, height / 2000f)
//        matrix.postTranslate(width / 2f, height / 2f)
        val saveCount = canvas.save()
//        canvas.concat(matrix)
        canvas.drawRect(rect, mPaint)
        canvas.restoreToCount(saveCount)
    }
}