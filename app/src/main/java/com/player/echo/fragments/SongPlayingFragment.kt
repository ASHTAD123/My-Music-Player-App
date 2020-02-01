package com.player.echo.fragments
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.player.echo.CurrentSongHelper
import com.player.echo.R
import com.player.echo.Songs
import com.player.echo.databases.EchoDatabase
import com.player.echo.fragments.SongPlayingFragment.Staticated.onSongComplete
import com.player.echo.fragments.SongPlayingFragment.Staticated.playNext
import com.player.echo.fragments.SongPlayingFragment.Staticated.processInformation
import com.player.echo.fragments.SongPlayingFragment.Staticated.updateTextViews
import com.player.echo.fragments.SongPlayingFragment.Statified.CurrentPosition
import com.player.echo.fragments.SongPlayingFragment.Statified.audioVisualization
import com.player.echo.fragments.SongPlayingFragment.Statified.currentSongHelper
import com.player.echo.fragments.SongPlayingFragment.Statified.endTimeText
import com.player.echo.fragments.SongPlayingFragment.Statified.fab
import com.player.echo.fragments.SongPlayingFragment.Statified.favoriteContent
import com.player.echo.fragments.SongPlayingFragment.Statified.fetchSongs
import com.player.echo.fragments.SongPlayingFragment.Statified.glView
import com.player.echo.fragments.SongPlayingFragment.Statified.loopImageButton
import com.player.echo.fragments.SongPlayingFragment.Statified.myActivity
import com.player.echo.fragments.SongPlayingFragment.Statified.nextImageButton
import com.player.echo.fragments.SongPlayingFragment.Statified.playPauseImageButton
import com.player.echo.fragments.SongPlayingFragment.Statified.previousImageButton
import com.player.echo.fragments.SongPlayingFragment.Statified.seekBar
import com.player.echo.fragments.SongPlayingFragment.Statified.shuffleImageButton
import com.player.echo.fragments.SongPlayingFragment.Statified.songArtistView


import com.player.echo.fragments.SongPlayingFragment.Statified.songTitleView
import com.player.echo.fragments.SongPlayingFragment.Statified.startTimeText
import com.player.echo.fragments.SongPlayingFragment.Statified.updateSongTime
import java.util.* import java.util.concurrent.TimeUnit
import kotlin.math.sqrt

@Suppress("DEPRECATION")
class SongPlayingFragment : Fragment() {

    @SuppressLint("StaticFieldLeak")
    object Statified
    {
        var myActivity    : Activity? = null
        var seekBar       : SeekBar? = null
        var mediaPlayer   : MediaPlayer? = null

        var startTimeText : TextView? = null
        var endTimeText   : TextView? = null
        var songArtistView: TextView? = null
        var songTitleView : TextView? = null


        var playPauseImageButton: ImageButton? = null
        var previousImageButton : ImageButton? = null
        var nextImageButton     : ImageButton? = null
        var loopImageButton     : ImageButton? = null
        var shuffleImageButton  : ImageButton? = null
        var fab                 : ImageButton? = null

        var MY_PREFS_NAME = "ShakeFeature"


        var CurrentPosition  : Int = 0

        var fetchSongs       : ArrayList<Songs>?  = null
        var currentSongHelper: CurrentSongHelper? = null

        var audioVisualization: AudioVisualization?       = null
        var glView            : GLAudioVisualizationView? = null
        var favoriteContent   : EchoDatabase?             = null

        var mSensorManager : SensorManager?       = null
        var mSensorListener: SensorEventListener? = null

        /* Variable used to update Song Time */
        var updateSongTime = object : Runnable
        {
            override fun run()
            {
                    val getCurrent = mediaPlayer?.currentPosition
                    startTimeText?.text = String.format("%d:%d",

                    TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),
                    TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent.toLong())
                    ))

                seekBar?.progress = getCurrent.toInt()

