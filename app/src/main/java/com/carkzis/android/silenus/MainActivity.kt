package com.carkzis.android.silenus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val localIp = "192.168.0.14"
        // Standard Android Emulator IP: "10.0.2.2"

        if (BuildConfig.DEBUG) {
            Firebase.database.useEmulator(localIp, 9000)
            Firebase.auth.useEmulator(localIp, 9099)
            Firebase.storage.useEmulator(localIp, 9199)
            FirebaseFirestore.getInstance().useEmulator(localIp, 8080)
            FirebaseFunctions.getInstance().useEmulator(localIp, 5001)
        }
    }
}