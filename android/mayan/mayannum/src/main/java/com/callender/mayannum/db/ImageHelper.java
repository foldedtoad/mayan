package com.callender.mayannum.db;
/*
 *
 */
class ImageHelper {

    private String imageId;
    private byte[] imageByteArray;
    private byte[] soundByteArray;
    private String mayanText;
    private String latinText;

    String getImageId() {
        return imageId;
    }
    void setImageId(String imageId) { this.imageId = imageId; }

    byte[] getImageByteArray() {
        return imageByteArray;
    }
    void setImageByteArray(byte[] imageByteArray) { this.imageByteArray = imageByteArray; }

    byte[] getSoundByteArray() {
        return soundByteArray;
    }
    void setSoundByteArray(byte[] soundByteArray) {
        this.soundByteArray = soundByteArray;
    }

    String getMayanText() { return mayanText; }
    void setMayanText(String mayanText) { this.mayanText = mayanText; }

    String getLatinText() { return latinText; }
    void setLatinText(String latinText) { this.latinText = latinText; }
}