                /** Helps to communicate with the Main thread */
                Handler().postDelayed(this, 1000)

            }
        }

    }

    object Staticated
    {
        var MY_PREFS_SHUFFLE = "Shuffle feature"
        var MY_PREFS_LOOP    = "Loop feature"

        /** Function SongComplete **/

        fun onSongComplete()
        {
            if (currentSongHelper?.isShuffle as Boolean)
            {
                playNext("PlayNextLikeNormalShuffle")
                currentSongHelper?.isPlaying = true
            } else
                {
                    if (currentSongHelper?.isLoop as Boolean)
                    {
                        currentSongHelper?.isPlaying  = true
                        var nextSong          = fetchSongs?.get(CurrentPosition)
                        currentSongHelper?.currentPos = CurrentPosition

                        currentSongHelper?.songPath   = nextSong?.songData
                        currentSongHelper?.songTitle  = nextSong?.songTitle
                        currentSongHelper?.songArtist = nextSong?.artist
                        currentSongHelper?.songId     = nextSong?.songID as Long

                        updateTextViews(currentSongHelper?.songTitle as String,
                                        currentSongHelper?.songArtist as String
                        )

                        Statified.mediaPlayer?.reset()

                        try {
                            Statified.mediaPlayer?.setDataSource(myActivity as Context, Uri.parse(currentSongHelper?.songPath))
                            Statified.mediaPlayer?.prepare()
                            Statified.mediaPlayer?.start()
                            processInformation(Statified.mediaPlayer as MediaPlayer)
                          }
                        catch (e: Exception)
                           {
                               e.printStackTrace()
                           }

                      } else {
                              playNext("PlayNextNormal")
                             currentSongHelper?.isPlaying = true
                             }
            }

            if (favoriteContent?.checkIfIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
            } else
             {
                 fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))

              }
        }

/************************************ FUNCTION ENDS HERE *******************************************/
        fun updateTextViews(songTitle: String, songArtist: String)
      {
            var songTitleUpdated  = songTitle
            var songArtistUpdated = songArtist

            if(songTitle.equals("<unknwon>",true))
            {
                songTitleUpdated = "unknown"
            }

           if(songArtist.equals("<unknwon>",true))
           {
              songArtistUpdated = "unknown"
           }
            songTitleView?.text  = songTitleUpdated
            songArtistView?.text = songArtistUpdated
        }

/*********************************** FUNCTION ENDS HERE *******************************************/

        fun processInformation(mediaPlayer: MediaPlayer)
      {
            val finalTime = mediaPlayer.duration
            val startTime = mediaPlayer.currentPosition
            seekBar?.max = finalTime

            /* START TIME */
            startTimeText?.text = String.format("%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(startTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime.toLong()))
            )
             /* END TIME */
            endTimeText?.text = String.format("%d: %d",
                TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()),
                TimeUnit.MILLISECONDS.toSeconds(finalTime.toLong()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime.toLong()))
            )

            seekBar?.progress = startTime
            Handler().postDelayed(updateSongTime, 1000)
        }

/********************************** FUNCTION ENDS HERE ********************************************/

/**  FUNCTION USED TO PLAY THE NEXT SONG */
        fun playNext(check: String)
        {
            /** CONDITION FOR SHUFFLE BUTTON */
            if (check.equals("PlayNextNormal", true))
            {
                CurrentPosition += 1
            }
              else if (check.equals("PlayNextLikeNormalShuffle", true))
              {
                var randomObject = Random()
                var randomPosition = randomObject.nextInt(fetchSongs?.size?.plus(1) as Int)
                  CurrentPosition= randomPosition
               }

            if (CurrentPosition == fetchSongs?.size)
            {
                CurrentPosition = 0
            }

            /* Disables loop when next button is clicked */
            currentSongHelper?.isLoop = false


            var nextSong = fetchSongs?.get(CurrentPosition)

            currentSongHelper?.songTitle   = nextSong?.songTitle
            currentSongHelper?.songPath    = nextSong?.songData
            currentSongHelper?.currentPos  = CurrentPosition
            currentSongHelper?.songArtist  = nextSong?.artist
            currentSongHelper?.songId      = nextSong?.songID as Long

            updateTextViews(currentSongHelper?.songTitle as String, currentSongHelper?.songArtist as String)
            Statified.mediaPlayer?.reset()

            try
            {   Statified.mediaPlayer?.setDataSource(myActivity as Context,Uri.parse(currentSongHelper?.songPath))
                Statified.mediaPlayer?.prepare()
                Statified.mediaPlayer?.start()
                processInformation(Statified.mediaPlayer as MediaPlayer)

            } catch (e: Exception)
              {
                  e.printStackTrace()
              }


            if (favoriteContent?.checkIfIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
            } else
            {

                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))

            }

        }

