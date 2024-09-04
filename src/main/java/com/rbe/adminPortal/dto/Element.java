package com.rbe.adminPortal.dto;

import java.sql.Blob;
import java.util.List;

public class Element {
    String text;
    List<Blob> image;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Blob> getImage() {
        return image;
    }

    public void setImage(List<Blob> image) {
        this.image = image;
    }
}
