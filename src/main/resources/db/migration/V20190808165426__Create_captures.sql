create table captures (
     id serial8 primary key not null ,
     simulation_id int8 not null references simulations,

     time int8 not null,
     distance double precision not null,
     created_at timestamptz not null default now()
);
create index captures_sim_id_ind ON captures(simulation_id);
create index captures_time_ind ON captures(time);
