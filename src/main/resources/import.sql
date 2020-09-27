insert into public.empresa
values
    (nextval('public.empresa_sequence'), now(), null, now(), 'Razão Social', '00000000000191', 'admin@epedidos.com.br', 'Nome Fantasia', '3133333333');
insert into public.sistema
values
    (nextval('public.sistema_sequence'), now(), null, now(), 'E-Pedidos');
insert into public.empresa_sistema
values
    (nextval('public.empresa_sistema_sequence'), now(), null, now(), 'admin@epedidos.com.br', 1, 'A', 'Cliente1', 1, 1);
insert into public.usuario_admin
values
    (nextval('public.usuario_admin_sequence'), now(), null, now(), 'admin@epedidos.com.br', 'Admin', '123');
insert into public.usuario_portal
values
    (nextval('public.usuario_portal_sequence'), now(), null, now(), 'admin@epedidos.com.br', null, '123', 1);
