package com.adam_lincoln.android.arrowforgithub.data.source.remote

import android.app.Application
import com.adam_lincoln.android.arrowforgithub.BuildConfig
import com.adam_lincoln.android.arrowforgithub.data.RepoModel
import com.adam_lincoln.android.arrowforgithub.data.source.RepoDataSource
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.internal.cache.CacheInterceptor
import okhttp3.internal.cache.CacheStrategy
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RepoRemoteDataSource : RepoDataSource {

    companion object {

        const val GITHUB_BASE_URL = "https://api.github.com/"
        const val GITHUB_ORG_NAME = "shopify"

        private var INSTANCE: RepoRemoteDataSource? = null

        @JvmStatic
        fun getInstance(): RepoRemoteDataSource {
            if (INSTANCE == null) {
                synchronized(RepoRemoteDataSource::javaClass) {
                    if (INSTANCE == null) {
                        INSTANCE = RepoRemoteDataSource()
                    }
                }
            }
            return INSTANCE!!
        }
    }

    private val repoCache = LinkedHashMap<Int, RepoModel>(0)
    private val httpClient: OkHttpClient
    private val retrofit: Retrofit
    private val gitHubService: GitHubService

    init {
        // https://square.github.io/okhttp/3.x/okhttp/okhttp3/Cache.html

        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            builder.addInterceptor(logging)
        }

        builder.addNetworkInterceptor(StethoInterceptor())

        builder.connectTimeout(3L, TimeUnit.SECONDS)
        builder.readTimeout(3L, TimeUnit.SECONDS)
        builder.writeTimeout(3L, TimeUnit.SECONDS)
        builder.retryOnConnectionFailure(false)

//        val cache = CacheControl.Builder()
//            .maxStale(365, TimeUnit.DAYS)
//            .build()

//        val cacheSize = 10 * 1024 * 1024 // 10MB
//        builder.cache(new Cache(context.getCacheDir(), cacheSize)

        httpClient = builder.build()

        retrofit = Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()

        gitHubService = retrofit.create(GitHubService::class.java)
    }

    override fun getRepos(repoCallback: RepoDataSource.RepoCallback) {
        val call = gitHubService.getRepos(GITHUB_ORG_NAME)
        call.enqueue(object : Callback<List<RepoModel>> {

            override fun onFailure(call: Call<List<RepoModel>>, t: Throwable) {
                repoCallback.onError()
            }

            override fun onResponse(call: Call<List<RepoModel>>, response: Response<List<RepoModel>>) {
                val modelList = response.body()!!
                repoCallback.onLoaded(modelList)
            }
        })
    }

    override fun refreshRepos() {
        // N/A - Implemented by RepoRepository.
    }

    override fun saveRepo(repo: RepoModel) {
        repoCache[repo.id] = repo
    }

    override fun deleteAllRepos() {
        repoCache.clear()
    }

}