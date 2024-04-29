package com.chatting_kbg.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class ChatRoom {
    private String roomName;
    private Set<String> clients = new HashSet<>();
    private File chatLogFile;
    private PrintWriter logWriter;

    public ChatRoom(String roomName) {
        this.roomName = roomName;
        chatLogFile = new File(roomName + "_history.txt");
        try {
            logWriter = new PrintWriter(new FileWriter(chatLogFile, true), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoomName() {
        return roomName;
    }

    public Set<String> getClient() {
        return clients;
    }

    public void addClient(String nickname) {
        clients.add(nickname);
        logWriter.println(nickname + "님이 채팅방에 입장하였습니다.");
    }

    public void removeClient(String nickname) {
        clients.remove(nickname);
        logWriter.println(nickname + "님이 채팅방에서 나갔습니다.");
    }
    public void logChat(String message) {
        logWriter.println(message);
    }

    public void closeLogFile() {
        logWriter.close();
    }
}
