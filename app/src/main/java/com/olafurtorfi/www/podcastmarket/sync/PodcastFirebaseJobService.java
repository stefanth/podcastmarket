package com.olafurtorfi.www.podcastmarket.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by olitorfi on 15/02/2017.
 */

public class PodcastFirebaseJobService  extends JobService {

    private AsyncTask<Void, Void, Void> mFetchPodcastTask;

    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mFetchPodcastTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
//                SunshineSyncTask.syncPodcast(context);
                jobFinished(jobParameters, false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(jobParameters, false);
            }
        };

        mFetchPodcastTask.execute();
        return true;
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchPodcastTask != null) {
            mFetchPodcastTask.cancel(true);
        }
        return true;
    }
}