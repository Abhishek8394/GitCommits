package com.apbytes.gitcommits.githubHelpers;

/**
 * Created by Abhishek on 6/10/2018.
 * Denotes a git branch, Dont really use this in the project.
 * Just a stub.
 */

public class GitBranch {
    GitRepo repo;
    private String name;

    public GitBranch(GitRepo repo, String name) {
        this.repo = repo;
        this.name = name;
    }

    public GitRepo getRepo() {
        return repo;
    }

    public void setRepo(GitRepo repo) {
        this.repo = repo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
