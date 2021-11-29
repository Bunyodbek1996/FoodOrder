package info.texnoman.foodorder.forretrofit;

import com.google.gson.annotations.SerializedName;

public class Token {
    @SerializedName("token")
    String token;

    public Token(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
