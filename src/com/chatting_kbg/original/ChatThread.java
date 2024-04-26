package com.chatting_kbg.original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

class ChatThread implements Runnable {
    //생성자를 통해서 클라이언트 소켓을 얻어옴.
    private Socket socket;
    private String nickname;
    private Map<String, PrintWriter> chatClients;

    private BufferedReader in;
    PrintWriter out;

    public ChatThread(Socket socket, Map<String, PrintWriter> chatClients) {
        this.socket = socket;
        this.chatClients = chatClients;

        //클라이언트가 생성될 때 클라이언트로 부터 아이디를 얻어오게 하고 싶어요.
        //각각 클라이언트와 통신 할 수 있는 통로얻어옴.
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String clientIP = socket.getInetAddress().getHostAddress();
            //Client가 접속하자마 id를 보낸다는 약속!!
            nickname = in.readLine();
            //이때..  모든 사용자에게 id님이 입장했다라는 정보를 알려줌.
            broadcast(nickname + "님이 입장하셨습니다.");
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
                if (msg.startsWith("/bye")) {
                    System.out.println(nickname + "닉네임의 사용자가 연결을 끊었습니다.");
                    break;
                }
                if (msg.startsWith("/w ")) {
                    whisper(msg);
                } else {
                    broadcast(nickname + " : " + msg);
                }
            }

        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            synchronized (chatClients) {
                chatClients.remove(nickname);
            }
            broadcast(nickname + "님이 채팅에서 나갔습니다.");

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
    public void broadcast(String msg) {
        for (PrintWriter out : chatClients.values()) {
            out.println(msg);
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
            receiverPW.println("[귓속말] " + nickname + " : " + message);
            out.println("[귓속말] " + name + "님에게 전송: " + message); // 보내는 클라이언트에게도 메시지 전송 확인
        } else if (this.nickname.equals(name)) {
            out.println("오류!! : 본인에게는 귓속말을 보내실 수 없습니다.");
        } else {
            out.println(name + "님을 찾을 수 없습니다.");
        }
    }
}