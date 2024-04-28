package com.chatting_kbg.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatRoom {
    private String roomName;
    private Set<String> clients = new HashSet<>();

    public ChatRoom(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomName() {
        return roomName;
    }

    public Set<String> getClient() {
        return clients;
    }

    public void addClient(String nickname) {
        clients.add(nickname);
    }

    public void removeClient(String nickname) {
        clients.remove(nickname);
    }
}
