/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author Work
 */
public class UploadedImage {
    
    private String imageInString;
    private String title;
    private String price;
    private String format;

    public UploadedImage() {
    }
    
    public String getImageInString() {
        return imageInString;
    }

    public void setImageInString(String imageInBytes) {
        this.imageInString = imageInBytes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
    
    
}
