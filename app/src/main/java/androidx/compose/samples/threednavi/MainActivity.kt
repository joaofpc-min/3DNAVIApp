package androidx.compose.samples.threednavi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.samples.threednavi.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.samples.threednavi.model.GeoViewModel
import androidx.compose.samples.threednavi.theme.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import com.google.android.gms.location.LocationServices
import com.skydoves.balloon.compose.Balloon
import com.skydoves.balloon.compose.rememberBalloonBuilder
import com.skydoves.balloon.compose.setBackgroundColor
import com.skydoves.balloon.compose.setTextColor

@AndroidEntryPoint
class MainActivity : ComponentActivity(), RecognitionListener {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent : Intent
    private var shouldSpeak = true

    private val viewModel: GeoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getSpeakPermission()

        viewModel.initializeRoute()

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(this)

        speechRecognizerIntent  = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR")
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_SEGMENTED_SESSION, RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS)

        setContent {
            BasicsCodelabTheme {
                Surface {
                    DetailsScreen(
                        onErrorLoading = { finish() },
                        modifier = Modifier.systemBarsPadding(),
                        context = this,
                        onTalk = { talk() },
                        shouldSpeak = shouldSpeak,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permission -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                shouldSpeak = true
            } else {
                Toast.makeText(this@MainActivity, "Permission Denied!",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getSpeakPermission() {
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            permission
        )
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Toast.makeText(this, "Ready", Toast.LENGTH_SHORT).show()
    }

    override fun onBeginningOfSpeech() {
        Log.d("Recognizer", "Started")
    }

    override fun onRmsChanged(p0: Float) {
        Log.d("RMS", "rms")
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.d("Buffer", "Buffer Received")
    }

    override fun onEndOfSpeech() {
        viewModel.changeBaloonMode()
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
    }

    override fun onError(error: Int) {
        val errorMessage: String = getErrorText(error)
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onResults(results: Bundle?) {
        val matches = results!!.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        var text = " "
        if (matches != null) {
            for (result in matches) {
                text = result.trimIndent()
                Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
            }
            viewModel.defineEndPoint(text)
        }
    }

    override fun onPartialResults(partialResults: Bundle?) {
        Log.d("PartialResult", "PartialResult")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.d("Event", "Event")
    }

    override fun onSegmentResults(segmentResults: Bundle) {
        super.onSegmentResults(segmentResults)
    }

    override fun onEndOfSegmentedSession() {
        super.onEndOfSegmentedSession()
    }

    private fun getErrorText(error: Int): String {
        var message = ""
        message = when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
            SpeechRecognizer.ERROR_SERVER -> "error from server"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
            else -> "Didn't understand, please try again."
        }
        return message
    }

    private fun talk(){
        if(shouldSpeak){
            speechRecognizer.startListening(speechRecognizerIntent)
        }
        else{
            Toast.makeText(applicationContext, "Permissions not given", Toast.LENGTH_SHORT).show()
            getSpeakPermission()
        }
    }

    companion object{
        private const val permission = 100
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2
    }

}

fun getCurrentLocation(context: Context, onSuccess: (Location) -> Unit) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            MainActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        return
    }
    else{
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            onSuccess(location)
            Toast.makeText(context,location.latitude.toString() + " " +
                    location.longitude.toString(), Toast.LENGTH_LONG ).show()
        }.addOnFailureListener {
            Toast.makeText(context,"Could not get location", Toast.LENGTH_LONG ).show()
        }
    }
}

@Composable
fun DetailsScreen(
    onErrorLoading: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GeoViewModel = viewModel(),
    context: Context,
    onTalk: () -> Unit,
    shouldSpeak: Boolean
) {
    DetailsContent(modifier, context, onTalk, shouldSpeak, viewModel = viewModel)
}

