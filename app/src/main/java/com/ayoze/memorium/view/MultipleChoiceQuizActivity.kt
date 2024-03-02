package com.ayoze.memorium.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.ayoze.memorium.databinding.ActivityMultipleChoiceQuizBinding

class MultipleChoiceQuizActivity : ComponentActivity() {

    private lateinit var binding: ActivityMultipleChoiceQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMultipleChoiceQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()
    }

    private fun setUp() {
        binding.ivQuizGoBack.setOnClickListener {
            onBackPressed()
        }
    }
}