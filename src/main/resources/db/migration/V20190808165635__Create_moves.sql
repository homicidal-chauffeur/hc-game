create table moves (
  id serial8 primary key not null ,
  simulation_id int8 not null references simulations,
  pilot_settings_id int8 not null references pilot_settings,

  time int8 not null,

  my_theta double precision not null,
  opp_theta double precision not null,

  my_position_x double precision not null,
  my_position_y double precision not null,
  my_position_z double precision not null,

  opp_position_x double precision not null,
  opp_position_y double precision not null,
  opp_position_z double precision not null,

  my_orientation_x double precision not null,
  my_orientation_y double precision not null,
  my_orientation_z double precision not null,
  my_orientation_w double precision not null,

  opp_orientation_x double precision not null,
  opp_orientation_y double precision not null,
  opp_orientation_z double precision not null,
  opp_orientation_w double precision not null,

  created_at timestamptz not null default now()
);
create index moves_sim_id_ind ON moves(simulation_id);
create index moves_settings_id_ind ON moves(pilot_settings_id);
create index moves_time_ind ON moves(time);
