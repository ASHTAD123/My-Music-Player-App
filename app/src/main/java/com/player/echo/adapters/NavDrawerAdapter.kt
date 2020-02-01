package com.player.echo.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.player.echo.R
import com.player.echo.activities.MainActivity
import com.player.echo.fragments.AboutUsFragment
import com.player.echo.fragments.FavouriteFragment
import com.player.echo.fragments.MainScreenFragment
import com.player.echo.fragments.SettingsFragment

class NavigationDrawerAdapter(_contentList:ArrayList<String>,_images:IntArray,_context:Context):
    RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>()
 {

            private var contentList :ArrayList<String>? = null
            private var images      :IntArray?          = null
            private var mContext    :Context?           = null

            init{
                this.contentList  =_contentList
                this.images       =_images
                this.mContext     =_context
            }
/**   ****************************** ON BIND VIEW HOLDER ******************************************/
    override fun onBindViewHolder(holder: NavViewHolder, position: Int)
    {

        holder.icon?.setBackgroundResource(images?.get(position) as Int)
        holder.text?.text = contentList?.get(position)

        holder.contentHolder?.setOnClickListener{

            when (position)
            {
                0 -> {
                    val mainScreenFragment = MainScreenFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,mainScreenFragment)
                        .commit()
                }

                1 -> {
                    val favScreenFragment = FavouriteFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,favScreenFragment)
                        .commit()
                }

                2 -> {
                    val settingsFragment = SettingsFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,settingsFragment)
                        .commit()
                }

                3 -> {
                    val aboutUsFragment = AboutUsFragment()
                    (mContext as MainActivity).supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.details_fragment,aboutUsFragment)
                        .commit()
                       }


            }
                  MainActivity.Statified.drawerLayout?.closeDrawers()
        }

    }

/** ***************************** ON CREATE VIEW HOLDER *******************************************/
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NavViewHolder
    {
        var itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_custom_navdrawer,parent,false)

        return NavViewHolder(itemView)
    }

/*** ******************************  GET ITEM COUNT ************************************************/
    override fun getItemCount(): Int
    {
        return (contentList as ArrayList).size
    }


     class NavViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView as View)
     {
        var icon         : ImageView? = null
        var text         : TextView? = null
        var contentHolder: RelativeLayout? = null

        init {
            icon          = itemView?.findViewById(R.id.icon_drawer)
            text          = itemView?.findViewById(R.id.text_nav_drawer)
            contentHolder = itemView?.findViewById(R.id.nav_drawer_item_content_holder)
           }

    }
}
/**################################### E N D #####################################################*/