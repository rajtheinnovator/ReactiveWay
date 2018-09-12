package com.enpassio.reactiveway.notesapp.view

import android.content.DialogInterface
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.notesapp.network.ApiClient
import com.enpassio.reactiveway.notesapp.network.ApiService
import com.enpassio.reactiveway.notesapp.network.model.Note
import com.enpassio.reactiveway.notesapp.network.model.User
import com.enpassio.reactiveway.notesapp.utils.PrefUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_notes.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class NotesActivity : AppCompatActivity() {

    private var apiService: ApiService? = null
    private val disposable = CompositeDisposable()
    private var mAdapter: NotesAdapter? = null
    private var notesList: ArrayList<Note>? = null

    lateinit var coordinatorLayout: CoordinatorLayout

    lateinit var recyclerView: RecyclerView

    lateinit var noNotesView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        setContentView(R.layout.activity_notes)

        notesList = ArrayList()
        recyclerView = recycler_view
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        noNotesView = txt_empty_notes_view

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setTitle(getString(R.string.activity_title_home))
        setSupportActionBar(toolbar)


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                showNoteDialog(false, null, -1)
            }
        })

        // white background notification bar
        whiteNotificationBar(fab)

        apiService = ApiClient.getClient(applicationContext).create(ApiService::class.java)

        mAdapter = NotesAdapter(this, notesList!!)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.addItemDecoration(MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16))
        recyclerView.adapter = mAdapter

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         */
        recyclerView.addOnItemTouchListener(RecyclerTouchListener(this,
                recyclerView, object : RecyclerTouchListener.ClickListener {
            override fun onClick(view: View, position: Int) {}

            override fun onLongClick(view: View?, position: Int) {
                showActionsDialog(position)
            }
        }))

        /**
         * Check for stored Api Key in shared preferences
         * If not present, make api call to register the user
         * This will be executed when app is installed for the first time
         * or data is cleared from settings
         */
        if (TextUtils.isEmpty(PrefUtils.getApiKey(this))) {
            registerUser()
        } else {
            // user is already registered, fetch all notes
            fetchAllNotes()
        }
    }

    /**
     * Registering new user
     * sending unique id as device identification
     * https://developer.android.com/training/articles/user-data-ids.html
     */
    private fun registerUser() {
        // unique id to identify the device
        val uniqueId = UUID.randomUUID().toString()

        disposable.add(
                apiService!!
                        .register(uniqueId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<User>() {
                            override fun onSuccess(user: User) {
                                // Storing user API Key in preferences
                                PrefUtils.storeApiKey(applicationContext, user.spiKey)

                                Toast.makeText(applicationContext,
                                        "Device is registered successfully! ApiKey: " + PrefUtils.getApiKey(applicationContext),
                                        Toast.LENGTH_LONG).show()
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        }))
    }

    /**
     * Fetching all notes from api
     * The received items will be in random order
     * map() operator is used to sort the items in descending order by Id
     */
    private fun fetchAllNotes() {
        disposable.add(
                apiService!!.fetchAllNotes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map { listOfNotes -> listOfNotes.sortedWith(compareBy({ it.id })) }
                        .subscribeWith(object : DisposableSingleObserver<List<Note>>() {
                            override fun onSuccess(noteses: List<Note>) {
                                runOnUiThread {
                                    notesList = ArrayList()
                                    for (note in noteses) {
                                        notesList?.add(note)
                                    }
                                    recyclerView.adapter = NotesAdapter(this@NotesActivity, noteses)
                                    toggleEmptyNotes()
                                    Log.v("my_tag", "inside runOnUi")
                                }
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        })
        )
    }

    /**
     * Creating new note
     */
    private fun createNote(note: String) {
        disposable.add(
                apiService!!.createNote(note)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableSingleObserver<Note>() {

                            override fun onSuccess(note: Note) {
                                if (!TextUtils.isEmpty(note.error)) {
                                    Toast.makeText(applicationContext, note.error, Toast.LENGTH_LONG).show()
                                    return
                                }

                                Log.d(TAG, "new note created: " + note.id + ", " + note.note + ", " + note.timestamp)
                                fetchAllNotes()
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        }))
    }

    /**
     * Updating a note
     */
    private fun updateNote(noteId: Int, note: String, position: Int) {
        disposable.add(
                apiService!!.updateNote(noteId, note)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                Log.d(TAG, "Note updated!")

                                val n = notesList?.get(position)
                                n?.note = note

                                // Update item and notify adapter
                                notesList?.set(position, n!!)
                                mAdapter!!.notifyItemChanged(position)
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        }))
    }

    /**
     * Deleting a note
     */
    private fun deleteNote(noteId: Int, position: Int) {
        Log.e(TAG, "deleteNote: $noteId, $position")
        disposable.add(
                apiService!!.deleteNote(noteId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(object : DisposableCompletableObserver() {
                            override fun onComplete() {
                                Log.d(TAG, "Note deleted! $noteId")

                                // Remove and notify adapter about item deletion
                                notesList?.removeAt(position)
                                mAdapter!!.notifyItemRemoved(position)

                                Toast.makeText(this@NotesActivity, "Note deleted!", Toast.LENGTH_SHORT).show()

                                toggleEmptyNotes()
                            }

                            override fun onError(e: Throwable) {
                                Log.e(TAG, "onError: " + e.message)
                                showError(e)
                            }
                        })
        )
    }

    /**
     * Shows alert dialog with EditText options to enter / edit
     * a note.
     * when shouldUpdate=true, it automatically displays old note and changes the
     * button text to UPDATE
     */
    private fun showNoteDialog(shouldUpdate: Boolean, note: Note?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(applicationContext)
        val view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null)

        val alertDialogBuilderUserInput = AlertDialog.Builder(this@NotesActivity)
        alertDialogBuilderUserInput.setView(view)

        val inputNote: EditText = view.findViewById(R.id.note)
        val dialogTitle: TextView = view.findViewById(R.id.dialog_title)
        dialogTitle.setText(if (!shouldUpdate) getString(R.string.lbl_new_note_title) else getString(R.string.lbl_edit_note_title))

        if (shouldUpdate && note != null) {
            inputNote.setText(note.note)
        }
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(if (shouldUpdate) "update" else "save", DialogInterface.OnClickListener { dialogBox, id -> })
                .setNegativeButton("cancel",
                        DialogInterface.OnClickListener { dialogBox, id -> dialogBox.cancel() })

        val alertDialog = alertDialogBuilderUserInput.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(this@NotesActivity, "Enter note!", Toast.LENGTH_SHORT).show()
                    return
                } else {
                    alertDialog.dismiss()
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(note.id, inputNote.getText().toString(), position)
                } else {
                    // create new note
                    createNote(inputNote.getText().toString())
                }
            }
        })
    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private fun showActionsDialog(position: Int) {
        val colors = arrayOf<CharSequence>("Edit", "Delete")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose option")
        builder.setItems(colors, DialogInterface.OnClickListener { dialog, which ->
            if (which == 0) {
                showNoteDialog(true, notesList?.get(position), position)
            } else {
                deleteNote(notesList?.get(position)!!.id, position)
            }
        })
        builder.show()
    }

    private fun toggleEmptyNotes() {
        Log.v("my_tag", "inside toggleEmptyNotes")
        if (notesList?.isNotEmpty()!!) {
            noNotesView.visibility = View.GONE
        } else {
            noNotesView.visibility = View.VISIBLE
        }
    }

    /**
     * Showing a Snackbar with error message
     * The error body will be in json format
     * {"error": "Error message!"}
     */
    private fun showError(e: Throwable) {
        var message = ""
        try {
            if (e is IOException) {
                message = "No internet connection!"
            } else if (e is HttpException) {
                val error = e as HttpException
                val errorBody = error.response().errorBody()?.string()
                val jObj = JSONObject(errorBody)

                message = jObj.getString("error")
            }
        } catch (e1: IOException) {
            e1.printStackTrace()
        } catch (e1: JSONException) {
            e1.printStackTrace()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Check LogCat."
        }

        val snackbar = Snackbar
                .make(coordinatorLayout!!, message, Snackbar.LENGTH_LONG)

        val sbView = snackbar.view
        val textView: TextView = sbView.findViewById(android.support.design.R.id.snackbar_text)
        textView.setTextColor(Color.YELLOW)
        snackbar.show()
    }

    private fun whiteNotificationBar(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            var flags = view.getSystemUiVisibility()
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            view.setSystemUiVisibility(flags)
            window.statusBarColor = Color.WHITE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    companion object {
        private val TAG = "my_tag"
    }
}