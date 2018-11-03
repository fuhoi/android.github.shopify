package com.lincoln.adam.githubshopifylauncher.data.source.local

import android.arch.persistence.room.*
import com.lincoln.adam.githubshopifylauncher.data.RepoModel

@Dao
interface RepoDao {

    @Query("SELECT * FROM RepoModel")
    fun getRepos(): List<RepoModel>

    @Query("SELECT * FROM RepoModel WHERE id = :repoId")
    fun getRepoById(repoId: String): RepoModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepo(repo: RepoModel)

    @Update
    fun updateRepo(repo: RepoModel): Int

    @Query("DELETE FROM RepoModel WHERE id = :repoId")
    fun deleteRepoById(repoId: String): Int

    @Query("DELETE FROM RepoModel")
    fun deleteRepos()

}
