package com.mindthetime.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.mindthetime.model.Prediction
import com.mindthetime.model.StopPoint
import com.mindthetime.model.StopPointResponse
import com.mindthetime.model.TransportMode
import com.mindthetime.network.ApiClient
import com.mindthetime.serialization.StopPointDeserializer
import java.io.File

class TflRepository(private val context: Context) {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(StopPoint::class.java, StopPointDeserializer())
        .create()

    private val cacheFile = File(context.cacheDir, "stations_cache.json")
    private val mapType = object : TypeToken<MutableMap<String, List<StopPoint>>>() {}.type

    private fun readCacheMap(): MutableMap<String, List<StopPoint>> {
        if (!cacheFile.exists()) return mutableMapOf()
        return try {
            gson.fromJson(cacheFile.readText(), mapType) ?: mutableMapOf()
        } catch (e: Exception) {
            Log.e("TflRepository", "Error reading cache map", e)
            mutableMapOf()
        }
    }

    private fun writeCacheMap(data: Map<String, List<StopPoint>>) {
        try {
            cacheFile.writeText(gson.toJson(data))
        } catch (e: Exception) {
            Log.e("TflRepository", "Error writing cache map", e)
        }
    }

    suspend fun getStopPoints(mode: String): List<StopPoint> {
        val cacheMap = readCacheMap()
        if (cacheMap.containsKey(mode)) {
            return cacheMap[mode]!!
        }
        val newStopPoints = fetchStopPointsFromNetwork(mode)
        if (newStopPoints.isNotEmpty()) {
            cacheMap[mode] = newStopPoints
            writeCacheMap(cacheMap)
        }
        return newStopPoints
    }

    suspend fun fetchAllAndCache() {
        val allStationsMap = readCacheMap()
        for (mode in TransportMode.entries) {
            if (!allStationsMap.containsKey(mode.apiName)) {
                val stopPoints = fetchStopPointsFromNetwork(mode.apiName)
                if (stopPoints.isNotEmpty()) {
                    allStationsMap[mode.apiName] = stopPoints
                }
            }
        }
        writeCacheMap(allStationsMap)
    }

    private suspend fun fetchStopPointsFromNetwork(mode: String): List<StopPoint> {
        val transportMode = TransportMode.entries.find { it.apiName == mode }
        try {
            val response = ApiClient.apiService.getStopPoints(mode.lowercase())
            if (response.isSuccessful) {
                val responseString = response.body()?.string()
                if (!responseString.isNullOrEmpty()) {
                    val jsonElement = gson.fromJson(responseString, JsonElement::class.java)
                    var stopPoints: List<StopPoint>? = null

                    if (jsonElement.isJsonObject) {
                        val stopPointResponse = gson.fromJson(jsonElement, StopPointResponse::class.java)
                        stopPoints = stopPointResponse.stopPoints
                    } else if (jsonElement.isJsonArray) {
                        val collectionType = object : TypeToken<List<StopPoint>>() {}.type
                        stopPoints = gson.fromJson(jsonElement, collectionType)
                    }

                    if (stopPoints != null) {
                        return if (transportMode?.stopTypeFilter != null) {
                            stopPoints.filter { it.stopType == transportMode.stopTypeFilter }
                        } else {
                            stopPoints
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("TflRepository", "API or Parsing Exception for mode '$mode'", e)
        }
        return emptyList()
    }


    suspend fun getArrivals(stopPointId: String): List<Prediction> {
        try {
            val response = ApiClient.apiService.getArrivals(stopPointId)
            if (response.isSuccessful) {
                return response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("TflRepository", "API Exception for arrivals", e)
        }
        return emptyList()
    }
}