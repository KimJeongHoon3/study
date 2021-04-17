Base64Encoding vs UnicodeEncoding(like UTF-8..)

- Base64 
	- Base64 Encoding은 Binary Data를 Text로 변경하는 Encoding이다
	- 2에 6승으로 6비트씩 잘라서 계산하고 마지막에 부족한부분은 "=="으로 채워넣는 방식.. 결국 목적은 파일과같은 바이너리를 UnicodeEncoding 방식으로 변경하려하면 깨지기때문에, 바이트 자체를 Encoding해줌으로써 깨지는것을 막기위해 만들어짐! 즉, 이를 활용하여 문자열로 변경시켜줌!(물론 반대도가능)
	- 기존에 보내는것보다 33% 길이가 증가한다고함.. 그래서 만약 이미지파일과 같은 바이너리를 바로 바이트배열로 받을수있는것이면 굳이 사용할 필요는없음!

- UnicodeEncoding(Decoding)
	- UTF-8이나 다른 charset을 사용하여 유니코드값을 byte배열이나 String으로 변경해줌! 

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