package com.neo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Random;


@RestController
public class HelloWorldController {
    // 初始时接收来的数据为空
    String receiveData = null;
	// 模拟从手机端接收的数据
//    @RequestMapping("/testReceive")
    // http://8.142.76.204:8080/test1?str=
    @RequestMapping("/test1")
    public String index(String str) {
        // 接收到消息后，模拟发送 $FACK0000002B
        System.out.println("接收到的字符为" + str);
        // 当接收的类型为05时，就不转发
        // 主协议
        if(str != null) {
            if(str.length()>40){
                String header = str.substring(32, 34);
                String type = str.substring(38, 40);
                if(header.equals("eb") && type.equals("05")){
                    // 这种情况丢弃
                }else {
                    receiveData = str;
                }
            }else if(str.startsWith("24505a5858")){
                // 当接收的协议为PZXX 不转发
            }else if(str.startsWith("244641434b")){
                // 当接收的协议为FACK 不转发
            }else{
                receiveData = str;
            }
        }
        return "244641434b0000002B";
    }

    // 模拟发送给手机端的数据
    // 主要完成两个任务
    // 1. 发送普通的状态信息
    // 2. 转发接收的消息，进行处理后再发送
//    @RequestMapping("/testSend")
    // http://8.142.76.204:8080/test2
    @RequestMapping("/test2")
    public String sendMessage(){
        if(receiveData != null&&receiveData.startsWith("2454585351")){
            String res = receiveData;
            // 将TXSQ转成TXXX
            String txxxStr = "2454585858";
            String temp = res.split("2454585351")[1];
            // 长度
            String length = temp.substring(0, 4);
            // 发送地址
            String cardID = temp.substring(4, 10);
            // 消息ID
            String messageID = temp.substring(10, 12);
            // 发送消息后面的内容
            String content = temp.substring(12);
            String sendMessage = txxxStr + length + messageID + cardID + content;
            receiveData = null;
            return sendMessage;
        }
        receiveData = null;
        return statusData();
    }
    // 自动生成状态数据
    public static String statusData(){
        String agreement = "245A54585800490101001214000105000000160101125538143C5B3C03040203000000000000000000";
        // "3C2D000000000000000000000000000000000000000000000"
        // "3b11513f361c2b1b540f464960401e52062126205137595e0
        // 模拟生成的通道信号值
        String signalStrength = "";
        for(int i=0;i<12;i++){
            signalStrength = signalStrength + hexRandom();
        }
        // "9320000000000000000000000000000"
        // "225540640600f45321c042c2b2f103f"
        // 模拟生成的波束值
        String locationStrength = "";
        for(int i=0;i<8;i++){
            locationStrength = locationStrength + hexRandom();
        }
        String xorResult = "09";
        String res = agreement + signalStrength + locationStrength + xorResult;
        return res;
    }
    // 模拟生成4个字符，都是16进制的
    public static String hexRandom(){
        Random r1 = new Random();
        Random r2 = new Random();
        int prn = r1.nextInt(100);
        int snr = r2.nextInt(100);
        String prnHex = Integer.toHexString(prn);
        if(prnHex.length() == 1){
            prnHex = "0" + prnHex;
        }
        String snrHex = Integer.toHexString(snr);
        if(snrHex.length() == 1){
            snrHex = "0" + snrHex;
        }
        String res = prnHex + snrHex;
        return res;
    }

    @RequestMapping("/apk")
    public String fileDownLoad(HttpServletResponse response, @RequestParam("fileName") String fileName){
        File file = new File("apk" +'/'+ fileName);
        if(!file.exists()){
            return "下载文件不存在";
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            System.out.println(e);
            return "下载失败";
        }
        return "下载成功";
    }

}