@Composable
fun DetailsContent(
    modifier: Modifier = Modifier,
    context: Context,
    onTalk: () -> Unit,
    shouldSpeak: Boolean,
    viewModel: GeoViewModel = viewModel()
) {
    val route by viewModel.route.collectAsState()

/*
    var mPoints by remember { mutableStateOf(listOf(DataPoint(0.0f, 10.0f), DataPoint(5.0f, 15.0f),
        DataPoint(10.0f, 10.0f), DataPoint(15.0f, 5.0f))) }*/

    Column(modifier = modifier, verticalArrangement = Arrangement.Center) {

        PlotTheme(){
           // SampleLineGraph(listOf(DataPoint(0.0f, 10.0f), DataPoint(5.0f, 15.0f),
              //  DataPoint(10.0f, 10.0f), DataPoint(15.0f, 5.0f)))
            SampleLineGraph(route as List<DataPoint>)
        }

        Spacer(Modifier.height(5.dp))
        CityMapView(context, onTalk = onTalk, shouldSpeak = shouldSpeak, viewModel = viewModel)
    }
}

@Composable
fun SampleLineGraph(line: List<DataPoint>/*, currentPosition : DataPoint?*/) {
    LineGraph(
        plot = LinePlot(
            listOf(
                LinePlot.Line(
                    line,
                    LinePlot.Connection(color = Chartreuse),
                    LinePlot.Intersection(color = LightBlue),
                    LinePlot.Highlight(color = Blue)
                )
                /*
                LinePlot.Line(
                    listOf(currentPosition!!),
                    LinePlot.Connection(color = Chartreuse),
                    LinePlot.Intersection(color = LightBlue),
                    LinePlot.Highlight(color = PalePurple)
                )
                 */
            ),
            grid = LinePlot.Grid(LightBlue, steps = 4),
            isZoomAllowed = true
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Navy),
        onSelection = { xLine, points ->
            // Do whatever you want here
        }
    )
}

@Composable
private fun CityMapView(context: Context,
                        onTalk: () -> Unit,
                        shouldSpeak: Boolean,
                        viewModel: GeoViewModel = viewModel()) {
    // The MapView lifecycle is handled by this composable. As the MapView also needs to be updated
    // with input from Compose UI, those updates are encapsulated into the MapViewContainer
    // composable. In this way, when an update to the MapView happens, this composable won't
    // recompose and the MapView won't need to be recreated.
    var uiSettings by remember { mutableStateOf(MapUiSettings()) }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.SATELLITE))
    }

    var mLocation by remember { mutableStateOf(mutableListOf<Double>(0.0, 0.0)) }

    //var balloonEvent by remember { mutableStateOf(BalloonLayout.MODE) }
    val balloonEvent by viewModel.baloonMode.collectAsState()

    val route by viewModel.route.collectAsState()

    val routeGeometry by viewModel.routeGeometry.collectAsState()

    val routeError by viewModel.routeError.collectAsState()
    val destError by viewModel.destError.collectAsState()
    val elevError by viewModel.elevError.collectAsState()

    val teste by viewModel.teste.collectAsState()

    val distances by viewModel.distances.collectAsState()

    val destination by viewModel.destination.collectAsState()

    var shouldShowBaloon by remember { mutableStateOf(false) }

    var shouldShowPath by remember { mutableStateOf(false) }

    // create and remember a builder of Balloon.
    val builder = rememberBalloonBuilder {
        setArrowSize(0)
        setWidthRatio(1.0f)
        setHeight(120)
        setPadding(15)
        setMarginHorizontal(12)
        setTextSize(15f)
        setCornerRadius(10f)
        setTextColor(Color.DarkGray) // set text color with compose color.
        setBackgroundColor(Color.White)
    }

    Balloon(
        builder = builder,
        balloonContent = {
            when (balloonEvent) {
                BalloonLayout.ADDRESS -> {
                    SpeakingNow(onTalk = onTalk, shouldSpeak)
                }
                BalloonLayout.MODE -> {
                    PickMode(onClick = {
                        viewModel.changeBaloonMode()
                        shouldShowBaloon = !shouldShowBaloon
                        viewModel.getElevation(listOf(mLocation[0], mLocation[1]),  "bicycle")
                        Toast.makeText(context, route.toString(), Toast.LENGTH_LONG).show()
                        shouldShowPath = !shouldShowPath
                    })
                }
            }
        }
    ) { balloonWindow ->
        LaunchedEffect(key1 = shouldShowBaloon) {
            if(shouldShowBaloon){
                balloonWindow.showAsDropDown(140, 180)
            }
            else{
                balloonWindow.dismiss()
            }
        }
    }

    Scaffold(floatingActionButton = {
        GeoFloatingActionButton(
            onClick = { shouldShowBaloon = !shouldShowBaloon }
        )
    }
    ) { padding ->

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            /*
            var cameraPositionState = rememberCameraPositionState {
                position =
                    CameraPosition.fromLatLngZoom(LatLng(mLocation[0], mLocation[1]), 10f)
            }
            */

            LaunchedEffect(Unit) { getCurrentLocation(context) {
                mLocation = mutableListOf(it.latitude, it.longitude)
            } }

            GoogleMap(
                modifier = Modifier.matchParentSize(),
                properties = properties,
                uiSettings = uiSettings,
                cameraPositionState = CameraPositionState(
                    CameraPosition.fromLatLngZoom(LatLng(mLocation[0], mLocation[1]), 11f)
                )
            ) {
                Marker(
                    state = MarkerState(position = LatLng(mLocation[0], mLocation[1])),
                    title = "My Location",
                    snippet = "Here"
                )
                Polyline(
                    points = routeGeometry as List<LatLng>,
                    clickable = true,
                    color = Blue,
                    width = 2.5f,
                    visible = shouldShowPath
                )
            }
            Switch(
                checked = uiSettings.zoomControlsEnabled,
                onCheckedChange = {
                    uiSettings = uiSettings.copy(zoomControlsEnabled = it)
                }
            )

        }

    }

}

