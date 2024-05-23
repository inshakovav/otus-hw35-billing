CREATE TABLE billing_scheme.billing
(
    id         bigserial,
    created_at timestamp NOT NULL,
    updated_at timestamp NOT NULL,
    status     text      NOT NULL,
    order_id   integer
);

