package com.simple.travelsharing.view.driver

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.simple.travelsharing.R
import com.simple.travelsharing.view.driver.FavoritesDriverFragment
import com.simple.travelsharing.view.driver.HomeDriverFragment
import com.simple.travelsharing.view.driver.ProfileDriverFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object{
        var jobDriverImage = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        initOnClickBottomBar()
        initRequestPermission()
        setFragment(HomeDriverFragment.newInstance())
    }

    private fun initRequestPermission() {
        val permissions = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        ActivityCompat.requestPermissions(this, permissions,0)
    }


    private fun initOnClickBottomBar() {
        bottom_navigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    setFragment(HomeDriverFragment.newInstance())
                }
                R.id.navigation_flavorate -> {
                    setFragment(FavoritesDriverFragment.newInstance())
                }
                R.id.navigation_profile -> {
                    setFragment(ProfileDriverFragment.newInstance("",""))
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, fragment)
        transaction.commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}