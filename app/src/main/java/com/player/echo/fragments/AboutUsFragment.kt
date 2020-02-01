package com.player.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.player.echo.R


class AboutUsFragment : Fragment()
{
    var myActivity   : Activity? = null

    override fun onAttach(context: Context?)
    {
        super.onAttach(context)
        myActivity = context as Activity
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {

        val view = inflater.inflate(R.layout.fragment_about_us, container, false)

        return view

    }


}
