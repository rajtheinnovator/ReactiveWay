package com.enpassio.reactiveway

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    private val adapter = GitHubRepoAdapter()
    private var subscription: Subscription? = null

    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET

    /*
    for redirecting back to the app, we'll need the code below which is
    referenced from: https://stackoverflow.com/a/33871016
    */

    private val redirectUri = "com.enpassio.reactiveway://redirecturi"

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
        val buttonAuthenticate = findViewById<View>(R.id.authenticate) as Button
        buttonAuthenticate.setOnClickListener { view -> setupAPI() }
    }

    override fun onResume() {
        super.onResume()

        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        val uri = intent.data
        Log.v("my_tag", "uri  is: " + uri)
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            val code = uri.getQueryParameter("code")
            Log.v("my_tag", "auth code is: " + code)
            if (code != null) {
                // get access token
                val loginService = ServiceGenerator.createService(LoginService::class.java, clientId, clientSecret)

                val call = loginService.getAccessToken(code)

                call.enqueue(object : Callback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                        val accessToken = response.body()
                        Log.v("my_tag", "accessToken is: " + accessToken?.token + " type is: " + accessToken?.tokenType)
                        Log.v("my_tag", "body is: " + response.body().toString() + "msg :" + response.message())
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        Log.e("my_tag", "error is: " + t.message)
                    }
                })
            } else if (uri.getQueryParameter("error") != null) {
                // show an error message here
            }
        }
    }

    private fun setupAPI() {
        val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(ServiceGenerator.API_BASE_URL + "authorize/" + "?client_id=" + clientId + "&redirect_uri=" + redirectUri))
        startActivity(intent)
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