package com.enpassio.reactiveway

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    
    private val adapter = GitHubRepoAdapter()
    private var subscription: Subscription? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<View>(R.id.list_view_repos) as ListView
        listView.setAdapter(adapter)

        val editTextUsername = findViewById<View>(R.id.edit_text_username) as EditText
        val buttonSearch = findViewById<View>(R.id.button_search) as Button
        buttonSearch.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val username = editTextUsername.text.toString()
                if (!TextUtils.isEmpty(username)) {
                    getStarredRepos(username)
                }
            }
        })
    }

    override fun onDestroy() {
        if (subscription != null && !subscription!!.isUnsubscribed) {
            subscription!!.unsubscribe()
        }
        super.onDestroy()
    }

    private fun getStarredRepos(username: String) {
        subscription = GitHubClient.getInstance()
                .getStarredRepos(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<GitHubRepo>> {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                    }

                    override fun onNext(gitHubRepos: List<GitHubRepo>) {
                        adapter.setGitHubRepos(gitHubRepos)
                    }
                })
    }

    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }
}