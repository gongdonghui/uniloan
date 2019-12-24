

;申请各种日期
select
apply_id
,max(case when status = 0 then create_time end) as apply_time
,max(case when status = 1 or status = 5 then create_time end) as auto_audit_time
,max(case when status = 2 or status = 6 then create_time end) as first_audit_time
,max(case when status = 3 or status = 7 then create_time end) as second_audit_time
,max(case when status = 4 or status = 8 then create_time end) as final_audit_time
,max(case when status = 12 then create_time end) as loan_time
,max(case when status = 15 then create_time end) as overdue_time
,max(case when status = 16 then create_time end) as write_off_time
from tb_apply_info_history
group by apply_id;

