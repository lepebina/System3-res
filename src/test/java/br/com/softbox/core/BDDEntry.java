package br.com.softbox.core;


import com.google.gson.annotations.SerializedName;

/**
 * Maps the BDD entry from a bdd.json file settings.
 */
public class BDDEntry {
    @SerializedName("pkg")
    public String pkg;

    @SerializedName("feature")
    public String feature;

    @SerializedName("test")
    public String test;

    @SerializedName("stepdefs")
    public String stepdefs;

    @SerializedName("title")
    public String title;
}
