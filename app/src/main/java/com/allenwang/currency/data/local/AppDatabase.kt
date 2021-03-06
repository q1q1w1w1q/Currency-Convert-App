package com.allenwang.currency.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.allenwang.currency.data.unity.CurrencyQuote
import com.allenwang.currency.data.unity.SupportedCurrency

@Database(entities = [SupportedCurrency::class, CurrencyQuote::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun supportedCurrencyDao(): SupportedCurrencyDao
    abstract fun currencyQuoteDao(): CurrencyQuoteDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "currency")
                .fallbackToDestructiveMigration()
                .build()
    }
}
