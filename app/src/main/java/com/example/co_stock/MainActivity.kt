package com.example.co_stock

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.navigation.findNavController
import com.google.android.gms.common.api.internal.ApiKey

class MainActivity : AppCompatActivity() {

    val viewModel: UserViewModel by viewModels<UserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //set Firebase manually
        val options = FirebaseOptions.Builder()
            .setApplicationId("com.example.co_stock")
            .setApiKey(getString(R.string.firebase_api_key))
            .setDatabaseUrl(getString(R.string.firebase_database)).build()
        val app = FirebaseApp.initializeApp(application, options, "https://stockapp-e3a44")

        viewModel.firebase.value = FirebaseDatabase.getInstance(app).reference
        viewModel.firebase.value?.addValueEventListener(viewModel)
        }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.home_menu ->{
                findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_homeFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}