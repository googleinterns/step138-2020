package com.google.sps.data;

public class Post{
    private final String question;
    private final String answer;
    private final List<String> replies;

    public Post(String question, String answer, List<String> replies){
        this.question = question;
        this.answer = answer;
        this.replies = replies;
    }
}
