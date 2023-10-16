package com.bit.bharatplus.models;

import com.google.gson.annotations.SerializedName;

public class ProfessionModel {
    @SerializedName("profession")
    String profession;
    @SerializedName("iconUrl")
    String iconURL;

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public ProfessionModel(String profession, String iconURL) {
        this.profession = profession;
        this.iconURL = iconURL;
    }

    public ProfessionModel() {
    }
}
