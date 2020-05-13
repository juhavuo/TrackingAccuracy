package fi.metropolia.juhavuo.trackingaccuracy

import kotlin.math.pow
import kotlin.math.sqrt

class KalmanFilter(speed: Float){

    private val minAccuracy = 1f

    private var meanSpeed = speed
    var lat = 0.0
    private set

    var lng = 0.0
    private set

    var variance = -1f
    private set

    var ts = 0L
    private set

    fun getAccuracy(): Float = sqrt(variance)

    fun setState(latitude: Double, longitude: Double, accuracy: Float, timestamp: Long){
        lat = latitude
        lng = longitude
        variance = accuracy.pow(2)
        ts = timestamp
    }

    fun process(measured_lat: Double, measured_lng: Double, me_acc: Float, timestamp: Long){
        var measured_acc = me_acc
        if(measured_acc<minAccuracy){
           measured_acc = minAccuracy
        }
        if(variance<0){
            setState(measured_lat,measured_lng,measured_acc,timestamp)
        }else{
            val timeBetween = timestamp - ts
            if(timeBetween> 0){
                variance += timeBetween*meanSpeed.pow(2)/1000
                ts = timestamp
            }
        }

        val k = variance/(variance+measured_acc.pow(2))
        lat += k*(measured_lat-lat)
        lng += k*(measured_lng-lng)
        variance *= (1 - k)
    }





}