package com.example.epicture

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        var access_token : String? = null
        var userName : String? = null
        var client_id : String = "a7f7808a97975da"
        var searchs : String? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(uOnNavigationItemSelectedListener)
        switchFragment(HomeFragment())
    }

    private val uOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                switchFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.favorite -> {
                switchFragment(FavFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.profile -> {
                switchFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.auth -> {
                if (access_token == null) {
                    switchFragment(AuthFragment())
                } else {
                    Toast.makeText(this@MainActivity, "You're already loged in $userName", Toast.LENGTH_SHORT).show()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchItem: MenuItem = menu.findItem(R.id.searchbtn)
        val searchView = searchItem.actionView as android.support.v7.widget.SearchView
        searchView.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    searchs = p0
                    Toast.makeText(this@MainActivity, searchs, Toast.LENGTH_SHORT).show()
                    switchFragment(SearchFragment())
                }
                return true
            }
        })
        return true
    }

    fun switchFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}
