package com.example.android.popularmovieapp;

/**
 * This is a useful callback mechanism so we can abstract our AsyncTasks out into separate, re-usable
 * and testable classes yet still retain a hook back into the calling activity. Basically, it'll make classes
 * cleaner and easier to unit test.
 *
 * @param <MovieData>
 */
public interface AsyncTaskCompleteListener<MovieData> {
    /**
     * Invoked when the AsyncTask has completed its execution.
     *
     * @param result     The resulting object from the AsyncTask.
     * @param totalPages the total pages to load
     */
    void onTaskComplete(MovieData[] result, int totalPages);
}
