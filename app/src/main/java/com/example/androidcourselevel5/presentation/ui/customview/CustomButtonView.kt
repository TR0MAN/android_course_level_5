package com.example.androidcourselevel5.presentation.ui.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.androidcourselevel5.R

class CustomButtonView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : View(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            this( context, attrs, defStyleAttr, 0)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    // text attribute for button
    private var buttonText: String? = null
    private var buttonTextColor: Int? = null
    private var buttonTextSize: Float? = null
    private var buttonTextStyle: Int? = null
    private var buttonLetterSpacing: Float? = null

    // attribute for button
    private var buttonBackgroundColor: Int? = null
    private var buttonBorderLineColor: Int? = null
    private var buttonBorderLineWidth: Float? = null
    private var buttonCornerRadius: Float? = null

    // icon attribute for button
    private var buttonImage: Drawable? = null
    private var buttonImageHeight: Float? = null
    private var distanceToText: Float? = null

    // set of brushes for painting (button, text, image)
    private var textPaint = Paint()
    private var buttonBackgroundPaint = Paint()
    private var buttonStrokePaint = Paint()

    // others additional variables
    private var centerX = 0f
    private var centerY = 0f
    private val textBoundRect = Rect()
    private var textWidth = 0
    private var textHeight = 0
    private var allInnerContentWidth: Float? = null
    private val placeForImage = Rect()
    private val imageBody = Rect()

    init {
        getViewAttributes(attrs, defStyleAttr, defStyleRes)
        initPaints()
    }

    private fun initPaints() {
        buttonBackgroundPaint.style = Paint.Style.FILL
        buttonBackgroundPaint.color = buttonBackgroundColor!!
        buttonBackgroundPaint.isAntiAlias = true

        buttonStrokePaint.style = Paint.Style.FILL
        buttonStrokePaint.color = buttonBorderLineColor!!
        buttonStrokePaint.isAntiAlias = true

        textPaint.style = Paint.Style.FILL
        textPaint.color = buttonTextColor!!
        textPaint.textSize = buttonTextSize!!
        textPaint.letterSpacing = buttonLetterSpacing!!

        when (buttonTextStyle) {
            0 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))
            1 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
            2 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC))
            3 -> textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC))
            4 -> textPaint.typeface = ResourcesCompat.getFont(context, R.font.opensans_semibold_600)
        }
        textPaint.isAntiAlias = true
    }

    private fun getViewAttributes(attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs == null) return

        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.CustomButtonView,
            defStyleAttr,
            defStyleRes
        )
        buttonText =
            attributes.getString(R.styleable.CustomButtonView_buttonText) ?: "Custom Button"
        buttonTextColor =
            attributes.getColor(R.styleable.CustomButtonView_buttonTextColor, Color.BLACK)
        buttonTextSize = attributes.getDimension(R.styleable.CustomButtonView_buttonTextSize, 40f)
        buttonBackgroundColor =
            attributes.getColor(R.styleable.CustomButtonView_buttonBackgroundColor, Color.GRAY)
        buttonBorderLineColor =
            attributes.getColor(R.styleable.CustomButtonView_buttonBorderLineColor, Color.GRAY)
        buttonBorderLineWidth =
            attributes.getDimension(R.styleable.CustomButtonView_buttonBorderLineWidth, 0f)
        buttonCornerRadius =
            attributes.getDimension(R.styleable.CustomButtonView_buttonCornerRadius, 0f)
        buttonImage = attributes.getDrawable(R.styleable.CustomButtonView_buttonImage)
        buttonImageHeight =
            attributes.getDimension(R.styleable.CustomButtonView_buttonImageHeight, 40f)
        distanceToText =
            attributes.getDimension(R.styleable.CustomButtonView_buttonDistanceToText, 10f)
        buttonLetterSpacing = attributes.getFloat(R.styleable.CustomButtonView_buttonTextLetterSpacing, 0.1f)
        buttonTextStyle = attributes.getInt(R.styleable.CustomButtonView_buttonTextStyle, 0)
        attributes.recycle()


    }

    // parameters receive the Width and Height values with the margin already taken into account (minus) from the XML file
    // calculation result comes to the onDraw method as getWidth and getHeight
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minimumWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minimumHeight = suggestedMinimumHeight + paddingTop + paddingBottom

        setMeasuredDimension(
            resolveSize(minimumWidth, widthMeasureSpec),
            resolveSize(minimumHeight, heightMeasureSpec)
        )
    }

    // painting method on "canvas"
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        centerX = width / 2f
        centerY = height / 2f

        // we get the size of the rectangle with the text, we find out the width and height of the text
        textPaint.getTextBounds(buttonText, 0, buttonText!!.length, textBoundRect)
        textWidth = textPaint.measureText(buttonText).toInt()
        textHeight = textBoundRect.height()

        drawButtonStroke(canvas)
        drawButtonBackground(canvas)
        drawButtonIcon(canvas)
        drawButtonText(canvas)
    }

    private fun drawButtonText(canvas: Canvas) {

        // if button does not have an icon, then we display the text without taking into account
        // the offset to the image
        if (allInnerContentWidth == null) {
            canvas.drawText(
                buttonText!!,
                centerX - textWidth / 2,
                centerY + textHeight / 2.1f,
                textPaint
            )
        } else {
            canvas.drawText(
                buttonText!!,
                centerX - allInnerContentWidth!! / 2 + buttonImageHeight!! + distanceToText!!,
                centerY + textHeight / 2.1f,
                textPaint
            )
        }
    }

    // draw the image over the background of the rectangle
    private fun drawButtonIcon(canvas: Canvas) {
        // if there is no picture, then we don’t calculate or do anything at all
        buttonImage?.let { icon ->
            val bitmap = icon.toBitmap(
                icon.minimumWidth,
                icon.minimumHeight,
                Bitmap.Config.ARGB_8888
            )
            // find maximum height of the button
            val maxImageHeight =
                (height - paddingTop - paddingBottom - buttonBorderLineWidth!!) * 0.85f

            // find size of the content inside the button (text + image width + space between them)
            allInnerContentWidth = textWidth + buttonImageHeight!! + distanceToText!!

            // adjust the image size if the set image height is greater than
            // height of the free space inside the button
            val buttonIcon = if (buttonImageHeight!! > maxImageHeight)
                maxImageHeight.toInt()
            else
                buttonImageHeight!!.toInt()

            // take all 100% of the pictures
            imageBody.apply {
                left = 0
                top = 0
                right = bitmap.width
                bottom = bitmap.height
            }

            // indicate the coordinates of the place where the picture will be inserted
            placeForImage.apply {
                left = (centerX - allInnerContentWidth!! / 2).toInt()
                top = (centerY - buttonIcon / 2).toInt()
                right = ((centerX - allInnerContentWidth!! / 2) + buttonIcon).toInt()
                bottom = (centerY + buttonIcon / 2).toInt()
            }
            canvas.drawBitmap(bitmap, imageBody, placeForImage, null)
        }
    }

    // draw button background
    private fun drawButtonBackground(canvas: Canvas) {
        canvas.drawRoundRect(
            (centerX - width / 2f) + paddingLeft + buttonBorderLineWidth!!,
            (centerY - height / 2f) + paddingTop + buttonBorderLineWidth!!,
            (centerX + width / 2f) - paddingRight - buttonBorderLineWidth!!,
            (centerY + height / 2f) - paddingBottom - buttonBorderLineWidth!!,
            buttonCornerRadius!!,
            buttonCornerRadius!!,
            buttonBackgroundPaint
        )
    }

    // draw the outline of the button
    // if color is the default, then set the color as the background of the button
    private fun drawButtonStroke(canvas: Canvas) {
        if (buttonStrokePaint.color == Color.GRAY) {
            buttonStrokePaint.color = buttonBackgroundColor!!
        }
        canvas.drawRoundRect(
            (centerX - width / 2f) + paddingLeft,
            (centerY - height / 2f) + paddingTop,
            (centerX + width / 2f) - paddingRight,
            (centerY + height / 2f) - paddingBottom,
            buttonCornerRadius!!,
            buttonCornerRadius!!,
            buttonStrokePaint
        )
    }

    // implementation of the reaction to pressing a button
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                buttonBackgroundPaint.color = Color.LTGRAY
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                buttonBackgroundPaint.color = buttonBackgroundColor!!
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }
}
