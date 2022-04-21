package com.github.wnebyte.workoutapp.provider

import java.lang.IllegalArgumentException
import java.lang.UnsupportedOperationException
import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.github.wnebyte.workoutapp.database.Repository

class MyContentProvider : ContentProvider() {

    private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, "set", 1)
        addURI(AUTHORITY, "exercise", 2)
        addURI(AUTHORITY, "workout", 3)
    }

    private lateinit var repository: Repository

    override fun onCreate(): Boolean {
        repository = when (Repository.isInitialized()) {
            true -> {
                Repository.get()
            }
            false -> {
                Repository.initialize(context!!)
                Repository.get()
            }
        }

        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        when (sUriMatcher.match(uri)) {
            1 -> {
                return repository.getSetsRaw()
            }
            2 -> {
                return repository.getExercisesRaw()
            }
            3 -> {
                return repository.getWorkoutsRaw()
            }
            else -> {
                throw IllegalArgumentException(
                    "Uri does not match a known table."
                )
            }
        }
    }

    override fun getType(uri: Uri): String {
        return when (sUriMatcher.match(uri)) {
            1 -> {
                "vnd.android.cursor.dir/com.github.wnebyte.workoutapp.free.provider.set"
            }
            2 -> {
                "vnd.android.cursor.dir/com.github.wnebyte.workoutapp.free.provider.exercise"
            }
            3 -> {
                "vnd.android.cursor.dir/com.github.wnebyte.workoutapp.free.provider.workout"
            }
            else -> {
                throw IllegalArgumentException(
                    "Uri does not match a known table."
                )
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException(
            "MyContentProvider does not provide support for insert operations."
        )
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException(
            "MyContentProvider does not provide support for delete operations."
        )
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException(
            "MyContentProvider does not provide support for update operations."
        )
    }

    companion object {
        const val AUTHORITY = "com.github.wnebyte.workoutapp.free.provider"
    }

}