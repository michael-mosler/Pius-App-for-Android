package com.rmkrings.helper;

public class AppDefaults {

    static public String getBaseUrl() {
        return "https://pius-gateway-ng.eu-gb.mybluemix.net";
    }

    static private boolean authenticated;

    /*
     * Authenticated flag
     */
    static public boolean isAuthenticated() {
        return authenticated;
    }

    static public void setAuthenticated(boolean authenticated) {
        AppDefaults.authenticated = authenticated;
    }

    /*
     * Username and Password
     */
    static String username;
    static String password;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        AppDefaults.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        AppDefaults.password = password;
    }
}
