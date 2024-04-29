package com.chatting_kbg.original;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private static boolean inRoom = false;
    private static String currentRoom = "";

    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 12345;

        Socket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try{
            socket = new Socket(hostName, portNumber);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner stdIn = new Scanner(System.in);

            System.out.print("Enter your nickname: ");
            String nickname = stdIn.nextLine();
            out.println(nickname); // 서버에 닉네임을 전송

            // 서버로부터 메시지를 읽어 화면에 출력하는 별도의 스레드
            Thread readThread = new Thread(new ServerMessageReader(in));
            readThread.start(); // 메시지 읽기 스레드 시작
            help();
            System.out.println("접속되었습니다.");
            // 사용자 입력 처리
            String userInput;
            while (true) {
                userInput = stdIn.nextLine();
                String[] commands = userInput.split(" ", 2);
                String command = commands[0];
                if (!inRoom) {
                    switch (command) {
                        case "/create":
                            out.println(userInput);
                            break;
                        case "/list":
                            out.println(userInput);
                            break;
                        case "/join":
                            inRoom = true;
                            out.println(userInput);
                            break;
                        case "/users":
                            out.println(userInput);
                            break;
                        case "/bye":
                            out.println(userInput);
                            socket.close();
                            return;
                        default:
                            System.out.println("방에 참여해주세요.");
                            break;
                    }
                } else {
                    switch (command) {
                        case "/exit":
                            out.println(userInput);
                            inRoom = false; // 채팅방에서 나갔음을 표시
                            currentRoom = ""; // 현재 방 초기화
                            continue;
                        case "/w ":
                            out.println(userInput);
                            break;
                        case "/users":
                            out.println(userInput);
                            break;
                        case "/roomusers":
                            out.println(userInput);
                            break;
                        default:
                            out.println(userInput);
                    }
                }
                if ("/help".equals(command)) {
                    help();
                }

            } // while
        } catch (IOException e) {
            System.out.println("Exception caught when trying to connect to " + hostName + " on port " + portNumber);
            e.printStackTrace();
        } finally {
            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void help() {
        System.out.println("언제든 가능한 명령어");
        System.out.println("\t명령어 모음 : /help");
        System.out.println("\t접속 유저 목록 : /users");
        System.out.println("로비 명령어");
        System.out.println("\t방 생성 : /create 방이름(중복 불가)");
        System.out.println("\t방 참가 : /join 방이름");
        System.out.println("\t방 목록 : /list");
        System.out.println("\t접속종료 : /bye");
        System.out.println("채팅방 명령어");
        System.out.println("\t귓속말 : /w 닉네임 할말");
        System.out.println("\t채팅방 유저 목록 : /roomusers");
        System.out.println("\t채팅방 나가기 : /exit");
    }
}