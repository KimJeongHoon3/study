jwt 정리

- jwt?
  - json web token 으로 json을 사용하여 정보를 암호화 (웹표준임 - RFC7519)
- jwt가 왜 나오게되었나
  - 세션 기반 인증방식이 보통 사용되었는데, 이는 서버에 해당 세션에 대한 데이터를 저장하고 잇어야함.. 
  - 서버 부하와 서버 확장시 고려해야하는 사항(세션은 특정 서버로 유지되어야함)들이 많아짐
  - 이를 해결하기 위해 나온 방식
- 구성 (나중에 세부내용들 더 추가해놓기)
  - header
    - 토큰의 타입과 암호화 알고리즘 정보를 담고 있음
  - payload
    - 큰이 전달할 실제 정보(claim)를 담고 있으며, 이는 보통 사용자의 인증 정보나, 데이터 권한 등이 있음
  - signature
    - header와 Payload를 각각 base64로 인코딩후, 이 둘을 합치고 secret key나 public key(or private key)로 암호화하여 signature을 생성
    

- [jwt 내용 좋음](https://velopert.com/2389)