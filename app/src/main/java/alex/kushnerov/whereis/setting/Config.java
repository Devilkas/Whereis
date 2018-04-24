package alex.kushnerov.whereis.setting;

public class Config {

    public static final String DEFAUTL_LOGIN_URL = "login.php";
    public static final String DEFAUTL_COORDINATES_REQUEST_URL = "addToJson.php";
    public static final String DATA_URL = "Servers.json";

    private static String LOGIN_URL;
    private static String COORDINATES_REQUEST_URL;
    private static boolean IS_ON;

    public static final String KEY_EMAIL = "username";
    public static final String KEY_PASSWORD = "password";

    public static final String LOGIN_SUCCESS = "success";

    public static final String SHARED_PREF_NAME = "myloginapp";

    public static final String EMAIL_SHARED_PREF = "email";

    public static final String LOGGEDIN_SHARED_PREF = "loggedin";
    public static final String BUTTON_ON_OFF_SHARED_PREF = "buttononoffchecked";
    public static final String GIVE_SERVER_NAME_SHARED_PREF = "giveservername";


    public static final String TAG_NAME_SERVER = "nameServer";
    public static final String TAG_LOGIN = "url_login";
    public static final String URL_COORDINATS = "url_addCoordinats";

    public static final String JSON_ARRAY_SERVER = "Servers";

    public static final String SHARED_PREF_SERVERS = "myserversapp";
    public static final String SHARED_PREF_LOGIN_URL = "urllogin";
    public static final String SHARED_PREF_COORDINAT_URL = "urlcoordinat";
    public static final String SHARED_PREF_SELECTED_ITEM_STRING = "selecteditemstring";

    public static String getLoginUrl() {
        return LOGIN_URL;
    }

    public static void setLoginUrl(String loginUrl) {
        LOGIN_URL = loginUrl;
    }

    public static String getCoordinatesRequestUrl() {
        return COORDINATES_REQUEST_URL;
    }

    public static void setCoordinatesRequestUrl(String coordinatesRequestUrl) {
        COORDINATES_REQUEST_URL = coordinatesRequestUrl;
    }

    public static boolean isOn() {
        return IS_ON;
    }

    public static void setIsOn(boolean isOn) {
        IS_ON = isOn;
    }
}
