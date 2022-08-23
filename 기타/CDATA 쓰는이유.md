CDATA 쓰는이유

- (Unparsed) Character data 라는 뜻으로, CDATA로 감싸져있는곳은 어떤 문자열이던 파싱하지말라는것! 
  - html이나 mybatis에서 특정부호가 의미가 있기에, 이를 그냥쓰게되었을경우 사용자가 원하는대로 보여지지않거나 의도치않는 문제를 야기할수있는데, 이를 막기위해 파싱하지마라고 알려주는것!
- https://devscb.tistory.com/75