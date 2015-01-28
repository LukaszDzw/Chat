package websocket;

import entities.Packet;
import enums.Type;
import utils.HashMapHelper;
import utils.JSONDecoder;
import utils.JSONEncoder;

import javax.json.Json;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * Created by Lukasz on 2015-01-22.
 */

@ServerEndpoint("/chatRoom")
public class ChatRoom {
    static HashMap<String, Session> usersMap=new HashMap<>();

    @OnOpen
    public void handleOpen(Session userSession) throws IOException{
        String usersJson = JSONDecoder.buildJsonUsersData(usersMap);
        userSession.getBasicRemote().sendText(usersJson);
    }

    @OnMessage
    public void handleMessage(String message, Session userSession) throws IOException{

        Packet packet = JSONEncoder.encodeIncoming(message);

        if(packet.getText().equals("")) return;
        if(!usersMap.containsValue(userSession))
        {
            if(packet.getType()== Type.poke) return;
            if(usersMap.containsKey(packet.getText())){
                String warningJson= JSONDecoder.buildJsonMessageData("System", "This name is already registered.");
                userSession.getBasicRemote().sendText(warningJson);
                return;
            }
            usersMap.put(packet.getText(), userSession);

            String helloJson= JSONDecoder.buildJsonMessageData("System", "you are now connected as " + packet.getText() + ".");
            userSession.getBasicRemote().sendText(helloJson);

            Iterator<Session> iterator = usersMap.values().iterator();
            String usersJson=JSONDecoder.buildJsonUsersData(usersMap);
            while (iterator.hasNext()){
                iterator.next().getBasicRemote().sendText(usersJson);
            }
        }
        else
        {
            Iterator<Session> iterator = usersMap.values().iterator();
            switch (packet.getType())
            {
                case message:
                    String messageJson = JSONDecoder.buildJsonMessageData(
                            HashMapHelper.getKeyByValue(usersMap, userSession),
                            packet.getText());
                    while (iterator.hasNext()) {
                        iterator.next().getBasicRemote().sendText(messageJson);
                    }
                    break;
                case poke:
                    Session session = usersMap.get(packet.getText());
                    String pokeJson = JSONDecoder.buildJsonMessageData(
                            "System",
                            HashMapHelper.getKeyByValue(usersMap, userSession) + " poked you");

                    session.getBasicRemote().sendText(pokeJson);
                    break;
            }
        }
    }

    @OnClose
    public void handleClose(Session userSession) throws IOException
    {
        usersMap.values().remove(userSession);
        Iterator<Session> iterator = usersMap.values().iterator();

        String usersJson = JSONDecoder.buildJsonUsersData(usersMap);

        while (iterator.hasNext()){
            iterator.next().getBasicRemote().sendText(usersJson);
        }
    }
}
