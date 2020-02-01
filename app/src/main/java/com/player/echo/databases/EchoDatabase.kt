package com.player.echo.databases

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.player.echo.Songs
import com.player.echo.databases.EchoDatabase.Static.COLUMN_ID
import com.player.echo.databases.EchoDatabase.Static.COLUMN_SONG_ARTIST
import com.player.echo.databases.EchoDatabase.Static.COLUMN_SONG_PATH
import com.player.echo.databases.EchoDatabase.Static.COLUMN_SONG_TITLE
import com.player.echo.databases.EchoDatabase.Static.TABLE_NAME

class EchoDatabase : SQLiteOpenHelper
{


    private var song_List = ArrayList<Songs>()
    object Static
    {
        var DB_VERSION         = 1
        var DB_NAME            = "FavouriteDatabase"
        var TABLE_NAME         = "FavouriteTable"
        var COLUMN_ID          = "SongID"
        var COLUMN_SONG_TITLE  = "SongTitle"
        var COLUMN_SONG_ARTIST = "SongArtist"
        var COLUMN_SONG_PATH   = "SongPath"


    }
/** ***********************************************************************************************/

    @SuppressLint("SQLiteString")

/** ******************************** ON CREATE ****************************************************/

    override fun onCreate(sqliteDatabase: SQLiteDatabase?)
    {
        /** Passing Sql Queries in Database object **/

        sqliteDatabase?.execSQL(
        "CREATE TABLE " + TABLE_NAME + "( " + COLUMN_ID + " INTEGER," +
                                                  COLUMN_SONG_ARTIST + " STRING," +
                                                  COLUMN_SONG_TITLE + " STRING," +
                                                  COLUMN_SONG_PATH + " STRING);"
        )
    }


/** ********************************* ON UPGRADE **************************************************/


    /** Is called when upgrading the version of the app adding new columns to existing list **/
    override fun onUpgrade(db: SQLiteDatabase?,oldVersion: Int , newVersion:Int)
    {

    }


/** *************************** C O N S T R U C T O R **********************************************/
    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(
        context,
        name,
        factory,
        version
    )


    constructor(context: Context?) : super(context, Static.DB_NAME, null, Static.DB_VERSION)

/** ************************** STORE AS FAVOURITE *************************************************/
         fun storeAsFavourite(id: Int?, artist: String?, songTitle: String?, path: String?)
        {
          val db = this.writableDatabase

         /** Object of Content Resolver **/
         var contentValues = ContentValues()


         contentValues.put(COLUMN_ID,id)
         contentValues.put(COLUMN_SONG_ARTIST,artist)
         contentValues.put(COLUMN_SONG_TITLE, songTitle  )
         contentValues.put(COLUMN_SONG_PATH, path)


        /** Inserting values in Database **/
            db.insert(TABLE_NAME,null,contentValues)
            db.close()

        }
/** ********************************* QUERY LIST **************************************************/
        /** Fun to fetch data from Database */
        fun queryList(): ArrayList<Songs>? {

            try {
                val db = this.readableDatabase
                val queryParams = "SELECT * FROM $TABLE_NAME"

                /** Selecting all rows **/
                var cSor = db.rawQuery(queryParams, null)

                /** Checking if cursor is on top **/
                if ( cSor.moveToFirst() )
                {
                    do {

                        var id          = cSor.getInt(cSor.getColumnIndexOrThrow(COLUMN_ID))
                        var artist   = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_ARTIST))
                        var title    = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_TITLE))
                        var songPath = cSor.getString(cSor.getColumnIndexOrThrow(COLUMN_SONG_PATH))

                        song_List.add(Songs(id.toLong(), artist,title, songPath, 0))
                    }
                    while ( cSor.moveToNext() )

                 }

                else { return null }
            }

            catch (e: Exception)
            {
                e.printStackTrace()
            }
            return song_List
        }
/**  ******************************* CHECK IF ID EXIST *******************************************/

    /** Checking if specific songId exists in Database or no **/

        fun checkIfIdExists(_id: Int): Boolean {
            var storeId = 1090
            val db = this.readableDatabase
            val queryParams = "SELECT * FROM $TABLE_NAME WHERE SongID = '$_id'"


            val cSor = db.rawQuery(queryParams, null)

            if ( cSor.moveToFirst() )
            {
                    do {

                        storeId = cSor.getInt(cSor.getColumnIndexOrThrow(COLUMN_ID) )

                    } while (cSor.moveToNext())


            }
            else { return false }

            return storeId != 1090

        }
/**  ******************************** DELETE FAVOURITES ********************************************/
        /** Fun to delete song **/
        fun deleteFavorites(_id: Int)
        {
            val db = this.writableDatabase
            db.delete(TABLE_NAME, "$COLUMN_ID = $_id", null)
            db.close()
        }

/** *********************************** CHECK SIZE ************************************************/
        fun checkSize():Int
        {
            var counter = 0
            val db = this.readableDatabase
            val queryParams = "SELECT * FROM $TABLE_NAME"

            val cSor = db.rawQuery(queryParams, null)


            if (cSor.moveToFirst())
            {
                    do {
                        counter += 1
                    }
                    while (cSor.moveToNext())
            }

            else {
                    return 0
                }

                return counter
            }
        }


/**################################### E N D #####################################################*/