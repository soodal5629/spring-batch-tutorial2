insert into address(address_id, location, id) values(1, 'location1', 1);
insert into address(address_id, location, id) values(2, 'location2', 2);
insert into address(address_id, location, id) values(3, 'location3', 3);
insert into address(address_id, location, id) values(4, 'location4', 4);
insert into address(address_id, location, id) values(5, 'location5', 5);
insert into address(address_id, location, id) values(6, 'location6', 6);
insert into address(address_id, location, id) values(7, 'location7', 7);
insert into address(address_id, location, id) values(8, 'location8', 8);
insert into address(address_id, location, id) values(9, 'location9', 9);
insert into address(address_id, location, id) values(10, 'location10', 10);

update batch_job_execution set STATUS = 'FAILED', exit_code = 'FAILED' WHERE job_execution_id = 190;
UPDATE batch_step_execution SET STATUS = 'FAILED', exit_code = 'FAILED' WHERE step_execution_id = 265;