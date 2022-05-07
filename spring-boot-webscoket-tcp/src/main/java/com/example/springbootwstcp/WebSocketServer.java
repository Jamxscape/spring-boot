package com.example.springbootwstcp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@ServerEndpoint("/ws/{uid}")
@Component
public class WebSocketServer {
    /**
     * 实时连接数量
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 会话容器
     */
    public static Map<Long,WebSocketServer> webSocketMap = new ConcurrentHashMap<>(128);

    /**
     * webSocket会话对象
     */
    private Session session;

    private static final ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    private String uid;

    @OnOpen
    public void onOpen(@PathParam("uid") String uid, Session session) {
        this.session = session;
        this.uid = uid;
        sessionMap.put(uid, session);
        try{
            this.sendMessage(this.uid + " hello connection is success");

        }catch (Exception e){
            log.error("连接失败");
            e.printStackTrace();
        }
    }

    /**
     * @Description 接收客户端消息
     * @param message 客户端传来的的消息
     * @param session 会话对象
     */
    @OnMessage
    public void onMessage(@PathParam("uid") String uid, String message, Session session) {
        if (StringUtils.isNotBlank(message)){
            try {
                this.sendMessageByUid(uid, message);
                log.info("收到来自:{} 的消息:{}", uid, message);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            log.info("消息不能为空");
        }
    }

    /**
     * @Description 关闭连接
     */
    @OnClose
    public void onClose() {
        log.info("有一连接关闭！当前在线用户数为");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("socket发生错误");
        error.printStackTrace();
    }

    /**
     * 服务器推送消息至客户端
     */
    public void sendMessage(String message) throws Exception {
        try {
            this.session.getBasicRemote().sendText(message);
        }catch (IOException e){
            log.error("消息异常 {}", e.getMessage());
        }
    }

    /**
     * 向指定用户发送消息
     */
    public void sendMessageByUid(String uid, String message) throws Exception {
        for (Map.Entry<String, Session> entry : sessionMap.entrySet()) {
            String userIdWithSession = entry.getKey().split("\\.")[0];
            Session session = entry.getValue();
            if (!userIdWithSession.equals(uid)) {
                if (session != null && session.isOpen()) {
                    this.sendMessage(message);
                }
            }
        }
    }

    /**
     * 发送群消息
     */

    /**
     * 获取当前在线用户数
     */
    public static int getOnlineCount() {
        return onlineCount.get();
    }

    /**
     * 在线用户数加1
     */
    public static void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    /**
     * 在线用户数减1
     */
    public static void subOnlineCount() {
        onlineCount.decrementAndGet();
    }

    /**
     * 定时消息
     */
    void sendScheduleMessage(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // 重写 run() 方法，返回系统时间
                try {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = df.format(new Date());
                    sendMessageByUid(uid, time);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Timer timer = new Timer();
        // 从现在开始每间隔 1000 ms 计划执行一个任务（规律性重复执行调度 TimerTask）
        timer.schedule(task, 0 ,1000);
    }

}
