CREATE TYPE invoice_status AS ENUM ('DRAFT','CONFIRMED', 'PAID');

CREATE TABLE invoice
(
    id            serial primary key,
    customer_name varchar not null,
    status        invoice_status
);

CREATE TABLE invoice_line
(
    id         serial primary key not null,
    invoice_id integer references invoice (id),
    label      varchar            not null,
    quantity   int                not null,
    unit_price numeric(10, 2)     not null
);


select invoice_id, customer_name, sum(quantity * unit_price) as total_amount, status
from invoice_line
         join invoice i on i.id = invoice_line.invoice_id
where status = 'CONFIRMED'
   or status = 'PAID'
group by invoice_id, customer_name, status;