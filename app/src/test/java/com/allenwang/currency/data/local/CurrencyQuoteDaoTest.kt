package com.allenwang.currency.data.local

import android.os.Build
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.allenwang.currency.data.unity.CurrencyQuote
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@SmallTest
@Config(sdk = [Build.VERSION_CODES.O])
class CurrencyQuoteDaoTest {

    private lateinit var database: AppDatabase

    @Before
    fun initDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun closeDb() = database.close()

    @Test
    fun insertQuoteAndGetByCode() {
        // GIVEN - insert a quote
        val currencyQuote = CurrencyQuote("code", 123.123)
        database.currencyQuoteDao().insert(currencyQuote)

        // WHEN - Get the quote by id from the database
        val loaded = database.currencyQuoteDao().getCurrencyQuote(currencyQuote.currencyCode).blockingFirst()

        // THEN - The loaded data contains the expected values
        MatcherAssert.assertThat<CurrencyQuote>(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded.currencyCode, `is`(currencyQuote.currencyCode))
        MatcherAssert.assertThat(loaded.quote, `is`(currencyQuote.quote))
    }

    @Test
    fun insertQuoteReplacesOnConflict() {
        // Given that a quote is inserted
        val currencyQuote = CurrencyQuote("code", 123.123)
        database.currencyQuoteDao().insert(currencyQuote)

        // When a quote with the same id is inserted
        val newQuote = CurrencyQuote("code", 456.456)
        database.currencyQuoteDao().insert(newQuote)

        // THEN - The loaded data contains the expected values
        val loaded = database.currencyQuoteDao().getCurrencyQuote(currencyQuote.currencyCode).blockingFirst()
        MatcherAssert.assertThat(loaded.currencyCode, `is`(newQuote.currencyCode))
        MatcherAssert.assertThat(loaded.quote, `is`(newQuote.quote))
    }

    @Test
    fun insertQuoteAndGetQuotes() {
        // GIVEN - insert a quote
        val currencyQuote = CurrencyQuote("code", 123.123)
        database.currencyQuoteDao().insert(currencyQuote)

        // WHEN - Get tasks from the database
        val quotes = database.currencyQuoteDao().getAllCurrencyQuotes().blockingFirst()
        // THEN - There is only 1 quote in the database, and contains the expected values
        MatcherAssert.assertThat(quotes.size, CoreMatchers.`is`(1))
        MatcherAssert.assertThat(quotes[0].currencyCode, `is`(currencyQuote.currencyCode))
        MatcherAssert.assertThat(quotes[0].quote, `is`(currencyQuote.quote))
    }
}