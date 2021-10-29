package com.udacity

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.renderscript.Sampler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import androidx.core.content.res.ResourcesCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)

    private var widthSize = 0
    private var heightSize = 0

    internal var valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private val rectF: RectF = RectF(0F, 0F, widthSize.toFloat(), heightSize.toFloat())

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 45.0f
//        typeface = Typeface.create("", Typeface.BOLD)
    }


    init {
        isClickable = true
        Log.d("BAKHA_LOG", "init")
//        setOnClickListener(this)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("BAKHA_LOG", "onSizeChanged")

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(widthSize, heightSize, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        Log.d("BAKHA_LOG", "onDraw")
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        rectF.recalcAfterOnMeasure(widthSize.toFloat(), heightSize.toFloat())

        // Draw button label
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Download", rectF.centerX(), rectF.centerY(), paint)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
//        Log.d("BAKHA_LOG", "onMeasure")
//        Log.d("BAKHA_LOG", "widthSize = $widthSize; heightSize = $heightSize")
    }

    private fun RectF.recalcAfterOnMeasure(widthSize: Float, heightSize: Float) {
        left = 0F
        top = 0F
        right = widthSize
        bottom = heightSize
    }

    fun startProgressAnimation() {
        Log.d("BAKHA_LOG", "onClick")
        val colorFrom = resources.getColor(R.color.colorPrimary, null)
        val colorTo = resources.getColor(R.color.colorPrimaryDark, null)

        var progressColor = Paint()
        progressColor.color = colorTo

        valueAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        valueAnimator.duration = 8000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener {
//            Log.d("BAKHA_LOG", "valueAnimator animated value = ${it.animatedValue})")

            extraCanvas.drawRect(0f, 0f, it.animatedValue as Float, heightSize.toFloat(), progressColor)
            invalidate()
        }
        valueAnimator.start()
    }

    fun updateProgressAnimation() {
        valueAnimator.duration = 2000
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
    }
}
