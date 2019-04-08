package br.com.softbox.atest_bdd.pages;


import com.google.gson.annotations.SerializedName;

/**
 * Maps the ProductPage entry from a product.json file settings.
 */
public class ProductConfigEntry {
    @SerializedName("type_name")
    public String type_name;

    @SerializedName("custom_name")
    public String custom_name;

    @SerializedName("alternative_type_names")
    public String[] alternative_type_names;

    @SerializedName("custom_number")
    public String custom_number;

    @SerializedName("can_have_color")
    public String can_have_color;

    @SerializedName("can_have_size")
    public String can_have_size;

    @SerializedName("custom_name_front")
    public String custom_name_front;
    
    @SerializedName("custom_name_left")
    public String custom_name_left;

    @SerializedName("custom_name_right")
    public String custom_name_right;

}

