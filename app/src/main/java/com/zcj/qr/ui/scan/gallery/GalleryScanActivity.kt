package com.zcj.qr.ui.scan.gallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.zcj.qr.R

class GalleryScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.container, GalleryScanFragment())
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }
}
