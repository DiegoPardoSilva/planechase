package com.carpes.planeschase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import com.carpes.planeschase.ui.navigation.AppNavGraph
import com.carpes.planeschase.ui.theme.PlaneschaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaneschaseTheme {
                AppNavGraph()
            }
        }
    }
}
