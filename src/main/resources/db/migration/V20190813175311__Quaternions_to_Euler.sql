-- Math.atan2( 2.0 * (y * z + x * w),(-x * x - y * y + z * z + w * w) )
DROP FUNCTION IF EXISTS q_to_roll(x double precision, y double precision, z double precision, w double precision);
CREATE FUNCTION q_to_roll(x double precision, y double precision, z double precision, w double precision) RETURNS double precision AS $$
SELECT ATAN2( 2.0 * (y * z + x * w),(-x * x - y * y + z * z + w * w) );
$$ LANGUAGE SQL IMMUTABLE ;

-- Math.asin(-2.0 * (x * z - y * w))
DROP FUNCTION IF EXISTS q_to_pitch(x double precision, y double precision, z double precision, w double precision);
CREATE FUNCTION q_to_pitch(x double precision, y double precision, z double precision, w double precision) RETURNS double precision AS $$
SELECT ASIN(-2.0 * (x * z - y * w));
$$ LANGUAGE SQL IMMUTABLE ;

-- Math.atan2( 2.0 * (x * y + z * w), x * x - y * y - z * z + w * w )
DROP FUNCTION IF EXISTS q_to_yaw(x double precision, y double precision, z double precision, w double precision);
CREATE FUNCTION q_to_yaw(x double precision, y double precision, z double precision, w double precision) RETURNS double precision AS $$
SELECT ATAN2( 2.0 * (x * y + z * w), x * x - y * y - z * z + w * w );
$$ LANGUAGE SQL IMMUTABLE ;

