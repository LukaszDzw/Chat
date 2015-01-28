package pl.umk.chat.utils;


import pl.umk.chat.entities.Packet;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

/**
 * Created by Lukasz on 2015-01-26.
 */
public class JSONEncoder {

    public static Packet encodeIncoming(String message)
    {
        JsonReader reader=Json.createReader(new StringReader(message));
        JsonObject parser = reader.readObject();

        Packet packet = new Packet();
        packet.setType(parser.getString("type"));
        packet.setText(parser.getString("text"));

        return packet;
    }
}
