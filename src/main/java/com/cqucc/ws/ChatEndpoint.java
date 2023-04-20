package com.cqucc.ws;

import com.alibaba.fastjson.JSON;
import com.cqucc.config.WebsocketConfig;
import com.cqucc.entity.Message;
import com.cqucc.entity.User;
import com.cqucc.entity.UserMessage;
import com.cqucc.service.UserMessageService;
import com.cqucc.service.UserService;
import com.cqucc.utils.MessageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/chat", configurator = WebsocketConfig.class)
@Component
@Slf4j
public class ChatEndpoint {

    private static UserService userService;

    /**
     * 解决@Autowired自动注入时UserService空指针异常
     * --（原因：WebSocket是多对象与Spring管理的单例模式冲突）
     * @param userService
     */
    @Autowired
    public void setApplicationContext(UserService userService) {
        ChatEndpoint.userService = userService;
    }

    //新建list集合存储数据
    private static ArrayList<UserMessage> MessageList = new ArrayList<>();
    //设置一次性存储数据的list的长度为固定值，每当list的长度达到固定值时，向数据库存储一次
    private static final Integer LIST_SIZE =3;
    public static UserMessageService userMessageService;
    @Autowired
    public void setUserMessageService(UserMessageService userMessageService) {
        ChatEndpoint.userMessageService = userMessageService;
    }

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
        this.httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        Long userId = (Long) httpSession.getAttribute("user");
        User user = userService.getById(userId);
        String userName = user.getName();
        onLineUsers.put(userName, this);

        String message = MessageUtils.getMessage(true, null, getNames());
        broadcastAllUsers(message);
        log.info("{}已上线",userName);
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
    public void onMessage(String message) {
        try {
//            ObjectMapper mapper = new ObjectMapper();
//            Message mess = mapper.readValue(message, Message.class);
            Message mess = JSON.parseObject(message, Message.class);
            String toName = mess.getToName();
            String data = mess.getMessage();
            Long userId = (Long) httpSession.getAttribute("user");
            User user = userService.getById(userId);
            String resultMessage = MessageUtils.getMessage(false, user.getName(), data);
            //发送数据
            onLineUsers.get(toName).session.getBasicRemote().sendText(resultMessage);

            //新建message对象，存储交流信息
            UserMessage message1 = new UserMessage();
            message1.setUsername(user.getName());
            message1.setToname(toName);
            message1.setMessage(data);
            message1.setCreatetime(String.valueOf(LocalDateTime.now()));
            //批量保存信息
            //将每条记录添加到list集合中
            MessageList.add(message1);
            //判断list集合长度
            if(MessageList.size() == LIST_SIZE){
                userMessageService.saveBatch(MessageList);
                //清空集合
                MessageList.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //关闭
    @OnClose
    public void onClose(Session session) {
        //判断list集合长度
        if(MessageList.size() <= LIST_SIZE){
            userMessageService.saveBatch(MessageList);
            //清空集合
            MessageList.clear();
        }
        Long userId = (Long) httpSession.getAttribute("user");
        User user = userService.getById(userId);
        String username = user.getUsername();
        //从容器中删除指定的用户
        onLineUsers.remove(username);
        log.info("{}已下线",username);
        MessageUtils.getMessage(true, null, getNames());
    }
}


