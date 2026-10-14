package com.zcj.qr.ui.scan.camera

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.zcj.qr.R

class CameraScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, CameraScanFragment())
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
