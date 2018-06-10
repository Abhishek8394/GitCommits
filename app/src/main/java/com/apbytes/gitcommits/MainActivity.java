package com.apbytes.gitcommits;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.apbytes.gitcommits.githubHelpers.GithubClient;
import com.apbytes.gitcommits.networking.CommitSynchronizer;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    CommitSynchronizer commitSynchronizer;
    GithubClient githubClient;
    private String repoUserName = "poynt";
    private String repoName = "PoyntSamples";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        commitSynchronizer = new CommitSynchronizer(this);
        githubClient = new GithubClient();
        refreshCommitList();
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    public void refreshCommitList(){
        Log.d(TAG, "Refreshing commit list");
        AsyncTaskHelper asyncTaskHelper = new AsyncTaskHelper();
        asyncTaskHelper.execute(repoUserName, repoName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_refresh){
            Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();
            refreshCommitList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getRepoUserName() {
        return repoUserName;
    }

    public void setRepoUserName(String repoUserName) {
        this.repoUserName = repoUserName;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    private final class AsyncTaskHelper extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            commitSynchronizer.syncCommits(githubClient, params[0], params[1]);
            return null;
        }
    }


}
