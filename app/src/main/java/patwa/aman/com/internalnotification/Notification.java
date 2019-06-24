package patwa.aman.com.internalnotification;

import java.util.ArrayList;

public class Notification {

    String message,mobile,id;
    ArrayList<String> images;

    public Notification(String message, String mobile, String id, ArrayList<String> images) {
        this.message = message;
        this.mobile = mobile;
        this.id = id;
        this.images = images;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
