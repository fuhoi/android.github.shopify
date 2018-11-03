package com.lincoln.adam.githubshopifylauncher.repo

import com.lincoln.adam.githubshopifylauncher.BasePresenter
import com.lincoln.adam.githubshopifylauncher.BaseView

interface RepoContract {

    interface View : BaseView<Presenter> {

        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showRepoList(repoList: List<RepoViewModel>)

        fun navigateToUrl(url: String)

    }

    interface Presenter : BasePresenter {

        fun loadRepos(forceUpdate: Boolean)

        fun onGitHubUrlClick(repo: RepoViewModel)

        fun onHomepageUrlClick(repo: RepoViewModel)

    }
}