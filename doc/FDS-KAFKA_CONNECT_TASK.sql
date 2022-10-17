-- Create table
create table KAFKA_CONNECT_TASK
(
  id                       NUMBER(20),
  name                     VARCHAR2(100),
  topic_prefix             VARCHAR2(20),
  table_name               VARCHAR2(200),
  timestamp_initial        DATE,
  poll_interval_ms         NUMBER(20),
  topic                    VARCHAR2(50),
  status                   VARCHAR2(10),
  sys_isvalid              VARCHAR2(1),
  creater_name             VARCHAR2(30),
  create_time              DATE,
  update_time              DATE
)
tablespace USERS
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

-- Create/Recreate indexes 
create index IND1_KAFKA_CONNECT_TASK on KAFKA_CONNECT_TASK (name)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );

create index IND2_KAFKA_CONNECT_TASK on KAFKA_CONNECT_TASK (status)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );


create index IND3_KAFKA_CONNECT_TASK on KAFKA_CONNECT_TASK (sys_isvalid)
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );


-- Create/Recreate primary, unique and foreign key constraints 
alter table KAFKA_CONNECT_TASK
  add constraint PK_KAFKA_CONNECT_TASK primary key (ID)
  using index 
  tablespace USERS
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    next 1M
    minextents 1
    maxextents unlimited
  );



CREATE SEQUENCE T_KAFKA_CONNECT_TASK_SEQ
MINVALUE 1 
NOMAXVALUE 
INCREMENT BY 1 
START WITH 1 NOCACHE ;
