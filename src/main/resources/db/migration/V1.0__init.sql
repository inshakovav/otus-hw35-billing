CREATE TABLE billing_scheme.account
(
    id         bigserial,
    created_at timestamp      NOT NULL,
    updated_at timestamp      NOT NULL,
    name       text           NOT NULL,
    balance    numeric(10, 2) NOT NULL DEFAULT 0
);

