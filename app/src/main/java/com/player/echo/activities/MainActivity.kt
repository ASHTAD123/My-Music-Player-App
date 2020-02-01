package com.player.echo.activities
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.player.echo.R
import com.player.echo.adapters.NavigationDrawerAdapter
import com.player.echo.fragments.MainScreenFragment
import com.player.echo.fragments.SongPlayingFragment


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    @SuppressLint("StaticFieldLeak")

/** *********************************** VARIABLES *************************************************/
    object Statified {

        // DRAWER LAYOUT OBJECT
        var drawerLayout: DrawerLayout? = null
        var notiManager: NotificationManager? = null
                    }


    private var navigationDrawerIconList: ArrayList<String> = arrayListOf()

    var navDrawerImages = intArrayOf(
        R.drawable.navigation_allsongs,
        R.drawable.navigation_favorites,
        R.drawable.navigation_settings,
        R.drawable.navigation_aboutus
    )


    var trackNotificationBuilder: Notification? = null

/** ********************************* ON CREATE **************************************************/
    override fun onCreate(savedInstanceState: Bundle?) 
   {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /** OBJECT OF TOOLBAR **/
        val toolbar = findViewById<Toolbar>(R.id.toolbar)


        /** SETTING TOOLBAR AS THE DEFAULT TOOLBAR **/
        setSupportActionBar(toolbar)

        /** INITIALIZATION OF DRAWER LAYOUT OBJECT **/
        Statified.drawerLayout = findViewById(R.id.drawer_layout)


        /** ADDING NAMES OF TITLES INTO ARRAY-LIST **/
        navigationDrawerIconList.add(("All Songs"))
        navigationDrawerIconList.add(("Favourites"))
        navigationDrawerIconList.add(("Settings"))
        navigationDrawerIconList.add(("About us"))

        /** OBJECT OF ACTION BAR DRAWER **/
        val toggle = ActionBarDrawerToggle(
            this@MainActivity,
            Statified.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )


        /** SETTING ONCLICK LISTENER **/
        Statified.drawerLayout?.addDrawerListener(toggle)
        toggle.syncState()


        /** OBJECT OF MAIN SCREEN FRAGMENT **/
        val mainScreenFragment = MainScreenFragment()


        /** To make fragment appear on screen ,fragment manager is used to manage fragments **/
        this.supportFragmentManager
            .beginTransaction()
            .add(R.id.details_fragment, mainScreenFragment, " MainScreenFragment ")
            .commit()


        /** OBJECT OF NAV ADAPTER **/
        val navAdapter = NavigationDrawerAdapter(navigationDrawerIconList, navDrawerImages, this)


        /** Notifies the adapter about the data changed & to refresh the list **/
        navAdapter.notifyDataSetChanged()


        /** Declaring the variable for the list inside the navigation drawer **/
        var navRecyclerView = findViewById<RecyclerView>(R.id.navRecyclerView)
        navRecyclerView.layoutManager    = LinearLayoutManager(this)


        /** ANIMATES THE WAY THE ITEMS APPEAR IN RECYCLER-VIEW **/
        navRecyclerView.itemAnimator = DefaultItemAnimator()


        /** Now we set the adapter to our recycler view **/
        navRecyclerView.adapter = navAdapter


        /** Here the number of items present in the recycler view are fixed and won't change any time **/
        navRecyclerView.setHasFixedSize(true)

        val intent = Intent(this@MainActivity, MainActivity::class.java)

        val pIntent = PendingIntent.getActivity(
            this@MainActivity,
            System.currentTimeMillis().toInt(),
            intent,
            0
        )


        trackNotificationBuilder = Notification.Builder(this)
            .setContentTitle("A track is playing in Background")
            .setSmallIcon(R.drawable.echo_logo)
            .setContentIntent(pIntent)
            .setOngoing(true)
            .setAutoCancel(true)
            .build()

        Statified.notiManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
/** ************************************** ON START ***********************************************/
    override fun onStart()
    {
        super.onStart()

        try
        {
            Statified.notiManager?.cancel(2000)
        }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
    }
/** *************************************** ON STOP ***********************************************/
    override fun onStop()
    {
        super.onStop()

        try {
              if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
              {
                 Statified.notiManager?.notify(2000, trackNotificationBuilder)
              }
           }

            catch (e: Exception)
           {
            e.printStackTrace()
            }
    }
    
/** ************************************* ON RESUME ***********************************************/
    override fun onResume()
    {
        super.onResume()
        
        try
        {
            Statified.notiManager?.cancel(2000)
        } 
            catch (e: Exception)
            {
                e.printStackTrace()
            }
    }



}
/**################################### E N D #####################################################*/