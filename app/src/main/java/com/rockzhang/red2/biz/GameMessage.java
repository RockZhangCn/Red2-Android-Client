package com.rockzhang.red2.biz;

import com.rockzhang.red2.log.VLog;
import org.json.JSONObject;

public class GameMessage {

    public static JSONObject fromString(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            VLog.debug("GameMessage.fromString " + obj.toString());
            return obj;
        } catch (Throwable t) {
            VLog.error("Could not parse malformed JSON: \"" + json + "\"");
            return null;
        }
    }
}
