create table pilot_settings (
                             id serial8 primary key not null ,
                             simulation_settings_id int8 not null references simulation_settings,
                             name varchar not null,
                             action_type varchar not null,
                             pilot_strategy varchar not null,
                             color varchar not null,
                             velocity_type varchar not null,
                             turning_radius double precision not null,
                             pilot_delay int4 not null,
                             created_at timestamptz not null default now()
);
create index pilot_settings_sim_set_id_ind ON pilot_settings(simulation_settings_id);
create index pilot_settings_name_ind ON pilot_settings(name);
