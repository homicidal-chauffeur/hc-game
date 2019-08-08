create table simulation_settings (
                             id serial8 primary key not null ,
                             simulation_id int8 references simulations not null,
                             ip varchar not null,
                             port int not null,
                             game_type varchar not null,
                             gamma double precision not null,
                             beta double precision not null,
                             max_velocity_pursuer double precision not null,
                             location_update_delay int4 not null,
                             game_time int4 not null,
                             created_at timestamptz not null default now()
);
create index sim_settings_sim_id_ind ON simulation_settings(simulation_id);
