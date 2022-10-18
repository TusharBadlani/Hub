package cessini.technology.persistence

//import android.content.Context
//import androidx.room.Room
//
//
//object DatabaseBuilder {
//    private var instance: MyWorldDB? = null
//
//    fun getInstance(context: Context): MyWorldDB {
//        if (instance == null) {
//            synchronized(MyWorldDB::class) {
//                instance = buildDB(context)
//            }
//        }
//
//        return instance!!
//    }
//
//    private fun buildDB(context: Context): MyWorldDB? {
//
//        return Room.databaseBuilder(
//            context.applicationContext,
//            MyWorldDB::class.java,
//            "my-world-db"
//        ).build()
//    }
//}