package com.example.movie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NetflixTheme {
                androidx.compose.material3.Scaffold { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        MovieNavHost()
                    }
                }
            }
        }
    }
}
