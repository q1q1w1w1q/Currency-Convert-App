package com.allenwang.currency.ui.quotes

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.allenwang.currency.RxImmediateSchedulerRule
import com.allenwang.currency.data.repository.CurrencyQuotesRepository
import com.allenwang.currency.data.unity.CurrencyQuote
import com.nhaarman.mockitokotlin2.never
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import java.util.concurrent.TimeUnit


class CurrencyQuotesViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: CurrencyQuotesRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getQuotesLiveData() {
        val viewModel = CurrencyQuotesViewModel(repository)
        MatcherAssert.assertThat(viewModel.quotes, CoreMatchers.not(CoreMatchers.nullValue()))
    }

    @Test
    fun getLoadingLiveData() {
        val viewModel = CurrencyQuotesViewModel(repository)
        MatcherAssert.assertThat(viewModel.loading, CoreMatchers.not(CoreMatchers.nullValue()))
    }

    @Test
    fun getErrorLiveData() {
        val viewModel = CurrencyQuotesViewModel(repository)
        MatcherAssert.assertThat(viewModel.error, CoreMatchers.not(CoreMatchers.nullValue()))
    }

    @Test
    fun getCurrencyQuotesFromAPIIfDbIsNull() {
        val emptyList = emptyList<CurrencyQuote>()
        val apiList = listOf(CurrencyQuote("OOXX", 123.123))
        Mockito.`when`(repository.getCurrencyQuotesFromDb()).thenReturn(Observable.just(emptyList))
        Mockito.`when`(repository.getCurrencyQuotesFromApi("")).thenReturn(Observable.just(apiList))

        val viewModel = CurrencyQuotesViewModel(repository)

        viewModel.getCurrencyQuotes("")
        Mockito.verify(repository).getCurrencyQuotesFromApi("")
        Assert.assertThat(viewModel.quotes.value, CoreMatchers.`is`(apiList))
        Assert.assertThat(viewModel.loading.value, CoreMatchers.`is`(false))
    }

    @Test
    fun getCurrencyQuotesFromAPIIfDbIsNotNull() {
        val apiList = listOf(CurrencyQuote("OOXX", 123.123))
        Mockito.`when`(repository.getCurrencyQuotesFromDb()).thenReturn(Observable.just(apiList))
        Mockito.`when`(repository.getCurrencyQuotesFromApi("")).thenReturn(Observable.empty())

        val viewModel = CurrencyQuotesViewModel(repository)

        viewModel.getCurrencyQuotes("")
        Mockito.verify(repository, never()).getCurrencyQuotesFromApi("")
        Assert.assertThat(viewModel.quotes.value, CoreMatchers.`is`(apiList))
        Assert.assertThat(viewModel.loading.value, CoreMatchers.`is`(false))
    }

    @Test
    fun getCurrencyQuotesIfDbError() {
        val apiList = listOf(CurrencyQuote("OOXX", 123.123))
        val error = Error("db error")
        Mockito.`when`(repository.getCurrencyQuotesFromDb()).thenReturn(Observable.error(error))
        Mockito.`when`(repository.getCurrencyQuotesFromApi("")).thenReturn(Observable.just(apiList))

        val viewModel = CurrencyQuotesViewModel(repository)

        viewModel.getCurrencyQuotes("")
        Mockito.verify(repository, never()).getCurrencyQuotesFromApi("")
        Assert.assertThat(viewModel.error.value, CoreMatchers.`is`(error))
        Assert.assertThat(viewModel.loading.value, CoreMatchers.`is`(false))
    }

    @Test
    fun getCurrencyQuotesIfApiError() {
        val apiList = listOf(CurrencyQuote("OOXX", 123.123))
        val error = Error("db error")
        Mockito.`when`(repository.getCurrencyQuotesFromDb()).thenReturn(Observable.just(apiList))
        Mockito.`when`(repository.getCurrencyQuotesFromApi("")).thenReturn(Observable.error(error))

        val viewModel = CurrencyQuotesViewModel(repository)

        viewModel.getCurrencyQuotes("")
        Mockito.verify(repository, never()).getCurrencyQuotesFromApi("")

        Assert.assertThat(viewModel.quotes.value, CoreMatchers.`is`(apiList))
        Assert.assertThat(viewModel.error.value, CoreMatchers.`is`(nullValue()))
        Assert.assertThat(viewModel.loading.value, CoreMatchers.`is`(false))
    }

    @Test
    fun updateCurrencyQuotes() {
        val testScheduler = TestScheduler()
        RxJavaPlugins.setComputationSchedulerHandler { testScheduler }

        val viewModel = CurrencyQuotesViewModel(repository)
        viewModel.updateCurrencyQuotes("", 60L)

        testScheduler.advanceTimeBy(60, TimeUnit.SECONDS)
        Mockito.verify(repository).getCurrencyQuotesFromApi("")
    }

    companion object {
        @get:ClassRule
        @JvmStatic
        val testSchedulerRule = RxImmediateSchedulerRule()
    }
}