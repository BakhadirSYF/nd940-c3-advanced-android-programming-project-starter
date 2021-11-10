package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.*
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)

    private var widthSize = 0
    private var heightSize = 0

    private var progressCircleSize = 0f

    private var linearProgressAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var buttonText = context.getString(R.string.download_button_label)

    private val rectF: RectF = RectF(0F, 0F, widthSize.toFloat(), heightSize.toFloat())
    private val circleF = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }


    init {
        isClickable = true
        Log.d("BAKHA_LOG", "init")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.d("BAKHA_LOG", "onSizeChanged")

        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(widthSize, heightSize, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)

        progressCircleSize = (min(w, h) / 2f) * 0.4f
    }

    private fun setupCircleF() {
        val buttonTextBounds = Rect()
        paint.getTextBounds(buttonText, 0, buttonText.length, buttonTextBounds)

        val horizontalOffset = widthSize / 2 + buttonTextBounds.width() / 2 + 48f
        val verticalOffset = (heightSize / 2f)

        circleF.set(
            horizontalOffset - progressCircleSize,
            verticalOffset - progressCircleSize,
            horizontalOffset + progressCircleSize,
            verticalOffset + progressCircleSize
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.d("BAKHA_LOG", "onDraw")
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        rectF.recalculateAfterOnMeasure(widthSize.toFloat(), heightSize.toFloat())

        // Draw button label
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText(
            buttonText,
            rectF.centerX(),
            rectF.centerY() + resources.getDimension(R.dimen.default_text_size) / 3,
            paint
        )

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
        Log.d("BAKHA_LOG", "onMeasure")
//        Log.d("BAKHA_LOG", "widthSize = $widthSize; heightSize = $heightSize")
    }

    private fun RectF.recalculateAfterOnMeasure(widthSize: Float, heightSize: Float) {
        left = 0F
        top = 0F
        right = widthSize
        bottom = heightSize
    }

    fun startProgressAnimation() {
        Log.d("BAKHA_LOG", "onClick")
        setupCircleF()
        setupLinearProgressAnimator()
        linearProgressAnimator.start()
    }

    private fun setupLinearProgressAnimator() {
        val colorTo = resources.getColor(R.color.colorPrimaryDark, null)
        val circleColor = resources.getColor(R.color.colorAccent, null)

        var progressColor = Paint()
        progressColor.color = colorTo

        val circleProgressColor = Paint(Paint.ANTI_ALIAS_FLAG)
        circleProgressColor.style = Paint.Style.FILL
        circleProgressColor.color = circleColor


        linearProgressAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        linearProgressAnimator.duration = 8000
        linearProgressAnimator.interpolator = LinearInterpolator()
        linearProgressAnimator.addUpdateListener {
//            Log.d("BAKHA_LOG", "valueAnimator animated value = ${it.animatedValue})")

            extraCanvas.drawRect(
                0f,
                0f,
                it.animatedValue as Float,
                heightSize.toFloat(),
                progressColor
            )

            extraCanvas.drawArc(circleF, 0f, it.animatedValue as Float, true, circleProgressColor)

            invalidate()
        }
    }

    fun updateProgressAnimation() {
        linearProgressAnimator.duration = 2000
        linearProgressAnimator.interpolator = AccelerateDecelerateInterpolator()
    }
}
