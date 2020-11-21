insert into user values(10001, sysdate(), 'Adam');
insert into user values(10002, sysdate(), 'Eva');
insert into user values(10003, sysdate(), 'Camilla');

insert into post values(11001, 'First post!', 10002);
insert into post values(11002, 'Woah!', 10001);
insert into post values(11003, 'Okay, I`m typing now... Gosh darn it! How do I delete this?', 10002);