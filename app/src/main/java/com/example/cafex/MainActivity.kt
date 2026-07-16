package com.example.cafex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cafex.ui.CafeXApp
import com.example.cafex.ui.theme.CafeXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CafeXApplication

        setContent {
            CafeXTheme {
                CafeXApp(viewModelFactory = app.viewModelFactory)
            }
        }
    }
}
