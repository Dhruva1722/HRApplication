package com.example.afinal.MapActivity

class KalmanFilter(var processNoise: Float) {

    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var latVariance: Double = 1.0
    private var lngVariance: Double = 1.0

    fun process(latitude: Double, longitude: Double, accuracy: Float, deltaTime: Long) {
        // Prediction
        val predictionLat = lat
        val predictionLng = lng
        val predictionLatVariance = latVariance + processNoise
        val predictionLngVariance = lngVariance + processNoise

        // Update
        val kalmanGainLat = predictionLatVariance / (predictionLatVariance + accuracy * accuracy)
        val kalmanGainLng = predictionLngVariance / (predictionLngVariance + accuracy * accuracy)

        lat = predictionLat + kalmanGainLat * (latitude - predictionLat)
        lng = predictionLng + kalmanGainLng * (longitude - predictionLng)
        latVariance = (1 - kalmanGainLat) * predictionLatVariance
        lngVariance = (1 - kalmanGainLng) * predictionLngVariance
    }

    fun getLatitude(): Double {
        return lat
    }

    fun getLongitude(): Double {
        return lng
    }
}