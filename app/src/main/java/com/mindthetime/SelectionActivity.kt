package com.mindthetime

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.mindthetime.adapter.TransportModeAdapter
import com.mindthetime.model.Prediction
import com.mindthetime.model.StopPoint
import com.mindthetime.model.TimetableSelection
import com.mindthetime.model.TransportMode
import com.mindthetime.repository.TflRepository
import com.mindthetime.DepartureBoardActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectionActivity : AppCompatActivity() {

    private lateinit var tflRepository: TflRepository
    private lateinit var departureBoard: DepartureBoardActivity
    private val selection = TimetableSelection()
    private var pollingJob: Job? = null

    private var allArrivalsForStation: List<Prediction> = emptyList()
    private var stations: List<StopPoint> = emptyList()

    // UI Components
    private lateinit var stationNameHeader: TextView
    private lateinit var stopPointLayout: TextInputLayout
    private lateinit var lineLayout: TextInputLayout
    private lateinit var directionLayout: TextInputLayout
    private lateinit var stopPointDropdown: AutoCompleteTextView
    private lateinit var lineDropdown: AutoCompleteTextView
    private lateinit var directionDropdown: AutoCompleteTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        tflRepository = TflRepository(this)
        initializeViews()
        setupTransportModeDropdown()
        setupStationSelection()
        setupLineSelection()
        setupDirectionSelection()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPolling()
    }

    private fun initializeViews() {
        stationNameHeader = findViewById(R.id.station_name_header)
        stopPointLayout = findViewById(R.id.stop_point_layout)
        lineLayout = findViewById(R.id.line_layout)
        directionLayout = findViewById(R.id.towards_layout)
        departureBoard = DepartureBoardActivity(findViewById(R.id.departure_board))
        stopPointDropdown = findViewById(R.id.stop_point_dropdown)
        lineDropdown = findViewById(R.id.line_dropdown)
        directionDropdown = findViewById(R.id.towards_dropdown)
    }

    private fun setupTransportModeDropdown() {
        val dropdown = findViewById<AutoCompleteTextView>(R.id.transport_mode_dropdown)
        dropdown.setAdapter(TransportModeAdapter(this, TransportMode.entries.toList()))
        dropdown.setOnItemClickListener { parent, _, position, _ ->
            resetSubsequentSelections(1)
            selection.transportMode = parent.adapter.getItem(position) as TransportMode
            fetchStopPointsForMode(selection.transportMode!!.apiName)
        }
    }

    private fun setupStationSelection() {
        stopPointDropdown.setOnItemClickListener { parent, _, position, _ ->
            resetSubsequentSelections(2)
            val selectedName = parent.getItemAtPosition(position) as String
            selection.station = stations.find { it.stationName == selectedName }
            selection.station?.let {
                stationNameHeader.text = it.stationName
                stationNameHeader.visibility = View.VISIBLE
                fetchArrivalsForStation(it.id)
            }
        }
    }

    private fun setupLineSelection() {
        lineDropdown.setOnItemClickListener { parent, _, position, _ ->
            resetSubsequentSelections(3)
            selection.lineId = parent.getItemAtPosition(position) as String
            populateDirectionDropdown()
        }
    }

    private fun setupDirectionSelection() {
        directionDropdown.setOnItemClickListener { _, _, _, _ ->
            startPolling()
        }
    }

    private fun fetchStopPointsForMode(mode: String) {
        CoroutineScope(Dispatchers.IO).launch {
            stations = tflRepository.getStopPoints(mode)
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@SelectionActivity, android.R.layout.simple_dropdown_item_1line, stations.map { it.stationName })
                stopPointDropdown.setAdapter(adapter)
                stopPointLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchArrivalsForStation(stationId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            allArrivalsForStation = tflRepository.getArrivals(stationId)
            val uniqueLines = allArrivalsForStation.map { it.lineName }.distinct()
            withContext(Dispatchers.Main) {
                val adapter = ArrayAdapter(this@SelectionActivity, android.R.layout.simple_dropdown_item_1line, uniqueLines)
                lineDropdown.setAdapter(adapter)
                lineLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun populateDirectionDropdown() {
        val directions = allArrivalsForStation
            .filter { it.lineName == selection.lineId && it.direction != null }
            .map { if (it.direction == "inbound") "Towards Central" else "Away from Central" }
            .distinct()

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, directions)
        directionDropdown.setAdapter(adapter)
        directionLayout.visibility = View.VISIBLE
    }

    private fun startPolling() {
        stopPolling()
        pollingJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                fetchAndDisplayDepartureBoard()
                delay(10000)
            }
        }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private suspend fun fetchAndDisplayDepartureBoard() {
        val stationId = selection.station?.id ?: return
        val lineId = selection.lineId ?: return
        val selectedDirection = if (directionDropdown.text.toString() == "Towards Central") "inbound" else "outbound"

        val arrivals = tflRepository.getArrivals(stationId)

        val filteredArrivals = arrivals
            .filter { it.lineName == lineId && it.direction == selectedDirection && !it.destinationName.isNullOrBlank() }
            .sortedBy { it.timeToStation }
            .take(3)

        val stationName = selection.station?.stationName ?: return

        withContext(Dispatchers.Main) {
            departureBoard.update(lineId, stationName, filteredArrivals)
            departureBoard.show()
        }
    }

    private fun resetSubsequentSelections(level: Int) {
        stopPolling()
        if (level <= 1) {
            stationNameHeader.visibility = View.GONE
            stopPointDropdown.text.clear()
            stopPointLayout.visibility = View.GONE
            selection.station = null
        }
        if (level <= 2) {
            lineDropdown.text.clear()
            lineLayout.visibility = View.GONE
            selection.lineId = null
        }
        if (level <= 3) {
            directionDropdown.text.clear()
            directionLayout.visibility = View.GONE
        }
        departureBoard.hide()
    }
}