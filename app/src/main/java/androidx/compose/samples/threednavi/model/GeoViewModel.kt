package androidx.compose.samples.threednavi.model

import androidx.compose.samples.threednavi.BalloonLayout
import androidx.compose.samples.threednavi.network.ApiService
import androidx.compose.samples.threednavi.network.RequestType
import androidx.compose.samples.threednavi.network.RetrofitHelper
import androidx.lifecycle.*
import com.madrapps.plot.line.DataPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.android.gms.maps.model.LatLng

@HiltViewModel
class GeoViewModel @Inject constructor(
    //private val destinationsRepository: DestinationsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var _distances : MutableStateFlow<MutableList<Double?>?> = MutableStateFlow(mutableListOf())
    val distances = _distances.asStateFlow()

    private var _elevation : MutableStateFlow<MutableList<Int?>?> = MutableStateFlow(mutableListOf())
    val elevation = _elevation.asStateFlow()

    private var _route : MutableStateFlow<List<DataPoint?>?> = MutableStateFlow(listOf(DataPoint(0.0f, 0.0f)))
    val route = _route.asStateFlow()

    private var _routeGeometry : MutableStateFlow<List<LatLng?>?> = MutableStateFlow(listOf(LatLng(0.0, 0.0)))
    val routeGeometry = _routeGeometry.asStateFlow()

    private var _routeError : MutableStateFlow<String?> = MutableStateFlow("")
    val routeError = _routeError.asStateFlow()

    private var _destination : MutableStateFlow<List<String?>?> = MutableStateFlow(mutableListOf())
    val destination = _destination.asStateFlow()

    private var _endPoint : MutableStateFlow<String> = MutableStateFlow("")
    val endPoint = _endPoint.asStateFlow()

    private var _baloonMode : MutableStateFlow<BalloonLayout> = MutableStateFlow(BalloonLayout.ADDRESS)
    val baloonMode = _baloonMode.asStateFlow()

    private var _teste : MutableStateFlow<String?> = MutableStateFlow("")
    val teste = _teste.asStateFlow()

    private var _destError : MutableStateFlow<String?> = MutableStateFlow("")
    val destError = _destError.asStateFlow()

    private var _elevError : MutableStateFlow<String?> = MutableStateFlow("")
    val elevError = _elevError.asStateFlow()


    fun initializeRoute() {
        _distances.value = mutableListOf()
        _elevation.value = mutableListOf()
        _destination.value  = mutableListOf()
        _endPoint.value  =""
        _route.value  = listOf(DataPoint(0.0f, 0.0f))
        _routeGeometry.value = listOf(LatLng(0.0, 0.0))
    }

    fun defineEndPoint(spoken: String?) {
        if (spoken != null) {
            _endPoint.value = spoken
        }
    }

    fun changeBaloonMode() {
        when(_baloonMode.value){
            BalloonLayout.ADDRESS -> _baloonMode.value = BalloonLayout.MODE
            BalloonLayout.MODE -> _baloonMode.value = BalloonLayout.ADDRESS
        }
    }

    suspend fun getRoute(startPoint :List<Double>, mode: String) {
        geocodeAddress(_endPoint.value)
        try {
            val GeoApi = RetrofitHelper.getInstance(RequestType.ROUTE).create(ApiService::class.java)
            val waypoints = startPoint[1].toString() + "," +
                    startPoint[0].toString() + ";" + _destination.value?.get(1).toString() +
                    "," + _destination.value?.get(0).toString()
            val response = GeoApi.getDirectionsResponse(waypoints, API_KEY, "json", "esriNAUKilometers",
            "esriDOTFeatureSets")
            val res = response.body()!!
            _teste.value = res.toString()
            var auxGeo = mutableListOf<LatLng?>(LatLng(startPoint[0], startPoint[1]))
            var auxDist = mutableListOf<Double?>(0.0)

            res.directionLines?.features?.forEach { feature ->
                //Get path
                auxGeo.add(LatLng(feature?.geometry?.paths?.get(0)?.last()?.get(1)!!,
                    feature.geometry.paths.get(0)?.last()?.get(0)!!))
                //Get distances
                auxDist.add(feature.attributes?.Meters)
            }
            _routeGeometry.value = auxGeo
            _distances.value = auxDist
            _distances.value?.forEachIndexed { index, d ->
                if(index != 0){
                    _distances.value!![index] = d?.let { _distances.value!![index -1]?.plus(it) }
                }
            }

        } catch (e: Exception) {
            val result = null
            _routeError.value = e.toString()
        }
    }

    fun getElevation(startPoint :List<Double>, mode: String) {
        viewModelScope.launch {
            getRoute(startPoint, mode)
            try {
                val GeoApi = RetrofitHelper.getInstance(RequestType.ELEVATION).create(ApiService::class.java)
                var auxInput = mutableListOf<String>()
                    _routeGeometry.value?.forEach { it ->
                        auxInput.add(it?.latitude.toString()+","+it?.longitude.toString())
                    }
                val response = GeoApi.getElevationResponse(auxInput.joinToString(separator = "|"))
                // Get distances and elevations for each step
                val res = response.body()

                var routeAux = mutableListOf<DataPoint>()
                res?.results?.forEachIndexed { index, i ->
                    routeAux.add(DataPoint(_distances.value?.get(index)!!.toFloat(), i?.elevation!!.toFloat()))
                }
                _route.value = routeAux
                _distances.value?.clear()
            } catch (e: Exception) {
                val result = null
                _elevError.value = e.toString()
            }
        }
    }

    suspend fun geocodeAddress(address :String) {
        try {
            val GeoApi = RetrofitHelper.getInstance(RequestType.GEOCODE).create(ApiService::class.java)
            val response = GeoApi.getGeocodingResponse(address, "*", "json", API_KEY)
            if (response != null) {
                val res = response.body()?.candidates?.get(0)?.location
                _destination.value = listOf(res?.y.toString(), res?.x.toString())
            } else {
                return
            }
        } catch (e: Exception) {
            val result = null
            _destError.value = e.toString()
        }
    }

    companion object {
        //Jawg API Key
        private const val API_KEY = "AAPK6373db1cf52e4b5e904becd420d70264ou6h2Us9m98O_qcfqwHfBcEpMBNH7li1blFYXr8nm-SeNO7Yf5N-MSRrzdCQvsa8"
    }

}

data class GeoPair(
    val distance: Double?,
    val elevation: Int?,
    val coordinates: List<Double?>?
)

/*
class GeoViewModelFactory(
    private val repository: GeoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
 */