package com.player.echo.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import com.player.echo.R
import com.player.echo.Songs
import com.player.echo.adapters.MainScreenAdapter
import java.util.*
import kotlin.collections.ArrayList


class MainScreenFragment : Fragment()
{

    /** Initialization of views with this fragment **/
    var getSongsList        : ArrayList<Songs>? = null

    var playPauseButton     : ImageButton?      = null
    var songTitle           : TextView?         = null

    /** Title which will be displayed below now playing text  **/
    var visibleLayout : RelativeLayout? = null

    /** Layout which contains Recycler View & the bottom bar  **/
    var noSongs       : RelativeLayout? = null


    /** View which would get displayed when there are no songs **/
    var recyclerView  : RecyclerView? = null
    var myActivity    : Activity?     = null


    /** Object of Main Screen Adapter **/
    var mainScreenAdapter: MainScreenAdapter? = null


    var trackPosition  :Int = 0

    @SuppressLint("StaticFieldLeak")
    object Statified{
        var  mediaPlayer        : MediaPlayer? = null
        var nowPlayingBottomBar : RelativeLayout?   = null
    }
/**  ********************************************************************************************* */


    /** This function is called when fragment's activity has been created & fragment's view hierarchy is instantiated **/
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        getSongsList = getSongsFromDevice()
        /** Object of SongList **/

        val prefs        = activity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)
        val action_sort_ascending  = prefs?.getString("action_sort_ascending", "true")
        val action_sort_recent     = prefs?.getString("action_sort_recent", "false")

        if (getSongsList == null)
        {
            visibleLayout?.visibility = View.INVISIBLE
            noSongs?.visibility = View.VISIBLE

            Toast.makeText(activity, "No Songs", Toast.LENGTH_SHORT).show()
        }
         else {
            mainScreenAdapter = MainScreenAdapter(getSongsList as ArrayList<Songs>, myActivity as Context)

            /** SETTING UP LAYOUT MANAGER **/
            val mLayoutManager = LinearLayoutManager(myActivity)
            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = mainScreenAdapter
        }

        if (getSongsList != null)
        {

            if (action_sort_ascending!!.equals("true", ignoreCase = true))
            {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
                mainScreenAdapter?.notifyDataSetChanged()
            }
                else if (action_sort_recent!!.equals("true", true))
                {
                    Collections.sort(getSongsList, Songs.Statified.dateComparator)
                    mainScreenAdapter?.notifyDataSetChanged()
                }
        }
        bottomBar_setup()
        bottomBarClickHanlder()
    }

/************************************************************************************************* */
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        /** Inflate the layout for this fragment ,all of the ids are accessed through this object */
        val view = inflater.inflate(R.layout.fragment_main_screen, container, false)

        setHasOptionsMenu(true)
        activity?.title = " All Songs"

        visibleLayout                 = view?.findViewById(R.id.visibleLayout)
        noSongs                       = view?.findViewById(R.id.noSongs)
        Statified.nowPlayingBottomBar = view?.findViewById(R.id.hiddenBarMainScreen)
        songTitle                     = view?.findViewById(R.id.songTitle1)
        playPauseButton               = view?.findViewById(R.id.PlayPauseBtn)
        recyclerView                  = view?.findViewById(R.id.content_Main)

        return view
    }

/** ********************************************************************************************** */

           /** This method is called when the fragment is attached to the activity **/


    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        myActivity = context as Activity
    }

    override fun onAttach(activity: Activity?)
    {
        super.onAttach(activity)
        myActivity = activity
    }

