mysql에서 'a'='a ' 가 true인 이유

- CHAR에서는 문자열을 비교할 때 공백(BLANK)을 채워서 비교하는 방법을 사용한다. 공백 채우기 비교에서는 우선 짧은 쪽의 끝에 공백을 추가하여 2개의 데이터가 같은 길이가 되도록 한다. 그리고 앞에서부터 한 문자씩 비교한다. 그렇기 때문에 끝의 공백만 다른 문자열은 같다고 판단된다. 그에 반해 VARCHAR 유형에서는 맨 처음부터 한 문자씩 비교하고 공백도 하나의 문자로 취급하므로 끝의 공백이 다르면 다른 문자로 판단한다. (SQL 전문가 가이드)
- 예를들어, CHAR(4)와 CHAR(6)에 들어가있는, 동일한 문자열은 아래와 같이 들어간다
  - CHAR(4) : 'ab__'
  - CHAR(6) : 'ab____'
  - 이를 PAD방식으로 비교하면
    - 'ab____'='ab____' => true
  - NOPAD 방식으로 비교하면
    - 'ab__'='ab____' => false
- MySQl 레퍼런스
  - When CHAR values are stored, they are right-padded with spaces to the specified length.
  - When CHAR values are retrieved, trailing spaces are removed unless the PAD_CHAR_TO_FULL_LENGTH SQL mode is enabled.
- char가 다르다고 내용이 같은데 공백을 패딩해주는것으로 인하여 비교시 false로 떨구면 non-sense!
- MySQL은 특별히 옵션을 주지않으면 padding은 알아서 만들어줌
- char 타입은 입력시, 지정한 사이즈에 맞추어 오른쪽에 padding을 넣어준다
- varchar 타입은 입력시, 입력된 데이터의 사이즈에 맞추어 insert 한다
- MySQL은 CHAR와 VARCHAR 모두 ***PAD 방식***으로 비교한다.(varchar인데 문자열 같으면 뒤에 공백이 더있어도 같은값..)
- 8.0 reference
  - To determine the pad attribute for a collation, use the INFORMATION_SCHEMA COLLATIONS table, which has a PAD_ATTRIBUTE column.
  - For nonbinary strings (CHAR, VARCHAR, and TEXT values), the string collation pad attribute determines treatment in comparisons of trailing spaces at the end of strings. NO PAD collations treat trailing spaces as significant in comparisons, like any other character. PAD SPACE collations treat trailing spaces as insignificant in comparisons; strings are compared without regard to trailing spaces. See Trailing Space Handling in Comparisons. The server SQL mode has no effect on comparison behavior with respect to trailing spaces.
- 참고 <br> <img src="https://woowabros.github.io/img/2018-02-26/mysql_char_table.jpg"></img>

- 정리 매우 굿(Postgre와 비교까지 잘되어있음) : https://woowabros.github.io/study/2018/02/26/mysql-char-comparison.html