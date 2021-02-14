create table currency_rates (
                              id bigint auto_increment primary key not null,
                              currency varchar(3),
                              rate double precision,
                              base_currency varchar(3),
                              operation_count   integer,
                              rate_date date
                            );
