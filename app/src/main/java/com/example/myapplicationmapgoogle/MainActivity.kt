package com.example.myapplicationmapgoogle

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplicationmapgoogle.ui.theme.MyApplicationMapGoogleTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.room.Room
import com.example.myapplicationmapgoogle.room.Contact
import com.example.myapplicationmapgoogle.room.ContactDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationMapGoogleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //  Greeting("Android")
                    MyUI(this@MainActivity)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyUI(mainActivity: MainActivity) {
    val database = Room.databaseBuilder(mainActivity,ContactDatabase::class.java, "DataClass")
        .allowMainThreadQueries()
        .build()
    val markerData = remember {
        database.contactDao().getContact().map { contact ->
            LatLng(contact.latitude, contact.longitude)

        }

    }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf(0) }
    var relation by remember { mutableStateOf("") }
    var latitudevalue by remember { mutableStateOf(0.0) }
    var longitudevalue by remember { mutableStateOf(0.0) }
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    // Check for location permission
    val permissionGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!permissionGranted) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
        return
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            MapView(context).apply {
                onCreate(null)
                getMapAsync { googleMap ->
                    googleMap.uiSettings.isZoomControlsEnabled = true



                    markerData.forEach { latLng ->
                        googleMap.addMarker(MarkerOptions().position(latLng).title("   "))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

                    }

                    // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10f))
                    googleMap.setOnMapClickListener { latLng ->
                        openDialog.value = true
                        val latitude = latLng.latitude
                        val longitude = latLng.longitude
                        latitudevalue=latLng.latitude
                        longitudevalue=latLng.longitude

                        Log.d("VAlueeeee","${latitude}")
                        Log.d("VAlueeeee","${longitude}")
                        val selectedLocation = LatLng(latitude, longitude)

                        googleMap.clear() // Clear existing markers
                        googleMap.addMarker(MarkerOptions().position(selectedLocation).title("Selected Location: ($latitude, $longitude)"))


                    }

                }
            }
        }
    )
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false }, // Dismiss the dialog if needed
            title = { Text(text = "Add Details") },
            confirmButton = {
                //  val database = DatabaseProvider.getDatabase(context)
                Button(
                    onClick = {
                        val person = Contact(
                            name = name,
                            age = age,
                            relation = relation,
                            address = address,
                            latitude = latitudevalue,
                            longitude = longitudevalue
                        )
                        GlobalScope.launch {


                            // database.getdatadao().insertDataClass(person)
                            database.contactDao().insertDataClass(person)

                        }
                        Toast.makeText(context, "Marked Saved", Toast.LENGTH_SHORT).show()

                        //  onSave(name, age, relation, address)
                        mainActivity.recreate()
                        openDialog.value = false

                        // Close the dialog after saving
                    },
                ) {
                    Text(text = "Save")
                }
            },
            text = {
                Column {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") }
                    )
                    TextField(
                        value = age.toString(),
                        onValueChange = { age = it.toIntOrNull() ?: 0 },
                        label = { Text("Age") }
                    )
                    TextField(
                        value = relation,
                        onValueChange = { relation = it },
                        label = { Text("Relation") }
                    )
                    TextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address") }
                    )
                }
            },
        )
    }
}

private const val PERMISSION_REQUEST_CODE = 123
