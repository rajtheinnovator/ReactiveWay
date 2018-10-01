package com.enpassio.reactiveway.githubexample

import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.enpassio.reactiveway.R


class GitHubRepoAdapter : BaseAdapter() {

    private val gitHubRepos = ArrayList<GitHubRepo>()

    override fun getCount(): Int {
        return gitHubRepos.size
    }

    override fun getItem(position: Int): GitHubRepo? {
        return if (position < 0 || position >= gitHubRepos.size) {
            null
        } else {
            gitHubRepos.get(position)
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = if (convertView != null) convertView else createView(parent)
        val viewHolder = view.getTag() as GitHubRepoViewHolder
        viewHolder.setGitHubRepo(getItem(position))
        return view
    }

    fun setGitHubRepos(@Nullable repos: List<GitHubRepo>?) {
        if (repos == null) {
            return
        }
        gitHubRepos.clear()
        gitHubRepos.addAll(repos)
        notifyDataSetChanged()
    }

    private fun createView(parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.github_repo_item, parent, false)
        val viewHolder = GitHubRepoViewHolder(view)
        view.tag = viewHolder
        return view
    }

    private class GitHubRepoViewHolder(view: View) {

        private val textRepoName: TextView
        private val textRepoDescription: TextView
        private val textLanguage: TextView
        private val textStars: TextView

        init {
            textRepoName = view.findViewById(R.id.text_view_repo_name)
            textRepoDescription = view.findViewById(R.id.text_view_repo_description)
            textLanguage = view.findViewById(R.id.text_view_project_language)
            textStars = view.findViewById(R.id.text_view_number_of_stars)
        }

        fun setGitHubRepo(gitHubRepo: GitHubRepo?) {
            textRepoName.text = gitHubRepo!!.name
            textRepoDescription.text = gitHubRepo.description
            textLanguage.text = "Language: " + gitHubRepo.language
            textStars.text = "Stars: " + gitHubRepo.stargazersCount
        }
    }
}