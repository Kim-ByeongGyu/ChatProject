package com.chatting_kbg.test;

import com.chatting_kbg.original.ServerMessageReader;

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
                    if ("/create".equals(command)) {
                        out.println(userInput); // 서버에 방 생성 요청 전송
                    } else if ("/join".equals(command)) {
                        out.println(userInput); // 서버에 방 참여 요청 전송
                        currentRoom = commands[1];
                        inRoom = true;
                    } else if ("/list".equals(userInput)) {
                        out.println(userInput);
                    } else {
                        System.out.println("방에 참여해주세요.");
                    }
                } else {
                    if (userInput.startsWith("/bye")) {
                        out.println(userInput);
                        break;
                    }
                    if (userInput.startsWith("/w ")) {
                        out.println(userInput);
                    }
                        out.println(currentRoom + " " + userInput); // 자신의 메시지를 서버로 보내지 않음

                }

            } // while

            // 클라이언트와 서버는 명시적으로 close를 합니다. close를 할 경우 상대방쪽의 readLine()이 null을 반환됩니다. 이 값을 이용하여 접속이 종료된 것을 알 수 있습니다.
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            System.out.println("Exception caught when trying to connect to " + hostName + " on port " + portNumber);
            e.printStackTrace();
        }
    }
}