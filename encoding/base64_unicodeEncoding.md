Base64Encoding vs UnicodeEncoding(like UTF-8..)

- Base64 
	- Base64 Encoding은 Binary Data를 Text로 변경하는 Encoding이다
	- 6비트씩 잘라서 Base64색인표에 나와있는대로 치환.. 마지막에 부족한부분은 "=="으로 채워넣는 방식.. 결국 목적은 파일과같은 바이너리를 UnicodeEncoding 방식으로 변경하려하면 깨지기때문에, 바이트 자체를 Encoding해줌으로써 깨지는것을 막기위해 만들어짐! 즉, 이를 활용하여 문자열로 변경시켜줌!(물론 반대도가능)
	- 기존에 보내는것보다 33% 길이가 증가한다고함.. 그래서 만약 이미지파일과 같은 바이너리를 바로 바이트배열로 받을수있는것이면 굳이 사용할 필요는없음!

- UnicodeEncoding(Decoding)
	- ~~UTF-8이나 다른 charset을 사용하여 유니코드값을 byte배열이나 String으로 변경해줌! ~~
	- 컴퓨터는 사람이 사용하는 문자를 알아볼수 없기에, 일종의 수많은 문자와 코드를 매핑시켜놓은 것이 유니코드
	- 이 유니코드를 몇 bit씩을 기준으로 인코딩을 할것인지에 따라 UTF-8, UTF-16 등이 된다. (즉, 동일한 유니코드지만 UTF-8과 UTF-16은 인코딩되어 나타나는 바이트 수가 다를수 있다..! 당연 bit도 다를수있고.. 예를들어 UTF-8은 한글은 3바이트인데, UTF-16은 2바이트..) 
      - Unicode Tranformation Format - N-bit 
	- [유니코드 인코딩 관련 참고하기 좋은사이트](https://luv-n-interest.tistory.com/1369#:~:text=%ED%95%9C%EA%B8%80%EC%9D%B4%203%EB%B0%94%EC%9D%B4%ED%8A%B8%EB%A1%9C,%EB%A9%94%EB%AA%A8%EB%A6%AC%EB%A5%BC%20%EC%A4%84%EC%9D%BC%20%EC%88%98%20%EC%9E%88%EB%8B%A4.)
	- [나무위키](https://namu.wiki/w/UTF-8)

- 위 둘의 비교자체가 사실 잘못된것이긴한데, Base64의 대상은 바이트자체인것이고, UnicodeEncoding의 대상은 Unicode인것이다!

- 참고 글
	- UTF-8 and UTF-16 are methods to encode Unicode strings to byte sequences.
		- See: The Absolute Minimum Every Software Developer Absolutely, Positively Must Know About Unicode and Character Sets (No Excuses!) (http://www.joelonsoftware.com/articles/Unicode.html)
	- Base64 is a method to encode a byte sequence to a string.
	- So, these are widely different concepts and should not be confused.
	- Things to keep in mind:
		- Not every byte sequence represents an Unicode string encoded in UTF-8 or UTF-16.
		- Not every Unicode string represents a byte sequence encoded in Base64.
	- 출처 : https://stackoverflow.com/questions/3866316/whats-the-difference-between-utf8-utf16-and-base64-in-terms-of-encoding
	
- Base 64 설명굿 : https://effectivesquid.tistory.com/entry/Base64-%EC%9D%B8%EC%BD%94%EB%94%A9%EC%9D%B4%EB%9E%80