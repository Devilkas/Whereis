package alex.kushnerov.whereis.setting;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alex on 22.02.2017.
 */

public class CoordinatesToJsonRequest extends StringRequest {
    private Map<String, String> params;

    public CoordinatesToJsonRequest(String username, String day, String time, String lat, String lon, Response.Listener<String> listener) {
        super(Method.POST, Config.getCoordinatesRequestUrl(), listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("day", day);
        params.put("time", time);
        params.put("latitude", lat);
        params.put("longitude", lon);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
