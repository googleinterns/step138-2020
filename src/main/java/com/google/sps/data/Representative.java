package com.google.sps.data;

import com.google.sps.data.Post;

public class Representative{
    private final String name;
    private final String title;
    private final List<Post> replies;

    public Representative(String name, String title, List<Post> replies){
        this.name = name;
        this.title = title;
        this.replies = replies;
    }
}