@Composable
private fun GeoFloatingActionButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(horizontal = 150.dp, vertical = 16.dp)

    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(45.dp)
            )
        }
    }
}

/**
 * Shows the loading state of the weather.
 */
@Composable
private fun SpeakingNow(onTalk: () -> Unit, shouldSpeak: Boolean) {

    var speaks by remember { mutableStateOf(shouldSpeak) }

    // Creates an `InfiniteTransition` that runs infinite child animation values.
    val infiniteTransition = rememberInfiniteTransition()
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,        targetValue = 1f,
        // `infiniteRepeatable` repeats the specified duration-based `AnimationSpec` infinitely.
        animationSpec = infiniteRepeatable(
            // The `keyframes` animates the value by specifying multiple timestamps.
            animation = keyframes {
                // One iteration is 1000 milliseconds.
                durationMillis = 800
                // 0.7f at the middle of an iteration.
                0.5f at 500
            },
            // When the value finishes animating from 0f to 1f, it repeats by reversing the
            // animation direction.
            repeatMode = RepeatMode.Reverse
        )
    )
    LaunchedEffect(key1 = speaks ){
        if(speaks){
            onTalk()
        }
    }
    Column() {
        Image(
            painterResource(R.drawable.ic_baseline_hearing_24),
            contentDescription = "ADDRESS",
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.Center,
            colorFilter = ColorFilter.tint(Color.Black.copy(alpha = alpha)),
            modifier = Modifier.size(200.dp)
            )
    }

}

@Composable
private fun PickMode(onClick: () -> Unit){
    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            /*modifier = Modifier.fillMaxSize()*/){
            Spacer(modifier = Modifier.width(50.dp))
            ItemGrid(R.drawable.ic_baseline_directions_car_24, onClick = onClick)
            Spacer(modifier = Modifier.height(50.dp))
            ItemGrid(R.drawable.ic_baseline_directions_walk_24, onClick = onClick)
        }
    }
}

@Composable
private fun ItemGrid(asset: Int, onClick: () -> Unit){
    IconButton(onClick = onClick,
        enabled = true,
        content = { Image(
            painterResource(asset),
            contentDescription = "MODE",
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.Center,
        )  },
    )
}

enum class BalloonLayout {
    ADDRESS, MODE
}

/*
@Composable
fun rememberBaloonEventStateHost(initialValue: BalloonLayout): BaloonEventStateHost =
    rememberSaveable(initialValue, saver = BaloonEventStateHost.Saver) {
        BaloonEventStateHost(initialValue)
    }

class BaloonEventStateHost(initialValue: BalloonLayout) {
    var mValue by mutableStateOf(initialValue)

    val changeEvent: Boolean
        get() = mValue == BalloonLayout.ADDRESS

    companion object {
        val Saver: Saver<BaloonEventStateHost, *> = Saver(
            save = { it.mValue },
            restore = {
                BaloonEventStateHost(
                    initialValue = it
                )
            }
        )
    }
}

 */

