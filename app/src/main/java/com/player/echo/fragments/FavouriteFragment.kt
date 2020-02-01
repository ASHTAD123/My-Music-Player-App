package com.player.echo.fragments


import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.player.echo.R
import com.player.echo.Songs
import com.player.echo.adapters.FavouriteAdapter
import com.player.echo.databases.EchoDatabase

/**
 * A simple [Fragment] subclass.
 */
class FavouriteFragment : Fragment()
{
    var myActivity          :Activity?         = null
    var nowBottomPlaying    :RelativeLayout?   = null
    var recyclerView        :RecyclerView?     = null

    var songTitle           :TextView?         = null
    var noFavourites        :TextView?         = null

    var playPauseBtn        :ImageButton?      = null

    var trackPosition       :Int?               = 0
    var favouriteContent    :EchoDatabase?     = null

    var refreshList         :ArrayList<Songs>? = null
    var getSongList         :ArrayList<Songs>? = null
    var getListFromDb       :ArrayList<Songs>? = null

    object Statified
    {
        var mediaPlayer :MediaPlayer? = null
    }

/*************************************** START *************************************************** */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
       val view =   inflater.inflate(R.layout.fragment_favourite, container, false)

        activity?.title =" Favourites "
        favouriteContent = EchoDatabase(myActivity)

        /** Initialization of views **/
        noFavourites      = view?.findViewById(R.id.NoFavourites)
        nowBottomPlaying  = view.findViewById(R.id.hiddenBarFavScreen)
        songTitle         = view.findViewById(R.id.songTitleFav)
        playPauseBtn      = view.findViewById(R.id.PlayPauseBtn)
        recyclerView      = view.findViewById(R.id.favRecycler)

        return  view
    }
/************************************************************************************************ */
    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        myActivity = context as  Activity
    }

    override fun onAttach(activity: Activity?)
    {
        super.onAttach(activity)
        myActivity = activity
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        myActivity = context as  Activity
    }

/************************************************************************************************* */
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        getSongList = getSongsFromDevice()

        if(getSongList == null)
        {
            recyclerView?.visibility = View.INVISIBLE
            noFavourites?.visibility = View.VISIBLE
        } else
        {
            /** Object **/
            var favouriteAdapter = FavouriteAdapter(getSongList as ArrayList<Songs>,myActivity as Context)
            var mLayoutManager  = LinearLayoutManager(activity)

            recyclerView?.layoutManager = mLayoutManager
            recyclerView?.itemAnimator  = DefaultItemAnimator()
            recyclerView?.adapter       = favouriteAdapter
            recyclerView?.setHasFixedSize(true)
        }

            favouriteContent = EchoDatabase(myActivity)
            displayFavouritesbySearching()
            bottomBarSetup()
        }

    override fun onPrepareOptionsMenu(menu: Menu?)
    {
        super.onPrepareOptionsMenu(menu)
        val item  = menu?.findItem(R.id.action_sort)
        item?.isVisible = false
    }

/************************************************************************************************* */
    fun getSongsFromDevice(): ArrayList<Songs>?
    {
        var arrayList = java.util.ArrayList<Songs>()

        /** IF DB IS ALREADY SET UP THEN THIS () IS USED TO COMMUNICATION WITH THE PROVIDER **/
        var contentResolver = myActivity?.contentResolver

        /** USED TO IDENTIFY SONG i.e a resource **/
        var songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        var songCursor = contentResolver?.query(songUri, null, null, null, null)

        if (songCursor != null && songCursor.moveToFirst() )
        {
            val songId        = songCursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val songTitle     = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val songArtist    = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val songData      = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA)
            val songDateIndex = songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while ( songCursor.moveToNext() )
            {
                var currentId      = songCursor.getLong(songId)
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
        else{
            return null
        }
        return arrayList
    }


/************************************************************************************************* */
    fun bottomBarClickHanlder()
      {
          nowBottomPlaying?.setOnClickListener {

              Statified.mediaPlayer = SongPlayingFragment.Statified.mediaPlayer
              val songPlayingFragment = SongPlayingFragment()

            var args = Bundle()
            args.putString("songArtist",    SongPlayingFragment.Statified.currentSongHelper?.songArtist)
            args.putString("songPath",      SongPlayingFragment.Statified.currentSongHelper?.songPath)
            args.putString("songTitle",     SongPlayingFragment.Statified.currentSongHelper?.songTitle)
            args.putInt   ("songId",        SongPlayingFragment.Statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",     SongPlayingFragment.Statified.currentSongHelper?.currentPos as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.Statified.fetchSongs)


            args.putString("FavBottomBar" ,"success")

           songPlayingFragment.arguments = args

            fragmentManager?.beginTransaction()
                ?.replace(R.id.details_fragment,songPlayingFragment)
                ?.addToBackStack("SongPlayingFragment")
                ?.commit()

    }
/************************************************************************************************* */
        playPauseBtn?.setOnClickListener {

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                SongPlayingFragment.Statified.mediaPlayer?.pause()
                trackPosition = SongPlayingFragment.Statified.mediaPlayer?.currentPosition as Int
                playPauseBtn?.setBackgroundResource(R.drawable.play_icon)
            } else
              {
                SongPlayingFragment.Statified.mediaPlayer?.seekTo(trackPosition!!)
                SongPlayingFragment.Statified.mediaPlayer?.start()
                playPauseBtn?.setBackgroundResource(R.drawable.pause_icon)
              }
        }

    }
/************************************************************************************************* */
    fun displayFavouritesbySearching()
    {
        if(favouriteContent?.checkSize() as Int > 0)
        {
             refreshList = ArrayList<Songs>()
            getListFromDb = favouriteContent?.queryList()

            val fetchListFromDevice = getSongsFromDevice()

            if(fetchListFromDevice !==null)
            {

                for(i in 0 until fetchListFromDevice.size -1 )
                {
                     for(j in 0 until getListFromDb?.size as Int)
                     {

                         if( getListFromDb!![j].songID === fetchListFromDevice[i].songID  )
                         {
                             refreshList?.add((getListFromDb as ArrayList<Songs>)[j] )
                         }
                         else{

                            }
                     }
                }

            } else{

                 }

            if (refreshList == null)
            {
                recyclerView?.visibility  = View.INVISIBLE
                noFavourites?.visibility  = View.VISIBLE

            }

                else{
                    var favAdapter  = FavouriteAdapter(refreshList as ArrayList<Songs> ,myActivity as Context)

                    var mlayoutManager = LinearLayoutManager(activity)

                    recyclerView?.layoutManager = mlayoutManager
                    recyclerView?.itemAnimator = DefaultItemAnimator()
                    recyclerView?.adapter      =favAdapter
                    recyclerView?.setHasFixedSize(true)
                }
        }

                else{
                    recyclerView?.visibility  = View.INVISIBLE
                    noFavourites?.visibility  = View.VISIBLE
                }



    }

/************************************************************************************************ */
     fun bottomBarSetup()
   {
     nowBottomPlaying?.isClickable = false
     bottomBarClickHanlder()

       try {

              songTitle?.text = SongPlayingFragment.Statified.currentSongHelper?.songTitle

               SongPlayingFragment.Statified.mediaPlayer?.setOnCompletionListener{

               songTitle?.text = (SongPlayingFragment.Statified.currentSongHelper?.songTitle)
                SongPlayingFragment.Staticated.onSongComplete()
           }

            if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
            {
                nowBottomPlaying?.visibility = View.VISIBLE
            } else
              {
               nowBottomPlaying?.visibility = View.INVISIBLE
              }

    }
       catch(e: Exception)
      {
        e.printStackTrace()
       }
  }


}