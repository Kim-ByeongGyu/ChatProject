package com.chatting_kbg.original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;
import java.util.Set;

class ChatThread implements Runnable {
    //생성자를 통해서 클라이언트 소켓을 얻어옴.
    private Socket socket;
    private String nickname;
    private Map<String, PrintWriter> chatClients;
    private Map<String, ChatRoom> chatRooms;
    private boolean inRoom = false;
    private String roomName = "";

    private BufferedReader in;
    PrintWriter out;

    public ChatThread(Socket socket, Map<String, PrintWriter> chatClients, Map<String, ChatRoom> chatRooms) {
        this.socket = socket;
        this.chatClients = chatClients;
        this.chatRooms = chatRooms;


        //클라이언트가 생성될 때 클라이언트로 부터 아이디를 얻어오게 하고 싶어요.
        //각각 클라이언트와 통신 할 수 있는 통로얻어옴.
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientIP = socket.getInetAddress().getHostAddress();
            //Client가 접속하자마 id를 보낸다는 약속!!
            nickname = in.readLine();
            //이때..  모든 사용자에게 id님이 입장했다라는 정보를 알려줌.
            System.out.println(clientIP);
            System.out.println(nickname + "닉네임의 사용자가 연결했습니다.");
            //동시에 일어날 수도..
            synchronized (chatClients) {
                chatClients.put(this.nickname, out);
            }


        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //연결된 클라이언트가 메시지를 전송하면, 그 메시지를 받아서 다른 사용자들에게 보내줌..
        String msg = null;
        try {
            while ((msg = in.readLine()) != null) {
                if (!inRoom) {
                    if (msg.startsWith("/create")) {
                        createRoom(msg);
                    } else if (msg.startsWith("/join")) {
                        joinRoom(msg);
                    } else if (msg.startsWith("/list")) {
                        for (ChatRoom room : chatRooms.values()) {
                            out.println(room.getRoomName());
                        }
                    } else if (msg.equals("/users")) {
                        listUsers();
                    }
                    if (msg.startsWith("/bye")) {
                        System.out.println(nickname + "님이 연결을 끊었습니다.");
                        break;
                    }
                } else {
                    if (msg.startsWith("/w ")) {
                        whisper(msg);
                    } else if (msg.startsWith("/exit")) {
                        exitRoom(msg);
                    } else if (msg.equals("/users")) {
                        listUsers();
                    } else if (msg.equals("/roomusers")) {
                        listRoomUsers();
                    } else {
                        broadcast(nickname + " : " + msg, chatRooms.get(roomName));
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            synchronized (chatClients) {
                chatClients.remove(nickname);
            }
            broadcast(nickname + "님이 채팅에서 나갔습니다.", chatRooms.get(roomName));

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    //전체 사용자에게 알려주는 메서드
    public void broadcast(String msg, ChatRoom chatRoom) {
//        Set<String> clientsInRoom = chatRoom.getClient();
//        for (String participant : clientsInRoom) {
//            PrintWriter participantWriter = chatClients.get(participant);
//            if (participantWriter != null) {
//                participantWriter.println(msg);
//                chatRooms.get(roomName).logChat(msg);
//            }
//        }
        if (chatRoom != null) { // chatRoom이 null이 아닌 경우에만 실행
            Set<String> clientsInRoom = chatRoom.getClient();
            for (String participant : clientsInRoom) {
                PrintWriter participantWriter = chatClients.get(participant);
                if (participantWriter != null) {
                    participantWriter.println(msg);
                    chatRoom.logChat(msg);
                }
            }
        }
    }

    // 귓속말
    public void whisper(String msg) {
        String[] whisperMsg = msg.split(" ", 3);

        if (whisperMsg.length < 3) {
            out.println("오류!! : 잘못된 형식( /w 수신자이름 메시지 )");
            return;
        }

        String name = whisperMsg[1];
        String message = whisperMsg[2];

        PrintWriter receiverPW = chatClients.get(name); // 수신자의 PrintWriter

        if (receiverPW != null && !this.nickname.equals(name)) { // 수신자가 존재하면 메시지 전송
//            out.println("[귓속말] " + name + "님에게 전송: " + message); // 보내는 클라이언트에게도 메시지 전송 확인
            receiverPW.println("[귓속말] " + nickname + " : " + message);
        } else if (this.nickname.equals(name)) {
            out.println("오류!! : 본인에게는 귓속말을 보내실 수 없습니다.");
        } else {
            out.println(name + "님을 찾을 수 없습니다.");
        }
    }
    public void createRoom(String msg) {
        String[] parts = msg.split(" ");
        if (parts.length < 2) {
            out.println("오류!! : 잘못된 형식( /create 방이름 )");
            return;
        }

        String roomName = parts[1];
        if (chatRooms.containsKey(roomName)) {
            out.println("이미 존재하는 방 이름입니다.");
            return;
        }

        ChatRoom newRoom = new ChatRoom(roomName);
        chatRooms.put(roomName, newRoom);
        out.println(roomName + " 방이 생성되었습니다.");
    }

    public void joinRoom(String msg) {
        String[] parts = msg.split(" ");
        if (parts.length < 2) {
            out.println("오류!! : 잘못된 형식( /join 방이름 )");
            return;
        }

        String roomName = parts[1];
        ChatRoom room = chatRooms.get(roomName);
        if (room == null) {
            out.println("존재하지 않는 방 이름입니다.");
            return;
        }
        inRoom = true;
        room.addClient(nickname);
        roomName = parts[1]; // 방 이름 설정
        out.println(roomName + "님이 방에 입장했습니다.");
        this.roomName = roomName; // 현재 방 이름 설정

    }
    public void exitRoom (String msg) {
        inRoom = false;
        out.println("방 나가기 완료");
        ChatRoom room = chatRooms.get(roomName);
        if (room != null) {
            room.removeClient(nickname); // 채팅방에서 클라이언트 제거
            broadcast(nickname + "님이 채팅방을 나갔습니다.", room); // 다른 클라이언트에게 알림
        }
        this.roomName = "";
    }

    public void listUsers() {
        out.println("현재 접속 중인 사용자 목록:");
        for (String user : chatClients.keySet()) {
            out.println(user);
        }
    }

    public void listRoomUsers() {
        if (!inRoom) {
            out.println("방에 참여 중이 아닙니다.");
            return;
        }
        ChatRoom room = chatRooms.get(roomName);

        out.println("현재 방에 있는 사용자 목록:");
        for (String user : room.getClient()) {
            out.println(user);
        }
    }
}