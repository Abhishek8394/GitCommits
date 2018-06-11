package com.apbytes.gitcommits;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.io.InvalidObjectException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    CommitSynchronizer commitSynchronizer;
    GithubClient githubClient;
    private String repoUserName = "poynt";
    private String repoName = "PoyntSamples";
    private NetworkStateBroadcastReceiver nsBroadcastReceiver;
    ConnectivityManager connectivitManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // setup stuff
        commitSynchronizer = new CommitSynchronizer(this);
        githubClient = new GithubClient();
        // fetch / refresh commit list.
        connectivitManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);


        if(isConnected()){
            refreshCommitList();
        }
        nsBroadcastReceiver = new NetworkStateBroadcastReceiver();
        this.registerReceiver(nsBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public boolean isConnected(){
        NetworkInfo activeNetwork = connectivitManager.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnected();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(!isConnected()){
            Toast.makeText(this, "Could not connect to the internet.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(nsBroadcastReceiver);
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
            if(isConnected()){
                Toast.makeText(getApplicationContext(), "Refreshing", Toast.LENGTH_LONG).show();
                refreshCommitList();
            }
            else{
                Toast.makeText(getApplicationContext(), "Connect to the internet first", Toast.LENGTH_LONG).show();
            }
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
            try {
                commitSynchronizer.syncCommits(githubClient, params[0], params[1]);
            } catch (InvalidObjectException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private final class NetworkStateBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "network connectivity change");
            if(isConnected()){
                MainActivity.this.refreshCommitList();
            }
        }
    }


}
