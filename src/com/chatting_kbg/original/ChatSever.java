package com.chatting_kbg.original;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatSever {
    private static void serverOn () {
        try (ServerSocket serverSocket = new ServerSocket(12345);
        ) {
            System.out.println("서버가 준비되었습니다.");
            //여러명의 클라이언트의 정보를 기억할 공간
            Map<String, PrintWriter> chatClients = new HashMap<>();
            Map<String, ChatRoom> chatRooms = new HashMap<>();

            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ChatThread(socket, chatClients, chatRooms)).start();
            } // while

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) {
        serverOn();
    }
}
