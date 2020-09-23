package fi.metropolia.juhavuo.trackingaccuracy

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [(Route::class), (MeasuredLocation::class)],version = 2, exportSchema = false)
abstract class RouteDB: RoomDatabase(){
    abstract fun routeDao(): RouteDao
    abstract fun measuredLocationDao(): MeasuredLocationDao

    companion object{
        private var sInstance: RouteDB? = null
        @Synchronized
        fun get(context: Context): RouteDB{

            val MIGRATION_1_2 = object: Migration(1,2){
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL("ALTER TABLE Route ADD COLUMN epsilon DOUBLE")
                    database.execSQL("ALTER TABLE Route ADD COLUMN accuracyThreshold INT")
                }
            }

            if(sInstance == null){
                sInstance = Room.databaseBuilder(context.applicationContext,
                RouteDB::class.java,"routes.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
            }
            return sInstance!!
        }
    }
}