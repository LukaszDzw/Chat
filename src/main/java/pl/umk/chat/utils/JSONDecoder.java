package pl.umk.chat.utils;

import org.springframework.web.socket.WebSocketSession;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Lukasz on 2015-01-27.
 */
public class JSONDecoder {
    public static String buildJsonUsersData(HashMap<String, WebSocketSession> usersMap){
        Iterator<String> iterator = usersMap.keySet().iterator();
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        while(iterator.hasNext())
        {
            jsonArrayBuilder.add(iterator.next());
        }
        return Json.createObjectBuilder().add("users", jsonArrayBuilder).build().toString();
    }

    public static String buildJsonMessageData(String username, String message)
    {
        JsonObject jsonObject = Json.createObjectBuilder().add("message", username+": "+message).build();
        StringWriter stringWriter = new StringWriter();
        try(JsonWriter jsonWriter = Json.createWriter(stringWriter)){
            jsonWriter.write(jsonObject);
        }

        return  stringWriter.toString();
    }
}
