package com.player.echo.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import com.player.echo.R

class SettingsFragment : Fragment()
{

    var myActivity  : Activity? = null
    var shakeSwitch : Switch? = null

    object Statified
    {
        var MY_PREFS_NAME = "ShakeFeature"
    }

       override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
       {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

           activity?.title = "Settings"
           shakeSwitch = view?.findViewById(R.id.switchShape)
           return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myActivity = context as Activity
    }


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myActivity = activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?)
    {
        super.onActivityCreated(savedInstanceState)

        val prefs = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)
        var isAllowed     = prefs?.getBoolean("feature", false)

        shakeSwitch?.isChecked = isAllowed as Boolean

        /*Now we handle the change events i.e. when the switched is turned ON or OFF*/
        shakeSwitch?.setOnCheckedChangeListener {
                compound_Button, b ->

            if (b) {
                   /*If the switch is turned on we then make the feature to be true*/
                   val editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                   editor?.putBoolean("feature", true)
                   editor?.apply ()
            }

            else
            {
                /*If the switch is turned on we then make the feature to be true*/
                val editor = myActivity?.getSharedPreferences(Statified.MY_PREFS_NAME, Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature", false)
                editor?.apply ()
            }
        }
    }



}