show engine innodb status


Create table innodb_lock_monitor (fd1 int) engine=innodb;


SELECT @@GLOBAL.tx_isolation, @@tx_isolation, @@session.tx_isolation;

/*
REPEATABLE-READ  =>  READ-COMMITTED

왜?


*/
SET tx_isolation = 'REPEATABLE-READ';

SET GLOBAL tx_isolation = 'REPEATABLE-READ';