create table user_action_types (  -- VIEW, LIKE, SCAN
    id bigserial primary key,
    name varchar(20) not null
);

create table user_actions (
    id bigserial primary key,
    keycloak_id uuid not null,
    product_id bigint not null references products(id),
    type_id bigint not null references user_action_types(id),
    created_at timestamptz not null default now()
);

create table user_allergens (
    id bigserial primary key,
    keycloak_id uuid,
    factor_id bigint,
    unique (keycloak_id, factor_id)
);

create table products (
    id bigserial primary key,
    name varchar(255) not null,
    rating int not null check (rating between 0 and 100),
    image_uri text not null
);

insert into user_action_types (name)
values ('like'), ('view'), ('scan');