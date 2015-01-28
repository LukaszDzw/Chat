<%--
  Created by IntelliJ IDEA.
  User: Lukasz
  Date: 2015-01-22
  Time: 11:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Chat</title>
    <script type="text/javascript">
      var websocket = new WebSocket("ws://localhost:8080/Chat_war_exploded/chatRoom");

      websocket.onmessage = function processMessage(message){
        var userBox = document.getElementById('userBox');
        var jsonData = JSON.parse(message.data);

        if(jsonData.message !=null) {
          messagesTextArea.value += jsonData.message + "\n";
          messagesTextArea.scrollTop=messagesTextArea.scrollHeight;
        }
        if(jsonData.users != null){
          userBox.innerHTML='';
          var i = 0;
          while (i<jsonData.users.length)
          {
            var user = jsonData.users[i++];
            addChatUser(user);
          }
        }
      };

      function addChatUser(name) {
        this.name = name;

        var userBox = document.getElementById('userBox');
        var newdiv=document.createElement("div");
        var button=document.createElement("button");

        button.onclick = function() {sendPoke(name)};
        button.style.backgroundImage= "url('lapka.png')";
        button.style.height="20";
        button.style.width="20";
        newdiv.innerHTML=name + " ";

        newdiv.appendChild(button);
        userBox.appendChild(newdiv);

      }

      function sendPoke(text)
      {
        var msg = new Object();
        msg.type="poke";
        msg.text=text;

        var jsonString=JSON.stringify(msg);

        websocket.send(jsonString);
      }

      function sendMessage(){
        var msg = new Object();
        msg.type="message";
        msg.text=messageText.value;

        var jsonString=JSON.stringify(msg);

        websocket.send(jsonString);
        messageText.value="";
      }

      window.onbeforeunload = function(){
        websocket.onclose = function(){};
        websocket.close();
      }
    </script>
  </head>
  <body>

    <textarea id="messagesTextArea" style="float:left; width: 450px; height: 150px;" readonly="readonly">Type your name and send to register.&#13;</textarea>
    <div id="userBox" style="float:left; border: 1px solid; border-color: #a9a9a9; height: 150px; width: 100px; overflow: scroll;"><p/></div><br/>
    <input type="text" id="messageText" style="width: 450px; clear:both; display: block; float:left;"/>    <input type="button" style="display: block; float:left;" value="Send" onclick="sendMessage();"/>
  </body>
</html>
