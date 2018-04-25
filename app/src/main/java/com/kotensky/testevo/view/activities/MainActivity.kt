package com.kotensky.testevo.view.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.kotensky.testevo.R
import com.kotensky.testevo.application.TestEvoApplication
import com.kotensky.testevo.listeners.OnChangeTitleListener
import com.kotensky.testevo.listeners.OnFragmentInteractionListener
import com.kotensky.testevo.view.fragments.ProductsListFragment
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnFragmentInteractionListener, FragmentManager.OnBackStackChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProductsListFragment())
                .commit()
    }

    override fun addFragment(fragment: Fragment) {
        val fragmentTag = fragment::class.java.simpleName

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, fragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit()
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    override fun onBackStackChanged() {
        val title = (supportFragmentManager.findFragmentById(R.id.fragment_container) as? OnChangeTitleListener)?.getTitle()
        supportActionBar?.title = if (title.isNullOrEmpty()) getString(R.string.app_name) else title
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        TestEvoApplication.applicationComponent.permissionManager()
                .onRequestPermissionsResult(requestCode, grantResults)
    }
}
