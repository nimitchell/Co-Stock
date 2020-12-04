package com.example.co_stock

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.google.android.gms.common.api.internal.ApiKey
import com.google.firebase.storage.FirebaseStorage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    val viewModel: UserViewModel by viewModels<UserViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set Firebase manually
        val options = FirebaseOptions.Builder()
            .setApplicationId("com.example.co_stock")
            .setApiKey(getString(R.string.firebase_api_key))
            .setDatabaseUrl(getString(R.string.firebase_database)).build()
        val app = FirebaseApp.initializeApp(application, options, "https://co-stock")

        viewModel.firebase.value = FirebaseDatabase.getInstance(app).reference
        viewModel.firebase.value?.addValueEventListener(viewModel)
        viewModel.apiManager.value = APIManager(viewModel, getString(R.string.stockmarket_api_key))
        viewModel.profilePic.value = BitmapFactory.decodeResource(resources, R.drawable.round_portrait_white_48dp)


        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                100
            )
        }
    }

    fun checkPermission(): Boolean {
        val fineLocPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return fineLocPermission == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() ||
            grantResults[0] == PackageManager.PERMISSION_DENIED
        ) {
            finish()
            exitProcess(0)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.home_menu ->{
                if (viewModel.currentUser.value != null){
                    findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_homeFragment)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}