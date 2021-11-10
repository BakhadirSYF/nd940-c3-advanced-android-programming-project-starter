package com.udacity

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.*
import androidx.core.animation.doOnEnd
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var buttonBackgroundColor = 0
    private var buttonLoadingBackgroundColor = 0
    private var circularLoadingColor = 0
    private var labelDownload = ""
    private var labelLoading = ""

    private var widthSize = 0
    private var heightSize = 0

    private var progressCircleSize = 0f

    private var linearProgressAnimator = ValueAnimator()
    private var circleProgressAnimator = ValueAnimator()

    private val animatorSet: AnimatorSet = AnimatorSet()

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                buttonText = labelLoading
                startProgressAnimation()
            }

            else -> {
                updateProgressAnimation()
            }
        }
    }

    private var buttonText = ""

    private val rectF: RectF = RectF(0F, 0F, widthSize.toFloat(), heightSize.toFloat())
    private val circleF = RectF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_defaultBackgroundColor, 0)
            buttonLoadingBackgroundColor =
                getColor(R.styleable.LoadingButton_loadingBackgroundColor, 0)
            circularLoadingColor = getColor(R.styleable.LoadingButton_circularLoadingTextColor, 0)
            labelDownload = getText(R.styleable.LoadingButton_defaultLabel) as String
            labelLoading = getText(R.styleable.LoadingButton_loadingLabel) as String
        }
        buttonText = labelDownload
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        redrawCanvas()
        progressCircleSize = (min(w, h) / 2f) * 0.4f
    }

    private fun redrawCanvas() {
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(widthSize, heightSize, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(buttonBackgroundColor)
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
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        rectF.recalculateAfterOnMeasure(widthSize.toFloat(), heightSize.toFloat())

        // Draw button label
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
    }

    private fun RectF.recalculateAfterOnMeasure(widthSize: Float, heightSize: Float) {
        left = 0F
        top = 0F
        right = widthSize
        bottom = heightSize
    }

    private fun startProgressAnimation() {
        setupCircleF()
        setupProgressAnimators()
        animatorSet.playTogether(linearProgressAnimator, circleProgressAnimator)
        animatorSet.start()
        animatorSet.doOnEnd {
            redrawCanvas()
            buttonText = labelDownload
            invalidate()
        }
    }

    private fun setupProgressAnimators() {
        var progressColor = Paint()
        progressColor.color = buttonLoadingBackgroundColor

        val circleProgressColor = Paint(Paint.ANTI_ALIAS_FLAG)
        circleProgressColor.style = Paint.Style.FILL
        circleProgressColor.color = circularLoadingColor


        linearProgressAnimator = ValueAnimator.ofFloat(0f, widthSize.toFloat())
        linearProgressAnimator.duration = 4000
        linearProgressAnimator.interpolator = AccelerateDecelerateInterpolator()
        linearProgressAnimator.addUpdateListener {
            extraCanvas.drawRect(
                0f,
                0f,
                it.animatedValue as Float,
                heightSize.toFloat(),
                progressColor
            )
            invalidate()
        }

        circleProgressAnimator = ValueAnimator.ofFloat(0f, 360f)
        circleProgressAnimator.duration = 4000
        circleProgressAnimator.interpolator = AccelerateDecelerateInterpolator()
        circleProgressAnimator.addUpdateListener {
            extraCanvas.drawArc(
                circleF,
                0f,
                it.animatedValue as Float,
                true,
                circleProgressColor
            )
            invalidate()
        }
    }

    private fun updateProgressAnimation() {
        linearProgressAnimator.duration = 2000
        linearProgressAnimator.interpolator = AccelerateInterpolator()
        circleProgressAnimator.duration = 2000
        circleProgressAnimator.interpolator = AccelerateInterpolator()
        animatorSet.start()
    }

    fun updateButtonState(state: ButtonState) {
        if (buttonState != state) {
            buttonState = state
            invalidate()
        }
    }
}
