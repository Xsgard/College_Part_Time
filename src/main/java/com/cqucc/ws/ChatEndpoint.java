package com.cqucc.ws;

import com.cqucc.entity.Message;
import com.cqucc.entity.User;
import com.cqucc.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", configurator = GetHttpSessionConfigurator.class)
@Component
public class ChatEndpoint {
    //用来存储每个用户客户端对象的ChatEndpoint对象
    private static final Map<String, ChatEndpoint> onLineUsers = new ConcurrentHashMap<>();

    //声明session对象，通过对象可以发送消息给指定的用户
    private Session session;

    //声明HttpSession对象，我们之前在HttpSession对象中存储了用户名
    private HttpSession httpSession;

    //返回在线用户名
    private Set<String> getNames() {
        return onLineUsers.keySet();
    }

    //建立连接
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        this.httpSession = httpSession;
        User user = (User) httpSession.getAttribute("user");
        String userName = user.getName();
        onLineUsers.put(userName, this);

        String message = MessageUtils.getMessage(true, null, getNames());
        broadcastAllUsers(message);
    }

    private void broadcastAllUsers(String message) {
        try {
            Set<String> names = onLineUsers.keySet();
            for (String name : names) {
                ChatEndpoint chatEndpoint = onLineUsers.get(name);
                chatEndpoint.session.getBasicRemote().sendText(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //收到消息
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Message mess = mapper.readValue(message, Message.class);
            String toName = mess.getToName();
            String data = mess.getMessage();
            User user = (User) httpSession.getAttribute("user");
            String resultMessage = MessageUtils.getMessage(false, user.getUsername(), data);
            //发送数据
            onLineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //关闭
    @OnClose
    public void onClose(Session session) {
        String username = (String) httpSession.getAttribute("user");
        //从容器中删除指定的用户
        onLineUsers.remove(username);
        MessageUtils.getMessage(true, null, getNames());
    }
}


