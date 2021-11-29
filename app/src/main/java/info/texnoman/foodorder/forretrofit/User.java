package info.texnoman.foodorder.forretrofit;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("phone")
    long phone;
    @SerializedName("sms_code")
    int sms_code;

    public User(long phone, int sms_code) {
        this.phone = phone;
        this.sms_code = sms_code;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public int getSms_code() {
        return sms_code;
    }

    public void setSms_code(int sms_code) {
        this.sms_code = sms_code;
    }
}
