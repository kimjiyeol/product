CREATE TABLE product (
    pid INT PRIMARY KEY,
    pname VARCHAR(255),
    quantity INT,
    price DECIMAL(10, 2)
);

ALTER TABLE product ADD CONSTRAINT pid_pk PRIMARY KEY (pid);

CREATE SEQUENCE product_pid_seq
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;
