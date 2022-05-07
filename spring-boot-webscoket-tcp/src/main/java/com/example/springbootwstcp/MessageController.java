//package com.example.springbootwstcp;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.SendTo;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Controller;
//
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//@Controller
//@EnableScheduling
//public class MessageController {
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    @MessageMapping("/hello")
//    @SendTo("/topic/greetings")
//    public String greeting(String message) throws Exception {
//        Thread.sleep(1000); // simulated delay
//        return message;
//    }
//
//    @Scheduled(fixedRate = 1000)
//    public void serverTime() throws Exception {
//        // 发送消息
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        messagingTemplate.convertAndSend("/topic/servertime", df.format(new Date()));
//    }
//}
