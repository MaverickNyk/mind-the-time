package com.mindthetime.network

import com.mindthetime.model.LineStatus
import com.mindthetime.model.Prediction
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("StopPoint/Mode/{mode}")
    suspend fun getStopPoints(
        @Path("mode") mode: String,
        @Query("app_key") appKey: String = API_KEY
    ): Response<ResponseBody>

    @GET("StopPoint/{id}/Arrivals")
    suspend fun getArrivals(
        @Path("id") id: String,
        @Query("app_key") appKey: String = API_KEY
    ): Response<List<Prediction>>

    @GET("Line/Mode/{mode}/Status")
    suspend fun getLineStatus(
        @Path("mode") mode: String,
        @Query("app_key") appKey: String = API_KEY
    ): Response<List<LineStatus>>

    companion object {
        const val BASE_URL = "https://api.tfl.gov.uk/"
        const val API_KEY = "bff7e80b234d440d9fea4a1b3b96fae4" // This should be stored securely
    }
}