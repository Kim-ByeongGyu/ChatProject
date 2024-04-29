package com.chatting_kbg.original;

import com.chatting_kbg.test.ServerMessageReader;

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
                        case "/roomusers":
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
}