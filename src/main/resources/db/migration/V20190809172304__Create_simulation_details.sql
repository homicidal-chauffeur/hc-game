DROP TYPE IF EXISTS capture;
CREATE TYPE capture AS (
    time int8,
    distance double precision
    );

DROP TYPE IF EXISTS vector3r;
CREATE TYPE vector3r AS (
    x double precision,
    y double precision,
    z double precision
    );

DROP TYPE IF EXISTS quaternion3r;
CREATE TYPE quaternion3r AS (
    x double precision,
    y double precision,
    z double precision,
    w double precision
    );

DROP TYPE IF EXISTS move;
CREATE TYPE move AS (
    name varchar,
    time int8,

    my_theta double precision,
    opp_theta double precision,

    my_position vector3r,
    opp_position vector3r,

    my_orientation quaternion3r,
    opp_orientation quaternion3r
    );

DROP VIEW IF EXISTS simulation_details;
CREATE VIEW simulation_details AS
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
       (SELECT MIN(time) FROM captures c WHERE c.simulation_id = s.id) AS first_capture,

        (SELECT array_agg(t::capture) FROM (
           SELECT c.time, c.distance
            FROM captures c
            WHERE c.simulation_id = s.id
            ORDER BY time
            ) AS t
        ) AS captures,

        (SELECT array_agg(t::move) FROM (
            SELECT ps.name, m.time,
                   m.my_theta, m.opp_theta,
                   ROW(m.my_position_x, m.my_position_y, m.my_position_z)::vector3r AS my_position,
                   ROW(m.opp_position_x, m.opp_position_y, m.opp_position_z)::vector3r AS opp_position,
                   ROW(m.my_orientation_x, m.my_orientation_y, m.my_orientation_z, m.my_orientation_w)::quaternion3r AS my_orientation,
                   ROW(m.opp_orientation_x, m.opp_orientation_y, m.opp_orientation_z, m.opp_orientation_w)::quaternion3r AS opp_orientation
            FROM moves m
                LEFT JOIN pilot_settings ps ON m.pilot_settings_id = ps.id
            WHERE m.simulation_id = s.id
            ORDER BY time
            ) AS t
        ) AS moves

    FROM simulations s
             LEFT JOIN simulation_settings ss ON ss.simulation_id = s.id
    ORDER BY id DESC;