/** ********************************************************************************************* */

                     /** Function to fetch songs from the handset **/


    fun getSongsFromDevice(): ArrayList<Songs>
    {
        /* OBJECT OF ARRAY LIST */
        var arrayList = ArrayList<Songs>()

        /** IF DB IS ALREADY SET UP THEN THIS () IS USED TO COMMUNICATE WITH THE PROVIDER **/
        var contentResolver = myActivity?.contentResolver

        /** USED TO IDENTIFY SONG i.e a resource **/
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst()) {
            val songId        = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle     = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist    = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData      = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songDateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (songCursor.moveToNext())
            {
                var currentId       = songCursor.getLong(songId)
                var currentTitle  = songCursor.getString(songTitle)
                var currentArtist = songCursor.getString(songArtist)
                var currentData   = songCursor.getString(songData)
                var currentDate    = songCursor.getLong(songDateIndex)

                arrayList.add(
                    Songs(
                        currentId,
                        currentTitle,
                        currentArtist,
                        currentData,
                        currentDate
                    )
                )
            }

        }
        return arrayList
    }
/** ********************************************************************************************* */
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        menu?.clear()
        inflater?.inflate(R.menu.main, menu)
        return
    }

/** ********************************************************************************************** */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        val switcher = item?.itemId

        if (switcher == R.id.action_sort_ascending)
        {
            val editiorOne =
                myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()

            editiorOne?.putString("action_sort_ascending", "true")
            editiorOne?.putString("action_sort_recent",   "false")
            editiorOne?.apply()

            if (getSongsList != null) {
                Collections.sort(getSongsList, Songs.Statified.nameComparator)
            }


            mainScreenAdapter?.notifyDataSetChanged()
            return false
        }

              else if (switcher == R.id.action_sort_recent)
            {
                val editiorTwo =
                    myActivity?.getSharedPreferences("action_sort", Context.MODE_PRIVATE)?.edit()

                editiorTwo?.putString("action_sort_recent", "true")
                editiorTwo?.putString("action_sort_ascending", "false")
                editiorTwo?.apply()

                if (getSongsList != null) {
                    Collections.sort(getSongsList, Songs.Statified.dateComparator)
                }

                mainScreenAdapter?.notifyDataSetChanged()

                return false
            }
           return super.onOptionsItemSelected(item)
    }
/** ********************************************************************************************* */
    override fun onPrepareOptionsMenu(menu: Menu?)
   {
        super.onPrepareOptionsMenu(menu)

        val item: MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible = true
    }
/** ********************************************************************************************* */
    fun bottomBar_setup()
    {
        Statified.nowPlayingBottomBar?.isClickable = true
        bottomBarClickHanlder()

        try {

            songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle

            SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener{

                songTitle?.text = (SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
            }

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                Statified.nowPlayingBottomBar?.visibility = View.VISIBLE
            } else
             {
                 Statified.nowPlayingBottomBar?.visibility = View.INVISIBLE
              }

        }
            catch(e: Exception)
            {
                e.printStackTrace()
            }
    }
/** ********************************************************************************************* */
        fun bottomBarClickHanlder()
        {

            Statified.nowPlayingBottomBar?.setOnClickListener {



                Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
                var songPlayingFragment = SongPlayingFragment()


                var args = Bundle()
                args.putString("songArtist",            SongPlayingFragment.Statified.currentSongHelper?.songArtist)
                args.putString("songPath",              SongPlayingFragment.Statified.currentSongHelper?.songPath)
                args.putString("songTitle",             SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                args.putInt("songId",                   SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
                args.putInt("songPosition",             SongPlayingFragment.Statified.currentSongHelper?.currentPos as Int)
                args.putParcelableArrayList("songData", SongPlayingFragment.Statified.fetchSongs)


                args.putString("MainBottomBar", "success2")


                songPlayingFragment.arguments = args

                fragmentManager?.beginTransaction()
                    ?.replace(R.id.details_fragment, songPlayingFragment)
                    ?.addToBackStack("MainScreenFragment1")
                    ?.commit()
            }


        playPauseButton?.setOnClickListener {

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                 trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition!!
                 SongPlayingFragment.Statified.currentSongHelper?.isPlaying = false
                 playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
             else {

                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                SongPlayingFragment.Statified.currentSongHelper?.isPlaying = true
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
            }
        }

    }


}

/************************************************************************************************* */




