DROP TABLE IF EXISTS `gProcesses`;
CREATE TABLE gProcesses (entity int not null AUTO_INCREMENT, owner char(255), process char(128), PRIMARY KEY(entity) );
INSERT INTO gProcesses VALUES (null,'test@example.com','13AG8i9');
INSERT INTO gProcesses VALUES (null,'test@example.com','15AG10i11');
INSERT INTO gProcesses VALUES (null,'test@example.com','16AG11i12');
INSERT INTO gProcesses VALUES (null,'test@example.com','17AG12i13');
INSERT INTO gProcesses VALUES (null,'test@example.com','19AG16i17');
INSERT INTO gProcesses VALUES (null,'test@example.com','20AG17i18');
INSERT INTO gProcesses VALUES (null,'test@example.com','9AG3i4');
INSERT INTO gProcesses VALUES (null,'test@example.com','10AG4i5');
INSERT INTO gProcesses VALUES (null,'test@example.com','11AG5i6');
INSERT INTO gProcesses VALUES (null,'test@example.com','12AG6i7');
INSERT INTO gProcesses VALUES (null,'test@example.com','12AG6i7tiny');
INSERT INTO gProcesses VALUES (null,'test@example.com','21AG18i19');