/*********************************** FUNCTION ENDS HERE *************f*******************************/

    }  /** STATICATED BLOCK ENDS HERE */

    /* SENSOR VARIABLES */
    var mAccelaration       : Float = 0f
    var mAccelarationCurrent: Float = 0f
    var mAccelarationLast   : Float = 0f

/************************************** OBJECT BLOCK STATICATED END ************************************/


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?

{
        val view = inflater.inflate(R.layout.fragment_song_playing, container, false)
        setHasOptionsMenu(true)

       activity?.title = " Now Playing "

       /* Linking views to their ids */
        seekBar              = view?.findViewById(R.id.seekbar)
        startTimeText        = view?.findViewById(R.id.startTime)
        endTimeText          = view?.findViewById(R.id.EndTime)
        playPauseImageButton = view?.findViewById(R.id.PlayPauseBtn)
        nextImageButton      = view?.findViewById(R.id.nextBtn)
        previousImageButton  = view?.findViewById(R.id.previousBtn)
        loopImageButton      = view?.findViewById(R.id.loopBtn)
        shuffleImageButton   = view?.findViewById(R.id.shuffleBtn)
        songTitleView        = view?.findViewById(R.id.songTitle)
        songArtistView       = view?.findViewById(R.id.songArtist)

        /** Linking it with the view */
        fab = view?.findViewById(R.id.favouriteIcon)

        /** Fading the favorite icon */
        fab?.alpha = 0.8f

        glView = view?.findViewById(R.id.visualizer_view)

        return view
    }

