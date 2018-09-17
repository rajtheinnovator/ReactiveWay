package com.enpassio.reactiveway.instantsearch.recyclerviewwithsearch

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.*
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.enpassio.reactiveway.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray


class RecyclerViewSearchActivity : AppCompatActivity(), ContactsAdapter.ContactsAdapterListener {
    private var recyclerView: RecyclerView? = null
    private var contactList: MutableList<Contact>? = null
    private var mAdapter: ContactsAdapter? = null
    private var searchView: SearchView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_search)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // toolbar fancy stuff
        supportActionBar!!.setTitle(R.string.toolbar_title)

        recyclerView = findViewById(R.id.recycler_view)
        contactList = ArrayList()
        mAdapter = ContactsAdapter(this, contactList!!, this)

        // white background notification bar
        whiteNotificationBar(recyclerView)

        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = mLayoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.addItemDecoration(MyDividerItemDecoration(this, DividerItemDecoration.VERTICAL, 36))
        recyclerView!!.adapter = mAdapter

        //fetchContactsThroughVolley()
        fetchContactsThroughHardcodedJsonData()
    }

    private fun fetchContactsThroughHardcodedJsonData() {
        val contactsStrings = "[{\n" +
                "\t\t\"name\": \"Tom Hardy\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/tom_hardy.jpg\",\n" +
                "\t\t\"phone\": \"(541) 754-3010\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Johnny Depp\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/johnny.jpg\",\n" +
                "\t\t\"phone\": \"(452) 839-1210\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Tom Cruise\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/tom_cruise.jpg\",\n" +
                "\t\t\"phone\": \"(541) 453-2311\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Keira Knightley\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/keira.jpg\",\n" +
                "\t\t\"phone\": \"(535) 324-4334\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Robert De Niro\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/robert_de.jpg\",\n" +
                "\t\t\"phone\": \"(767) 544-8867\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Leonardo DiCaprio\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/leonardo.jpg\",\n" +
                "\t\t\"phone\": \"(564) 333-2452\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Will Smith\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/will.jpg\",\n" +
                "\t\t\"phone\": \"(541) 879-3453\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Russell Crowe\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/russell.jpg\",\n" +
                "\t\t\"phone\": \"(234) 234-3321\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Brad Pitt\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/brad.jpg\",\n" +
                "\t\t\"phone\": \"(567) 754-8945\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Angelina Jolie\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/angelina.jpg\",\n" +
                "\t\t\"phone\": \"(324) 754-5433\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Kate Winslet\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/kate.jpg\",\n" +
                "\t\t\"phone\": \"(788) 343-3433\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Christian Bale\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/christian.jpg\",\n" +
                "\t\t\"phone\": \"(865) 755-3555\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Morgan Freeman\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/morgan.jpg\",\n" +
                "\t\t\"phone\": \"(445) 776-9076\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Hugh Jackman\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/hugh.jpg\",\n" +
                "\t\t\"phone\": \"(544) 454-4544\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Keanu Reeves\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/keanu.jpg\",\n" +
                "\t\t\"phone\": \"(454) 455-5445\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Tom Hanks\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/tom.jpg\",\n" +
                "\t\t\"phone\": \"(541) 454-4544\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Scarlett Johansson\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/scarlett.jpg\",\n" +
                "\t\t\"phone\": \"(545) 454-2567\"\n" +
                "\t},\n" +
                "\t{\n" +
                "\t\t\"name\": \"Robert Downey Jr.\",\n" +
                "\t\t\"image\": \"https://api.androidhive.info/json/images/robert.jpg\",\n" +
                "\t\t\"phone\": \"(444) 444-4444\"\n" +
                "\t}\n" +
                "]"
        val items = Gson().fromJson<List<Contact>>(contactsStrings,
                object : TypeToken<List<Contact>>() {

                }.type)
        // adding contacts to contacts list
        contactList!!.clear()
        contactList!!.addAll(items)
        // refreshing recycler view
        mAdapter!!.notifyDataSetChanged()
    }

    /**
     * fetches json by making http calls
     */
    private fun fetchContactsThroughVolley() {

        val request = JsonArrayRequest(URL,
                Response.Listener<JSONArray>() {
                    fun onResponse(response: JSONArray?) {
                        if (response == null) {
                            Toast.makeText(applicationContext, "Couldn't fetch the contacts! Pleas try again.", Toast.LENGTH_LONG).show()
                            return
                        }

                        val items = Gson().fromJson<List<Contact>>(response.toString(),
                                object : TypeToken<List<Contact>>() {

                                }.type)

                        // adding contacts to contacts list
                        contactList!!.clear()
                        contactList!!.addAll(items)
                        // refreshing recycler view
                        mAdapter!!.notifyDataSetChanged()
                    }
                }, object : Response.ErrorListener {
            override fun onErrorResponse(error: VolleyError) {
                // error in getting json
                Log.e(TAG, "Error: " + error.message)
                Toast.makeText(applicationContext, "Error: " + error.message, Toast.LENGTH_SHORT).show()
            }
        })

        MyApplication.instance?.addToRequestQueue(request)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.action_search)
                .getActionView() as SearchView
        searchView!!.setSearchableInfo(searchManager
                .getSearchableInfo(componentName))
        searchView!!.setMaxWidth(Integer.MAX_VALUE)

        // listening to search query text change
        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                mAdapter!!.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                mAdapter!!.filter.filter(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()


        return if (id == R.id.action_search) {
            true
        } else super.onOptionsItemSelected(item)

    }

    override fun onBackPressed() {
        // close search view on back button pressed
        if (!searchView!!.isIconified()) {
            searchView!!.setIconified(true)
            return
        }
        super.onBackPressed()
    }

    private fun whiteNotificationBar(view: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view!!.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onContactSelected(contact: Contact) {
        Toast.makeText(applicationContext, "Selected: " + contact.name + ", " + contact.phone, Toast.LENGTH_LONG).show()
    }

    companion object {
        private val TAG = RecyclerViewSearchActivity::class.java.simpleName

        // url to fetch contacts json
        private val URL = "https://api.androidhive.info/json/contacts.json"
    }
}