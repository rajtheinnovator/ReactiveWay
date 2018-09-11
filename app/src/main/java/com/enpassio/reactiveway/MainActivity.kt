package com.enpassio.reactiveway

import android.content.Context
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

//This will serve as a copy of master before merging operators example
class MainActivity : AppCompatActivity() {

    private val adapter = GitHubRepoAdapter()
    private var subscription: Subscription? = null

    private val clientId = BuildConfig.CLIENT_ID
    private val clientSecret = BuildConfig.CLIENT_SECRET

    /*
    for redirecting back to the app, we'll need the code below which is
    referenced from: https://stackoverflow.com/a/33871016
    */
    private var context: Context? = null

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
        if (checkIfTokenIsAvailable()) {
            //if token is available in shared preference, we just need to make an api call to fetch users data
            getUsersDetails()
        } else {
            //otherwise, first save the token in the shared preference
            setupAPI()
        }
        context = this
    }

    private fun getUsersDetails() {
        //get access token from the shared preference
        val token = getPreferences(Context.MODE_PRIVATE).getString("token", "")
        Log.v("my_tag", "token is " + token)
        //now create service using the service generator so that it adds header to our call to the endpoint @/user
        val userDetailsService = UsersClient.client.create(UsersService::class.java)
        /* fetch data for users scope. This is important as we must include at least one @Field
        parameter to our service and because we don't want anything specific, we just use @user
        scope
        */
        val call = userDetailsService.getUsersData("user", token!!)
        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("my_tag", "couldn't get users data with error: " + t.message)
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                val usersData = response.body()
                Log.v("my_tag", "users avatar is: " + usersData?.avatarUrl)
                Log.v("my_tag", "users data is: " + usersData.toString())
            }
        })
    }

    private fun checkIfTokenIsAvailable(): Boolean {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", "")!!
        if (!token.isEmpty()) return true else return false
    }

    override fun onResume() {
        super.onResume()
        // the intent filter defined in AndroidManifest will handle the return from ACTION_VIEW intent
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(redirectUri)) {
            // use the parameter your API exposes for the code (mostly it's "code")
            val code = uri.getQueryParameter("code")
            if (code != null) {
                // get access token
                val loginService = APIClient.client.create(LoginService::class.java)

                val call = loginService.getAccessToken(code, BuildConfig.CLIENT_ID, BuildConfig.CLIENT_SECRET)

                call.enqueue(object : Callback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {
                        val accessToken = response.body()
                        runOnUiThread() {
                            val sharedPref = getPreferences(Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putString("token", accessToken?.token)
                                apply()
                            }
                            //after saving the token, fetch users data from the api
                            getUsersDetails()
                        }
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
                Uri.parse("https://github.com/login/oauth/"
                        + "authorize/" + "?client_id=" + clientId
                        + "&scope=user"
                        + "&redirect_uri=" + redirectUri))
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