/*********************************** FUNCTION ENDS HERE ********************************************/
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        audioVisualization = glView as AudioVisualization
    }

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

    override fun onResume()
    {
        super.onResume()
        audioVisualization?.onResume()
        Statified.mSensorManager?.registerListener(
            Statified.mSensorListener,
            Statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    override fun onPause()
    {
        audioVisualization?.onPause()
        super.onPause()
        Statified.mSensorManager?.unregisterListener(Statified.mSensorListener)
    }

    override fun onDestroyView()
    {
        audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        Statified.mSensorManager = Statified.myActivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager?


        mAccelaration        = 0.0f
        mAccelarationCurrent = SensorManager.GRAVITY_EARTH
        mAccelarationLast    = SensorManager.GRAVITY_EARTH

        bindShakeListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?)
    {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /** This methods helps in managing diff icons on diff screens */
    override fun onPrepareOptionsMenu(menu: Menu?)
    {
        super.onPrepareOptionsMenu(menu)

        val item:MenuItem? = menu?.findItem(R.id.action_redirect)
        item?.isVisible    = true

        val item2:MenuItem? = menu?.findItem(R.id.action_sort)
        item2?.isVisible    = false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean
    {
        when(item?.itemId)
        {
            R.id.action_redirect ->
            {
                Statified.myActivity?.onBackPressed()
                return  false
            }
        }

        return false
    }

/*********************************** FUNCTION ENDS HERE ********************************************/
    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        /*Initialising the database*/
        favoriteContent = EchoDatabase(myActivity)

        /* OBJECT OF CURRENT SONG HELPER CLASS */
        currentSongHelper = CurrentSongHelper()

        /* INITIALIZATION OF PARAMS OF Helper Class */
        currentSongHelper?.isPlaying = true
        currentSongHelper?.isLoop    = false
        currentSongHelper?.isShuffle = false


        var path        : String? = null
        var songTitle   : String?
        var songArtist  : String?
        var songId      : Long

        try {
              path            = arguments?.getString("songPath")
              songTitle       = arguments?.getString("songTitle")
              songArtist      = arguments?.getString("songArtist")
              songId          = arguments?.getInt("songId")!!.toLong()

            /** Fetching bundle for details & position */
            CurrentPosition = arguments?.getInt("position")!!
            fetchSongs      = arguments?.getParcelableArrayList("songData")

            /** Storing it in Helper Class object for later use */
            currentSongHelper?.songPath    = path
            currentSongHelper?.songTitle   = songTitle
            currentSongHelper?.songArtist  = songArtist
            currentSongHelper?.songId      = songId
            currentSongHelper?.currentPos = CurrentPosition


            updateTextViews(currentSongHelper?.songTitle as String,
                           currentSongHelper?.songArtist as String)

        }
            catch (e: Exception) { e.printStackTrace() }

/** ******************************** BOTTOM BAR ****************************************************/
        val fromBottomBar   = arguments?.get("MainBottomBar") as? String
        val fromFavBottomBar = arguments?.get("FavBottomBar") as? String

        if (fromBottomBar != null)
        {
            Statified.mediaPlayer = MainScreenFragment.Statified.mediaPlayer
        }
          else if(fromFavBottomBar !=null)
         {

            Statified.mediaPlayer = FavouriteFragment.Statified.mediaPlayer

         }
           else {
                  Statified.mediaPlayer = MediaPlayer()
                  Statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

              try
              {
                  Statified. mediaPlayer?.setDataSource(myActivity as Context, Uri.parse(path) )
                  Statified. mediaPlayer?.prepare()
              }
                 catch (e: Exception)
                 {

                     Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
                     e.printStackTrace()
                 }

              Statified.mediaPlayer?.start()
        }

        processInformation( Statified.mediaPlayer as MediaPlayer)

/** ***********************************************************************************************/


        /** CHANGING ICONS */
        if (currentSongHelper?.isPlaying as Boolean)
        {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else
            {
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }


        Statified.mediaPlayer?.setOnCompletionListener {
            onSongComplete()

        }

        clickHandler()

        var visualizationHandler = DbmHandler.Factory.newVisualizerHandler(myActivity as Context, 0)
        audioVisualization?.linkTo(visualizationHandler)

        var prefsForShuffle = myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)
        var isShuffleAllowed        = prefsForShuffle?.getBoolean("feature", false)

        if (isShuffleAllowed as Boolean)
        {
            currentSongHelper?.isShuffle = true
            currentSongHelper?.isLoop    = false
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        } else {
                currentSongHelper?.isShuffle = false
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
               }



        var prefsForLoop = myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)
        var isLoopAllowed       = prefsForLoop?.getBoolean("feature", false)



        if (isLoopAllowed as Boolean)
        {
            currentSongHelper?.isShuffle = false
            currentSongHelper?.isLoop    = true
            shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        } else {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                currentSongHelper?.isLoop = false
               }



        if (favoriteContent?.checkIfIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
        {
            fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
        } else
            {

                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))
                //fab?.setImageResource(R.drawable.favorite_off)
            }

    }

/*************************************** FUNCTION ENDS HERE ****************************************/
    fun clickHandler()
    {

        fab?.setOnClickListener {

            if (favoriteContent?.checkIfIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
            {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))
                favoriteContent?.deleteFavorites(currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(myActivity, "Song Removed from Favorites", Toast.LENGTH_SHORT).show()
            } else
              {
                fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))


                   favoriteContent?.storeAsFavourite(
                       currentSongHelper?.songId?.toInt(),
                       currentSongHelper?.songArtist,
                       currentSongHelper?.songTitle,
                       currentSongHelper?.songPath

                )
                Toast.makeText(myActivity, "Song Added to Favorites", Toast.LENGTH_SHORT).show()
              }

        }
/********************************** LISTENER STARTS HERE *******************************************/
        shuffleImageButton?.setOnClickListener {

            var editorShuffle =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()

            var editorLoop =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            /* Disabling the shuffle icon */
            if (currentSongHelper?.isShuffle as Boolean)
            {
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                currentSongHelper?.isShuffle = false
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
            } else
                {
                    currentSongHelper?.isShuffle = true
                    currentSongHelper?.isLoop    = false
                    shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
                    loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)

                    editorShuffle?.putBoolean("feature", true)
                    editorShuffle?.apply()

                    editorLoop?.putBoolean("feature", false)
                    editorLoop?.apply()
                }
        }

/********************************** LISTENER STARTS HERE *******************************************/
        nextImageButton?.setOnClickListener {

            currentSongHelper?.isPlaying = true

            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)

            /* CHECKING IF SUFFLE IS ON */
            if (currentSongHelper?.isShuffle as Boolean)
            {
                playNext("PlayNextLikeNormalShuffle")
            }
              else { playNext("PlayNextNormal") }
        }
