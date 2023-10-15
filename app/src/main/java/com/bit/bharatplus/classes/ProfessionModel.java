package com.bit.bharatplus.classes;

public class ProfessionModel {
    String profession;
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
