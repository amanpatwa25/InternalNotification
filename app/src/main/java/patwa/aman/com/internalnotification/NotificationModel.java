package patwa.aman.com.internalnotification;

public class NotificationModel {

    public NotificationModel() {
    }

    public NotificationModel(String number, String mess) {
        this.number = number;
        this.mess = mess;
    }

    String number,mess;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }
}