/**************************************** LISTENER STARTS HERE *************************************/
        previousImageButton?.setOnClickListener {

            currentSongHelper?.isPlaying = true

            if (currentSongHelper?.isLoop as Boolean)
            {
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
            }
            playPrevious()
        }

/**************************************** LISTENER STARTS HERE *************************************/
        loopImageButton?.setOnClickListener {

            var editorShuffle =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_SHUFFLE, Context.MODE_PRIVATE)?.edit()

            var editorLoop =
                myActivity?.getSharedPreferences(Staticated.MY_PREFS_LOOP, Context.MODE_PRIVATE)?.edit()

            /* Disabling the loop icon */
            if (currentSongHelper?.isLoop as Boolean)
            {
                currentSongHelper?.isLoop = false
                loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
                editorLoop?.putBoolean("feature", false)
                editorLoop?.apply()
            } else {
                currentSongHelper?.isLoop = true
                currentSongHelper?.isShuffle = false

                loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
                shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
                editorShuffle?.putBoolean("feature", false)
                editorShuffle?.apply()
                editorLoop?.putBoolean("feature", true)
                editorLoop?.apply()
            }
        }

/**************************************** LISTENER STARTS HERE *************************************/
        playPauseImageButton?.setOnClickListener {

            /**  Checks whether song is playing or not  **/
            if (Statified.mediaPlayer?.isPlaying as Boolean)
            {
                Statified.mediaPlayer?.pause()
                currentSongHelper?.isPlaying = false
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            } else
                 {   Statified.mediaPlayer?.start()
                     currentSongHelper?.isPlaying = true
                     playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
                 }
        }


    }

/*********************************** FUNCTION ENDS HERE ********************************************/


    fun playPrevious()
   {
        CurrentPosition -= 1

        if (CurrentPosition == -1)
        {   CurrentPosition = 0   }

        if (currentSongHelper?.isPlaying as Boolean)
        {
            playPauseImageButton?.setBackgroundResource(R.drawable.pause_icon)
        } else
            {
                playPauseImageButton?.setBackgroundResource(R.drawable.play_icon)
            }

        currentSongHelper?.isLoop = false
        var nextSong = fetchSongs?.get(CurrentPosition)

        currentSongHelper?.songTitle  = nextSong?.songTitle
        currentSongHelper?.songPath   = nextSong?.songData
        currentSongHelper?.currentPos = CurrentPosition
        currentSongHelper?.songArtist = nextSong?.artist
        currentSongHelper?.songId     = nextSong?.songID as Long

        updateTextViews(
            currentSongHelper?.songTitle as String,
            currentSongHelper?.songArtist as String
        )

       Statified.mediaPlayer?.reset()


        try {
            Statified. mediaPlayer?.setDataSource(myActivity as Context, Uri.parse(currentSongHelper?.songPath))
            Statified.mediaPlayer?.prepare()
            Statified.mediaPlayer?.start()
            processInformation(Statified.mediaPlayer as MediaPlayer)
        }
          catch (e: Exception)
          {  e.printStackTrace()   }


       if (favoriteContent?.checkIfIdExists(currentSongHelper?.songId?.toInt() as Int) as Boolean)
       {
           fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_on))
       } else
         {
             fab?.setImageDrawable(ContextCompat.getDrawable(myActivity as Context,R.drawable.favorite_off))
          }


    }


/*********************************** FUNCTION ENDS HERE ********************************************/
    fun bindShakeListener()
  {
        Statified.mSensorListener = object : SensorEventListener
        {

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
            {

            }

            override fun onSensorChanged(p0: SensorEvent) {
                val x = p0.values[0]
                val y = p0.values[1]
                val z = p0.values[2]


                mAccelarationLast = mAccelarationCurrent

                mAccelarationCurrent = sqrt( ( (x*x + y*y + z*z).toDouble())).toFloat()

                val delta = mAccelarationCurrent - mAccelarationLast

                mAccelaration = mAccelaration * 0.9f + delta

                if (mAccelaration > 12)
                {

                    val prefs = myActivity?.getSharedPreferences(
                        Statified.MY_PREFS_NAME,
                        Context.MODE_PRIVATE
                    )

                    val isAllowed = prefs?.getBoolean("feature", false)

                    if (isAllowed as Boolean)
                    {
                        playNext("PlayNextNormal")
                    }
                }
            }


        }
    }



}




