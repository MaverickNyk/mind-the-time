package com.mindthetime.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.mindthetime.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LineStatusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var statusText: String = ""
    private var reasonText: String = ""

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.tfl_amber)
        textSize = 52f
        isAntiAlias = true
        typeface = ResourcesCompat.getFont(context, R.font.tfl_pixel_font)
        setShadowLayer(2.5f, 0f, 0f, ContextCompat.getColor(context, R.color.tfl_amber_glow))
    }

    private var scrollJob: Job? = null
    private var textX = 0f
    private var reasonWidth = 0f
    private var statusWidth = 0f
    private val gap = 200f

    fun setStatus(status: String, reason: String?) {
        this.statusText = "$status: "
        this.reasonText = reason ?: ""
        stopScrolling()
        startScrolling()
        invalidate()
    }

    private fun startScrolling() {
        stopScrolling()
        scrollJob = CoroutineScope(Dispatchers.Main).launch {
            statusWidth = paint.measureText(statusText)
            reasonWidth = paint.measureText(reasonText)
            textX = 0f

            if (reasonWidth < (width - statusWidth)) {
                postInvalidate()
                return@launch
            }

            textX = 0f

            while (true) {
                textX -= 2f
                if (textX < -(reasonWidth + gap)) {
                    textX = 0f
                }
                postInvalidate()
                delay(16)
            }
        }
    }

    private fun stopScrolling() {
        scrollJob?.cancel()
        scrollJob = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val yPos = (height / 2f) - ((paint.descent() + paint.ascent()) / 2f)

        canvas.drawText(statusText, 0f, yPos, paint)

        if (reasonText.isNotEmpty()) {
            canvas.save()
            canvas.clipRect(statusWidth, 0f, width.toFloat(), height.toFloat())
            canvas.drawText(reasonText, statusWidth + textX, yPos, paint)
            if (reasonWidth > (width - statusWidth)) {
                 canvas.drawText(reasonText, statusWidth + textX + reasonWidth + gap, yPos, paint)
            }
            canvas.restore()
        }
    }
}