package com.player.echo.adapters

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.player.echo.R
import com.player.echo.Songs
import com.player.echo.activities.MainActivity
import com.player.echo.fragments.SongPlayingFragment

class FavouriteAdapter (_songDetails:ArrayList<Songs>, _context: Context): RecyclerView.Adapter<FavouriteAdapter.MyViewHolder> ()
{

    var songDetails:ArrayList<Songs>? = null
    var mContext   : Context?         = null

    init {
        this.songDetails = _songDetails
        this.mContext  =   _context
        }
/** ******************************** ON BIND VIEW HOLDER ******************************************/


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        /** CONTAINS ARTIST,DATE,NAME **/
        val songObject = songDetails?.get(position)

        holder.trackTitle!!.text = songObject?.songTitle
        holder.trackArtist!!.text = songObject?.artist

        holder.contentHolder?.setOnClickListener {
            val songPlayingFragment = SongPlayingFragment()

            var args = Bundle()
            args.putString("songArtist", songObject?.artist)
            args.putString("songTitle", songObject?.songTitle)
            args.putString("songPath", songObject?.songData)
            args.putInt("songId", songObject?.songID?.toInt() as Int)
            args.putInt("songPosition", position)
            args.putParcelableArrayList("songData", songDetails)

            songPlayingFragment.arguments = args

            (mContext as MainActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.details_fragment, songPlayingFragment)
                .addToBackStack("SongPlayingFragmentFavourite")
                .commit()


            try
            {
                if (SongPlayingFragment.Statified.mediaPlayer?.isPlaying as Boolean)
                {

                    SongPlayingFragment.Statified.mediaPlayer?.stop()

                }

            } catch (e: Exception) { e.printStackTrace() }
        }
    }


/** ***************************** ON CREATE VIEW HOLDER *******************************************/


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
        {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_cutom_mainscreen_adapter,parent,false)

            return MyViewHolder(itemView)
        }

/** ******************************  GET ITEM COUNT *************************************************/

        override fun getItemCount(): Int
        {
            if(songDetails ==null)
            {
                return  0
            }

            else {   return (songDetails as ArrayList<Songs>).size }
    }

/** ****************************** MY VIEW HOLDER CLASS *******************************************/

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        var trackTitle: TextView?          = null
        var trackArtist: TextView?         = null
        var contentHolder: RelativeLayout? = null

        init {
            trackTitle = view.findViewById(R.id.trackTitle) as TextView
            trackArtist = view.findViewById(R.id.TrackArtist)as TextView
            contentHolder = view.findViewById(R.id.content_row) as RelativeLayout
        }
    }
}

/**################################### E N D #####################################################*/