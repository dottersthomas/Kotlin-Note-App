package io.github.thomas.dotters.noteapp

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.os.Build
import android.app.Activity
import android.graphics.Color
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.View
import io.paperdb.Paper
import android.view.WindowManager
import io.github.thomas.dotters.noteapp.Adapters.Note
import io.github.thomas.dotters.noteapp.Adapters.NoteRecyclerViewAdapter
import kotlinx.android.synthetic.main.fragment_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    public var shouldRefresh = false
    var toggle:ActionBarDrawerToggle ?= null

    var currentFragment:String = "All Notes"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

//        val fab = findViewById(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val w = window // in Activity's onCreate() for instance
//            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
//        }


        Paper.init(this)




        val drawer = findViewById(R.id.drawer_layout) as? DrawerLayout
        toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawer!!.addDrawerListener(toggle!!)
        toggle!!.syncState()
        toggle!!.toolbarNavigationClickListener = View.OnClickListener {
            onBackPressed()
        };
        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)




    }


    fun loadDrawerItems(){
        doAsync {
            //var result = runLongTask()
            val data = mutableListOf<String>()
            val keys =  Paper.book("tags").allKeys
            for( i in 0..keys.size - 1) {

                val tag = Paper.book("tags").read<String>(keys[i])
                data.add(tag)

            }
            uiThread {
                //(notes_list.adapter  as NoteRecyclerViewAdapter).addNotes(tag)
                val navigationView = findViewById(R.id.nav_view) as NavigationView
                navigationView.menu.clear()

                navigationView.menu.add("All Notes")
                for (i in 0..data.size - 1)
                    navigationView.menu.add(data[i])


            }
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
//            R.id.nav_camera -> {
//                // Handle the camera action
//            }
//            R.id.nav_gallery -> {
//
//            }
//            R.id.nav_slideshow -> {
//
//            }
//            R.id.nav_manage -> {
//
//            }
//            R.id.nav_share -> {
//
//            }
//            R.id.nav_send -> {
//
//            }
        }

        if(item.title == "All Notes"){

        }


        val drawer = findViewById(R.id.drawer_layout) as? DrawerLayout
        drawer!!.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as? DrawerLayout
        if (drawer!!.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        }  else {
            val backStackCount = supportFragmentManager.backStackEntryCount

            if (backStackCount > 0) {
                supportFragmentManager.popBackStack()
                // Change to hamburger icon if at bottom of stack


            } else {
                super.onBackPressed()
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        if(id == android.R.id.home) {
            val drawer = findViewById(R.id.drawer_layout) as? DrawerLayout
            drawer!!.openDrawer(GravityCompat.START)
            return true
        }

        return super.onOptionsItemSelected(item)
    }


    fun openFragmentWithTag(tag:String){
        if(tag == currentFragment)
            return;
        else{
            currentFragment = tag
            replaceFragmentSafely(MainActivityFragment.newInstance(tag), "tag", R.id.fragment, true)
        }
    }
}
