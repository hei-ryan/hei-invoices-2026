-- Q3
select sum(case when status = 'PAID' then invoice_line.quantity * invoice_line.unit_price else 0 end) as total_paid,
       sum(case
               when status = 'CONFIRMED' then invoice_line.quantity * invoice_line.unit_price
               else 0 end)                                                                            as total_confirmed,
       sum(case
               when status = 'DRAFT' then invoice_line.quantity * invoice_line.unit_price
               else 0 end)                                                                            as total_draft
from invoice_line
         join public.invoice i on i.id = invoice_line.invoice_id;
-- Q4
select sum(case
               when status = 'PAID' then il.quantity * il.unit_price * 1
               else
                   case
                       when status = 'CONFIRMED' then il.quantity * il.unit_price * 0.5
                       else
                           case when invoice.status = 'DRAFT' then il.quantity * il.unit_price * 0 else 0 end
                       end
    end)
from invoice
         join invoice_line il on invoice.id = il.invoice_id;


-- Q5-A
create view invoice_montant_ttc as
(
select invoice_id,
       montant_ht,
       montant_ht * tax_config.rate / 100              as tva,
       montant_ht + montant_ht * tax_config.rate / 100 as montant_ttc
from (select invoice_id, sum(quantity * unit_price) as montant_ht
      from invoice_line
      group by invoice_id) invoice_montant_ht,
     tax_config);
select invoice_id, montant_ht, tva, montant_ttc
from invoice_montant_ttc;

-- Q5-B
select sum(case
               when status = 'PAID' then montant_ttc * 1
               else
                   case
                       when status = 'CONFIRMED' then montant_ttc * 0.5
                       else
                           case when invoice.status = 'DRAFT' then montant_ttc * 0 else 0 end
                       end
    end)
from invoice
         join invoice_montant_ttc il on invoice.id = il.invoice_id;