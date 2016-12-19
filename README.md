## 16-3 네트워크 기말 프로젝트
> 소켓 프로그래밍으로 다대다 채팅을 구현한다.

### compile & installation guide

> OpenSSL은 이미 설치되어있다고 가정합니다.

### Installation
```
mkdir snackchat
cd snackchat
git clone https://github.com/bbq923/snackchat.git
```

### Compile
```
javac DantokServer.java
javac DantokClient.java
```
---

### User guide
1.다음 명령어로 SSL 키를 생성합니다.
```
keytool -genkey -keystore mySrvKeystore -keyalg RSA
```
2. javac 명령어로 DantokServer.java와 DantokClient.java를 컴파일 합니다.
```
javac DantokServer.java
javac DantokClient.java
```
3. 생성한 keyStore와 server의 port를 인자로 넘겨주며 DantokServer를 우선 실행시킵니다.
```
java -Djavax.net.ssl.keyStore=mySrvKeystore -Djavax.net.ssl.keyStorePassword=123456 DantokServer [port]
```
4. 호스트 아이피, 포트 번호, 유저 아이디를 인자로 넘겨주며 DantokClient를 실행시킵니다.
```
java -Djavax.net.ssl.trustStore=mySrvKeystore -Djavax.net.ssl.trustStorePassword=123456 DantokClient [host_ip] [port] [user_id]
```

### Bug report
방에 참가할 때 누가 참가중인지를 알 수 있는데 마지막 사람의  뒤에도 콤마(,)가 붙습니다. 사소하지만 신경쓰입니다. 
