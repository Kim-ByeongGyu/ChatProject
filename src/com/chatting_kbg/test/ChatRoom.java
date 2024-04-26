package com.chatting_kbg.test;

import java.util.ArrayList;
import java.util.List;

public class ChatRoom {
    private String roomName;
    private List<String> clients;

    public ChatRoom(String roomName) {
        this.roomName = roomName;
        this.clients = new ArrayList<>();
    }

    public String getRoomName() {
        return roomName;
    }

    public List<String> getClient() {
        return clients;
    }

    public void addClient(String nickname) {
        clients.add(nickname);
    }

    public void removeClient(String nickname) {
        clients.remove(nickname);
    }
}
