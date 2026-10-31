package com.frenchfrie.singasong

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import com.frenchfrie.singasong.practice.PracticeViewModel
import com.frenchfrie.singasong.ui.screens.LibraryScreen
import com.frenchfrie.singasong.ui.theme.SingASongTheme

class MainActivity : ComponentActivity() {
    private val practice by lazy { ViewModelProvider(this)[PracticeViewModel::class.java] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SingASongTheme {
                val state by practice.state.collectAsStateWithLifecycle()
                LibraryScreen(state, practice)
            }
        }
    }

    override fun onStop() {
        if (!isChangingConfigurations) practice.pause()
        super.onStop()
    }
}
