package com.mindthetime


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mindthetime.R
import com.mindthetime.model.Prediction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Manages the UI and logic for the entire departure board, including headers, rows, and the clock.
 */
class DepartureBoardActivity(private val view: View) {

    private val context: Context = view.context
    private val lineNameTextView: TextView = view.findViewById(R.id.board_line_name)
    private val dateTextView: TextView = view.findViewById(R.id.board_date_textview)
    private val stationNameTextView: TextView = view.findViewById(R.id.board_station_name)
    private val rowsContainer: LinearLayout = view.findViewById(R.id.departure_board_rows_container)
    private val clockTextView: TextView = view.findViewById(R.id.clock_textview)
    private var clockJob: Job? = null

    fun show() {
        view.visibility = View.VISIBLE
        startClock()
    }

    fun hide() {
        view.visibility = View.GONE
        stopClock()
    }

    /**
     * Clears and populates the departure board with new data.
     * @param lineName The name of the line to display.
     * @param platformName The platform name to display.
     * @param predictions The list of arrival predictions.
     */
    fun update(lineName: String, stationName: String?, predictions: List<Prediction>) {
        lineNameTextView.text = "$lineName Line"
        stationNameTextView.text = stationName ?: ""
        stationNameTextView.visibility = if (stationName.isNullOrBlank()) View.GONE else View.VISIBLE
        dateTextView.text = SimpleDateFormat("EEE dd MMM", Locale.getDefault()).format(Date())

        rowsContainer.removeAllViews()

        if (predictions.isEmpty()) {
            val noArrivalsView = TextView(context)
            noArrivalsView.text = "No arrivals found"
            noArrivalsView.setTextColor(context.getColor(R.color.tfl_amber))
            rowsContainer.addView(noArrivalsView)
            return
        }

        predictions.forEachIndexed { index, prediction ->
            val rowView = LayoutInflater.from(context).inflate(R.layout.departure_board_row, rowsContainer, false)

            val destinationTextView = rowView.findViewById<TextView>(R.id.destination_textview)
            val arrivalTimeTextView = rowView.findViewById<TextView>(R.id.arrival_time_textview)

            val destination = "${index + 1} ${prediction.destinationName?.replace("Underground Station", "")?.trim() ?: ""}"
            val minutes = TimeUnit.SECONDS.toMinutes(prediction.timeToStation.toLong())
            val timeText = if (minutes <= 0) "Now" else "$minutes mins"

            destinationTextView.text = destination
            arrivalTimeTextView.text = timeText

            rowsContainer.addView(rowView)
        }
    }

    private fun startClock() {
        stopClock()
        clockJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                clockTextView.text = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                delay(1000)
            }
        }
    }

    private fun stopClock() {
        clockJob?.cancel()
        clockJob = null
    }
}