package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(Route::class), (MeasuredLocation::class)],version = 1, exportSchema = false)
abstract class RouteDB: RoomDatabase(){
    abstract fun routeDao(): RouteDao
    abstract fun measuredLocationDao(): MeasuredLocationDao

    companion object{
        private var sInstance: RouteDB? = null
        @Synchronized
        fun get(context: Context): RouteDB{
            if(sInstance == null){
                sInstance = Room.databaseBuilder(context.applicationContext,
                RouteDB::class.java,"routes.db").build()
            }
            return sInstance!!
        }
    }
}