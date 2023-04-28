package androidx.compose.samples.threednavi.network


import androidx.compose.samples.threednavi.network.directionsdata.DirectionsData
import androidx.compose.samples.threednavi.network.elevationdata.ElevationData
import androidx.compose.samples.threednavi.network.geocodedata.GeocodeData
import com.google.gson.GsonBuilder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Create a property for the base URL
private const val ROUTE_BASE_URL = "https://route.arcgis.com/arcgis/rest/services/World/Route/NAServer/Route_World/"
private const val ELEVATION_BASE_URL = "https://api.opentopodata.org/v1/"
private const val GEOCODE_BASE_URL = "https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer/"

enum class RequestType{
    ROUTE, ELEVATION, GEOCODE
}

object RetrofitHelper {
    fun getInstance(requestType: RequestType): Retrofit {

        var BASE_URL = when(requestType){
            RequestType.ROUTE -> ROUTE_BASE_URL
            RequestType.ELEVATION -> ELEVATION_BASE_URL
            RequestType.GEOCODE -> GEOCODE_BASE_URL
        }

        val gson = GsonBuilder().setLenient()
            .enableComplexMapKeySerialization()
            .create()

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            // we need to add converter factory to
            // convert JSON object to Java object
            .build()
    }
}

interface ApiService {
    // Declare a suspended function to get the API
    //@Headers("X-RapidAPI-Key: 935a0d4e4amshbca22437a47c265p10dd38jsne3f59264aab9",
        //"X-RapidAPI-Host: maptoolkit.p.rapidapi.com")
    @GET("solve?")
    suspend fun getDirectionsResponse(@Query("stops") stops : String,
                                      @Query("token") token : String,
                                      @Query("f") f : String,
                                      @Query("directionsLengthUnits") directionsLengthUnits : String,
                                      @Query("directionsOutputType") directionsOutputType : String) : Response<DirectionsData>

    //directionsLengthUnits=esriNAUKilometers&directionsOutputType=esriDOTFeatureSets

    @GET("aster30m?")
    suspend fun getElevationResponse(@Query("locations") locations : String) : Response<ElevationData>

    @GET("findAddressCandidates?")
    suspend fun getGeocodingResponse(@Query("address") address : String,
                                     @Query("outFields") outFields : String,
                                     @Query("f") f : String,
                                     @Query("token") token : String) : Response<GeocodeData>
}
