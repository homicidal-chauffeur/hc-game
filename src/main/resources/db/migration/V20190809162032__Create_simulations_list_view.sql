DROP TYPE IF EXISTS pilot_settings_preview CASCADE;
CREATE TYPE pilot_settings_preview AS (
    name varchar,
    action_type varchar,
    pilot_strategy varchar,
    velocity_type varchar,
    turning_radius double precision,
    pilot_delay int4
    );

DROP VIEW IF EXISTS simulation_results;
CREATE VIEW simulation_results AS
    SELECT s.id, s.date_at, s.tags,
           ss.gamma, ss.beta, ss.max_velocity_pursuer, ss.location_update_delay, ss.game_time,

           (SELECT array_agg(t::pilot_settings_preview) FROM (
               SELECT ps.name, ps.action_type, ps.pilot_strategy, ps.velocity_type, ps.turning_radius, ps.pilot_delay
               FROM pilot_settings ps
               WHERE ps.simulation_settings_id = ss.id
               ORDER BY action_type, name
             ) AS t
           ) AS pilot_settings,

           (SELECT count(*) FROM captures c WHERE c.simulation_id = s.id) AS captures_count,
           (SELECT MIN(time) FROM captures c WHERE c.simulation_id = s.id) AS first_capture



    FROM simulations s
        LEFT JOIN simulation_settings ss ON ss.simulation_id = s.id
    ORDER BY id DESC;
