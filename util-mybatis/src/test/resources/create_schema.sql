CREATE TABLE users(ID INT PRIMARY KEY AUTO_INCREMENT, USERNAME VARCHAR(20), AGE INT);
INSERT INTO users(USERNAME, AGE) VALUES('孤傲苍狼', 27);
INSERT INTO users(USERNAME, AGE) VALUES('白虎神皇', 27);


CREATE TABLE TEACHER(
    T_ID INT PRIMARY KEY AUTO_INCREMENT, 
    T_NAME VARCHAR(20)
);
CREATE TABLE T_CLASS(
    C_ID INT PRIMARY KEY AUTO_INCREMENT, 
    C_NAME VARCHAR(20), 
    TEACHER_ID INT
);
ALTER TABLE T_CLASS ADD CONSTRAINT fk_teacher_id FOREIGN KEY (TEACHER_ID) REFERENCES TEACHER(T_ID);    

INSERT INTO TEACHER(T_NAME) VALUES('teacher1');
INSERT INTO TEACHER(T_NAME) VALUES('teacher2');

INSERT INTO T_CLASS(C_NAME, TEACHER_ID) VALUES('class_a', 1);
INSERT INTO T_CLASS(C_NAME, teacher_id) VALUES('class_b', 2);