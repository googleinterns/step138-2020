package com.google.sps.data;

import java.util.Objects;

public final class Tab{
    private final String name;
    private final String platform;
    private final long id;

    public Tab(String name, String platform, long id){
        this.name = name;
        this.platform = platform;
        this.id = id; 
    }

    public String getTabName(){
        return name; 
    }

    public String getPlatform(){
        return platform; 
    }

    public long getID(){
        return id; 
    }

    @Override
    public boolean equals(Object o) {   
        if (!(o instanceof Tab)) { 
            return false; 
        } 
        Tab that = (Tab) o;
        return that.getPlatform().equals(this.platform)
            && that.getTabName().equals(this.name);
    } 

    @Override 
    public int hashCode() {
        return Objects.hash(name, platform);
    }
    
    @Override 
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tab name: ").append(name).append(System.getProperty("line.separator")); 
        sb.append("Platform: ").append(platform).append(System.getProperty("line.separator")); 
        return sb.toString();
    }
}
