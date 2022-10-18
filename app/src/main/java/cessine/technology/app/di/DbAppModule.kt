package cessine.technology.app.di

import android.app.Application
import androidx.room.Room
import cessini.technology.persistence.DatabaseHelper
import cessini.technology.persistence.DatabaseHelperImpl
import cessini.technology.persistence.MyWorldDB
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbAppModule {

    @Provides
    @Singleton
    fun providesDatabase(app: Application): MyWorldDB {
        return Room.databaseBuilder(
            app,
            MyWorldDB::class.java,
            "my-world-db"
        ).build()
    }

    @Provides
    @Singleton
    fun providesDatabaseHelper(myWorldDB: MyWorldDB):DatabaseHelper{
        return DatabaseHelperImpl(myWorldDB)
    }
}