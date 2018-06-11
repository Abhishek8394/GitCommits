package com.apbytes.gitcommits.githubHelpers;

/**
 * Created by Abhishek on 6/8/2018.
 * Represents a github client.
 * Can contain OauthToken or Username and password (not implemented).
 */

public class GithubClient {
    private String token;
    private static String baseUri = "";

    public GithubClient() {

    }

    public GithubClient(String token){
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    /**
     * Returns a repository based on provided repo owner name and repo name.
     * @param userName
     * @param repoName
     * @return
     */
    public GitRepo getRepository(String userName, String repoName){
        return new GitRepo(this, userName, repoName);
    }
}
