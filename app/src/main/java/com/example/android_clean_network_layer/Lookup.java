package com.example.android_clean_network_layer;

public class Lookup
{
    public  int id;
    public  String name;
    public  String description;
    public  boolean isDeleted;

    @Override
    public String toString() {
        return "Lookup{" +
                "id=" + id +
                ", name='" + name + '\''+
                ", description='" + description + '\'' +
                ", isDeleted=" + isDeleted  +
                '}';
    }
}
