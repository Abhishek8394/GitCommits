package com.apbytes.gitcommits;

import com.apbytes.gitcommits.networking.NetworkHelper;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Abhishek on 6/8/2018.
 */

public class NetworkTester {
    @Test
    public void test_getRequest(){
        String url = "https://api.github.com/repos/tensorflow/tensorflow/commits";
        String response = NetworkHelper.makeRequest(url, "GET", null);
        Assert.assertTrue(response.trim().length() > 0);
    }
}
