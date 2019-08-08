create table simulations (
                             id serial8 primary key not null ,
                             start_time int8 not null,
                             date_at timestamptz not null,
                             tags text,
                             created_at timestamptz not null default now()
);
