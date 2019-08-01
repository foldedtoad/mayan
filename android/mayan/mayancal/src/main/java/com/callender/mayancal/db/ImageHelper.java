package com.callender.mayancal.db;
/*
 *
 */
public class ImageHelper {

    private String imageId;
    private byte[] imageByteArray;
    private byte[] soundByteArray;
    private String mayanText;
    private String latinText;

    public String getImageId() {
        return imageId;
    }
    public void setImageId(String imageId) { this.imageId = imageId; }

    public byte[] getImageByteArray() {
        return imageByteArray;
    }
    public void setImageByteArray(byte[] imageByteArray) { this.imageByteArray = imageByteArray; }

    public byte[] getSoundByteArray() {
        return soundByteArray;
    }
    public void setSoundByteArray(byte[] soundByteArray) {
        this.soundByteArray = soundByteArray;
    }

    public String getMayanText() { return mayanText; }
    public void setMayanText(String mayanText) { this.mayanText = mayanText; }

    public String getLatinText() { return latinText; }
    public void setLatinText(String latinText) { this.latinText = latinText; }
}