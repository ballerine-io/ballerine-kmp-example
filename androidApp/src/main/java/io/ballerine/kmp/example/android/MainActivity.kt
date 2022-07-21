package io.ballerine.kmp.example.android

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import io.ballerine.kmp.example.BallerineStorage

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        setText()
        button.setOnClickListener {
            startUserRegistrationFlow()
        }
    }

    private fun setText() {
        val textResult = findViewById<TextView>(R.id.textResult)
        textResult.text = BallerineStorage.previouslyStoredKey() ?: "No secret available"
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            setText()
        }
    }

    private fun startUserRegistrationFlow(){
        val intent = Intent(this@MainActivity, BallerineKYCFlow::class.java)
        resultLauncher.launch(intent)
    }


}
