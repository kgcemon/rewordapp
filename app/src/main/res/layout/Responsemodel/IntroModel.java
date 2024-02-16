package com.app.earningpoints.Responsemodel;

public class IntroModel {
    private int Image;
    private String headLine, description;

    public IntroModel(int Image, String headLine, String description) {
        this.Image =Image;
        this.headLine = headLine;
        this.description = description;
    }

    public int getImage() {
        return Image;
    }

    public void setImage(int Image) {
        this.Image = Image;
    }

    public String getHeadLine() {
        return headLine;
    }

    public void setHeadLine(String headLine) {
        this.headLine = headLine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
