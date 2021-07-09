Signal과 Resignal

- Signal : 예외를 셋팅해서 던져주겠다는것(자바에서 throw new Exception("새롭게정의") 와 유사)
- Resignal : error or warning handler에서 사용
    - Signal과 유사하나 단독으로도 사용가능
    - 단독으로 사용할때는 전달받은 에러내용 그대로 전달(throw e 와 유사)

- 참고내용
  - MySQL RESIGNAL statement    
Besides the SIGNAL  statement, MySQL also provides the RESIGNAL  statement used to raise a warning or error condition.    
The RESIGNAL  statement is similar to SIGNAL  statement in term of functionality and syntax, except that:    
You must use the RESIGNAL  statement within an error or warning handler, otherwise, you will get an error message saying that “RESIGNAL when the handler is not active”. Notice that you can use SIGNAL  statement anywhere inside a stored procedure.
You can omit all attributes of the RESIGNAL statement, even the SQLSTATE value.
If you use the RESIGNAL statement alone, all attributes are the same as the ones passed to the condition handler.    
The following stored procedure changes the error message before issuing it to the caller.

```sql
DELIMITER $$

CREATE PROCEDURE Divide(IN numerator INT, IN denominator INT, OUT result double)
BEGIN
	DECLARE division_by_zero CONDITION FOR SQLSTATE '22012';

	DECLARE CONTINUE HANDLER FOR division_by_zero 
	RESIGNAL SET MESSAGE_TEXT = 'Division by zero / Denominator cannot be zero';
	-- 
	IF denominator = 0 THEN
		SIGNAL division_by_zero;
	ELSE
		SET result := numerator / denominator;
	END IF;
END
```


- 참고사이트 : https://www.mysqltutorial.org/mysql-signal-resignal/