package pl.umk.chat.handlers;

/**
 * Created by Lukasz on 2015-01-28.
 */
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.umk.chat.entities.Packet;
import pl.umk.chat.enums.Type;
import pl.umk.chat.utils.HashMapHelper;
import pl.umk.chat.utils.JSONDecoder;
import pl.umk.chat.utils.JSONEncoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class MyHandler extends TextWebSocketHandler {

    static HashMap<String, WebSocketSession> usersMap=new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {
        String usersJson = JSONDecoder.buildJsonUsersData(usersMap);
        session.sendMessage(new TextMessage(usersJson));
    }

    @Override
    public void handleTextMessage(WebSocketSession userSession, TextMessage message) throws IOException{
        Packet packet = JSONEncoder.encodeIncoming(message.getPayload());

        if(packet.getText().equals("")) return;
        if(!usersMap.containsValue(userSession))
        {
            if(packet.getType()== Type.poke) return;
            if(usersMap.containsKey(packet.getText())){
                String warningJson= JSONDecoder.buildJsonMessageData("System", "This name is already registered.");
                userSession.sendMessage(new TextMessage(warningJson));
                return;
            }
            usersMap.put(packet.getText(), userSession);

            String helloJson= JSONDecoder.buildJsonMessageData("System", "you are now connected as " + packet.getText() + ".");
            userSession.sendMessage(new TextMessage(helloJson));

            Iterator<WebSocketSession> iterator = usersMap.values().iterator();
            String usersJson=JSONDecoder.buildJsonUsersData(usersMap);
            while (iterator.hasNext()){
                iterator.next().sendMessage(new TextMessage(usersJson));
            }
        }
        else
        {
            Iterator<WebSocketSession> iterator = usersMap.values().iterator();
            switch (packet.getType())
            {
                case message:
                    String messageJson = JSONDecoder.buildJsonMessageData(
                            HashMapHelper.getKeyByValue(usersMap, userSession),
                            packet.getText());
                    while (iterator.hasNext()) {
                        iterator.next().sendMessage(new TextMessage(messageJson));
                    }
                    break;
                case poke:
                    WebSocketSession session = usersMap.get(packet.getText());
                    String pokeJson = JSONDecoder.buildJsonMessageData(
                            "System",
                            HashMapHelper.getKeyByValue(usersMap, userSession) + " poked you");

                    session.sendMessage(new TextMessage(pokeJson));
                    break;
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session,
                                      CloseStatus status) {
        //Handle closing connection here
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws IOException{
        usersMap.values().remove(session);
        Iterator<WebSocketSession> iterator = usersMap.values().iterator();

        String usersJson = JSONDecoder.buildJsonUsersData(usersMap);

        while (iterator.hasNext()){
            iterator.next().sendMessage(new TextMessage(usersJson));
        }
    }

}