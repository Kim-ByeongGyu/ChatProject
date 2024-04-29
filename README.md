# 채팅 만들기 프로젝트
## 사용 설명서
포트 :  12345
클라이언트는 서버에 접속하면, 사용자는 닉네임을 입력받아 서버에 전송합니다.

서버는 사용자의 닉네임을 받고 "OOO 닉네임의 사용자가 연결했습니다."라고 출력합니다.

클라이언트가 접속하면 서버는 사용자의 IP 주소를 출력합니다.

사용자가 메시지를 입력하면 서버에 전송합니다.

메시지는 같은 방에 접속한 멤버만 공개합니다. (귓속말 제외)

### 💡명령어 모음

      방 목록 보기 : /list
      방 생성 : /create
      방 입장 : /join [방번호]
      방 나가기 : /exit
      접속종료 : /bye
      로그인 접속자 보기 : /users
      현재 방 접속자 보기 : /roomusers
      귓속말 : /w [닉네임] [메시지]
      
### list

"/list" 명령을 입력하면 서버는 생성된 모든 방의 목록을 출력합니다.

### create

클라이언트가 "/create"를 입력하면 서버는 새 방을 생성시킵니다.

> 채팅방 마다 생성될 때 채팅 내역이 저장되는 파일이 생성됩니다.

### join [방번호]

"/join [방번호]"를 통해 특정 방에 입장할 수 있습니다. 방에 입장하면, "닉네임이 방에 입장했습니다." 메시지를 전달합니다.

### exit

방에서 "/exit"를 입력하면, "님이 방을 나갔습니다." 메시지와 함께 로비로 이동합니다. 방에 아무도 남지 않으면 해당 방을 삭제하고 "방 번호 [방번호]가 삭제되었습니다."를 출력합니다.

### bye

사용자가 "/bye"를 입력하면 연결을 종료하고 프로그램을 종료합니다. 서버도 "OOO 닉네임의 사용자가 연결을 끊었습니다."를 출력하고 연결을 종료합니다.

### users

"/users" 명령으로 현재 접속 중인 모든 사용자의 목록을 볼 수 있습니다.

### roomusers

"/roomusers" 명령으로 현재 방에 있는 모든 사용자의 목록을 확인할 수 있습니다.

### wipsepr [닉네임] [메시지]

"/w [닉네임] [메시지]" 명령을 사용하여 특정 사용자에게만 메시지를 전송할 수 있습니다. 방 내에서도 같은 명령을 사용하여 특정 닉네임에게만 메시지를 보낼 수 있습니다.

### help

/help 명령을 사용하여 명령어를 볼 수 있습니다
