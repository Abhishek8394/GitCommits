package com.apbytes.gitcommits.githubHelpers;

import android.net.Uri;
import android.util.Log;

import com.apbytes.gitcommits.Utils.Utility;
import com.apbytes.gitcommits.networking.NetworkHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InvalidObjectException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Abhishek on 6/10/2018.
 * Contains a list of commits.
 * A good argument for making a class instead of using an array list everywhere:
 * - Can hold more data like branch.
 * - Can implement as hashmap cache if looking up specific commits is a frequent op.
 */

public class GitCommitList {
    private static final String TAG = "GitCommitList";
    GitRepo repo;
    GitBranch branch;
    ArrayList<GitCommit> commitList;

    public GitCommitList(GitRepo repo){
        this.repo = repo;
        this.commitList = new ArrayList<>();
        branch = null;
    }

    public GitCommitList(GitRepo repo, ArrayList<GitCommit> commitList) {
        this.repo = repo;
        this.commitList = commitList;
        branch = null;
    }

    /**
     * Fetched commits from network, parses them and stores them in `commitList`.
     * Upto the caller to make sure this call is made in a different thread.
     * @param params Contains key value pairs for query. See Github commits api docs
     *               at https://developer.github.com/v3/repos/commits/
     */
    public void fetchCommits(HashMap<String, String> params) throws JSONException, InvalidObjectException {
        Log.d(TAG, Uri.parse(GitConstants.GIT_BASE_URL).toString());
        Uri uri = Uri.parse(GitConstants.GIT_BASE_URL).buildUpon()
                .appendPath(GitConstants.REPOS_PATH)
                .appendPath(repo.getUserName())
                .appendPath(repo.getRepoName())
                .appendPath(GitConstants.COMMITS_PATH)
                .build();
        String result = NetworkHelper.makeRequest(uri.toString(), NetworkHelper.GET_METHOD, params, null);
        if(result == null){
            Log.d(TAG, "Failed to fetch commits.");
            throw new InvalidObjectException("Received null from API response.");
        }
        parseAndPopulateCommits(result);
    }

    /**
     * Convert a json object to GitCommit.
     * Note that we do this here because GitCommit should contain parser for when it fetches
     * a single commit object. But that doesnt work for our cause since we will exhaust our API.
     * So, we have a parser here that creates commit based on API response for commit list.
     * @param jsonObject EVERY SINGLE item in the array returned from GET /repos/:user/:repo/commits
     * @return
     */
    public GitCommit parseSingleCommit(JSONObject jsonObject) throws JSONException, ParseException {
        JSONObject commitJSON = jsonObject.getJSONObject("commit");
        String userName = commitJSON.getJSONObject("author").getString("name");
        String commitMessage = commitJSON.getString("message");
        String commitTimeStr = commitJSON.getJSONObject("author").getString("date");
        Date commitTime = Utility.stringToISODate(commitTimeStr);
        String commitHash = jsonObject.getString("sha");
        GitCommit commit = new GitCommit(userName, commitMessage, commitTime, commitHash);
        return commit;
    }

    /**
     * Parses JSON response from GET /repos/:user/:repo/commits
     * @param jsonResponse
     */
    public void parseAndPopulateCommits(String jsonResponse) throws JSONException {
        JSONArray response = new JSONArray(jsonResponse);
        // put whole thing in try. We either parse all or we stop on crash.
        try{
            for(int i=0; i<response.length(); i++) {
                GitCommit commit = parseSingleCommit(response.getJSONObject(i));
                commitList.add(commit);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public GitRepo getRepo() {
        return repo;
    }

    public void setRepo(GitRepo repo) {
        this.repo = repo;
    }

    public GitBranch getBranch() {
        return branch;
    }

    public void setBranch(GitBranch branch) {
        this.branch = branch;
    }

    public ArrayList<GitCommit> getCommitList() {
        return commitList;
    }

    public void setCommitList(ArrayList<GitCommit> commitList) {
        this.commitList = commitList;
    }
}
