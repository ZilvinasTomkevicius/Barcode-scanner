package app.georentate.barcodescanner.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.AttributeSet
import android.view.View



class CustomIndicator(context: Context, attrs: AttributeSet?): View(context, attrs) {

    private val paint = Paint()
    private var mState: Int = 0
    private var xStart: Float = 0f
    private var xEnd: Float = 0f
    private var yStart: Float = 0f
    private var yEnd: Float = 0f

    lateinit var shapeDrawable: ShapeDrawable

    override fun onDraw(canvas: Canvas?) {
        when(mState) {
            1 -> {
//                paint.color = Color.GREEN
//                paint.strokeWidth = 20f
//                canvas!!.drawLine(0f, 0f, width.toFloat() /2f, height.toFloat(), paint)
//                canvas.drawLine(width /2f, height.toFloat(), width.toFloat(), height.toFloat() /2f, paint)

                paint.color = Color.GREEN
                paint.strokeWidth = 20f
                canvas!!.drawRect(100f, 100f, 600f, 400f, paint)
            }
            2 -> {
                paint.color = Color.RED
                paint.strokeWidth = 20f
                canvas!!.drawLine(0f, 0f, width.toFloat(), height.toFloat(), paint)
                canvas.drawLine(0f, height.toFloat(), width.toFloat(), 0f, paint)
            }
            3 -> {
//                paint.color = Color.BLUE
//                paint.strokeWidth = 20f
//
//                canvas!!.drawLine(0f, 0f, width.toFloat() / 2f, height.toFloat(), paint)
//                canvas.drawLine(width.toFloat() / 2f, height.toFloat(), width.toFloat(), 0f, paint)
//                canvas.drawLine(width.toFloat(), 0f, 0f, 0f, paint)

//                shapeDrawable = ShapeDrawable(RectShape())
//                shapeDrawable.setBounds(0, height, width, 0)
//                shapeDrawable.paint.color = Color.BLUE
//                shapeDrawable.draw(canvas!!)

                paint.color = Color.BLUE
                paint.strokeWidth = 20f
                canvas!!.drawRect(0f, height.toFloat(), width.toFloat(), width.toFloat(), paint)
            }
        }
    }

    fun draw(state: Int) {
        this.mState = state
        invalidate()
    }
}