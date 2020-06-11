
use uniloan2;

set @yesterday=DATE_SUB(CURDATE(),INTERVAL 1 DAY);
-- set @yesterday='2020-05-25';

select ii.dt
  ,ai.product_id
  ,download_num
  ,regist_num
  ,apply_total_num
  ,apply_user_num
  ,auto_pass_num
  ,auto_deny_num
  ,manual_pass_num
  ,manual_deny_num
  ,apply_cancel_num
  ,loan_num
  ,repay_total
  ,repay_clear_total
  ,repay_then_apply_num
  ,repay_early_num
  ,repay_normal_num
  ,repay_late_num
  ,contract_amount
  ,inhand_amount
  ,repay_amount
  ,repay_should_num
  ,(repay_should_num - repay_normal_num) as first_overdue
from (
  select date(install_begin_date) as dt, count(distinct deviceid) as download_num
  from tb_install_click_info where date(install_begin_date)=@yesterday
  group by date(install_begin_date)
) ii
left join (
  select date(create_time) as dt, count(distinct mobile) as regist_num
  from tb_user_regist_info where date(create_time)=@yesterday
  group by date(create_time)
) ri on ii.dt=ri.dt
left join (
  select date(create_time) as dt, product_id,
  count(distinct apply_id) as apply_total_num,
  count(distinct user_id)  as apply_user_num,
  count(distinct case when status=1 then apply_id end) as auto_pass_num,
  count(distinct case when status=5 then apply_id end) as auto_deny_num,
  count(distinct case when status in (2,4) then apply_id end) as manual_pass_num,
  count(distinct case when status in (6,8) then apply_id end) as manual_deny_num,
  count(distinct case when status=9 then apply_id end) as apply_cancel_num,
  count(distinct case when status=12 then apply_id end) as loan_num,
  sum(case when status=12 then grant_quota end)  as contract_amount,
  sum(case when status=12 then inhand_quota end) as inhand_amount
  from (
    select id, grant_quota, inhand_quota from tb_apply_info where date(create_time)=@yesterday or date(update_time)=@yesterday
  ) ai
  left join (
    select * from tb_apply_info_history where date(create_time)=@yesterday
  ) aih on ai.id = aih.apply_id
  group by date(create_time), product_id
) ai on ii.dt=ai.dt
left join (
  select @yesterday as dt, product_id
  , sum(case when repay_status in (1,2) then act_total end) as repay_amount
  ,count(case when repay_status in (1,2) then apply_id end) as repay_total
  ,count(case when repay_status=2 then apply_id end) as repay_clear_total
  ,count(case when repay_status=2 and date(repay_end_date)>@yesterday then apply_id end) as repay_early_num
  ,count(case when repay_status=2 and date(repay_end_date)=@yesterday then apply_id end) as repay_normal_num
  ,count(case when repay_status=2 and date(repay_end_date)<@yesterday then apply_id end) as repay_late_num
  from tb_repay_plan where date(repay_time)=@yesterday
  group by product_id
) rp on ii.dt=rp.dt and ai.product_id = rp.product_id
left join (
  select @yesterday as dt, product_id
  ,count(apply_id) as repay_should_num
  from tb_repay_plan where date(repay_end_date)=@yesterday
  group by product_id
) rp2 on ii.dt=rp2.dt and ai.product_id = rp2.product_id
left join (
-- 当日结清复贷数
  select @yesterday as dt, product_id, count(apply_id) as repay_then_apply_num
  from (
    select user_id from tb_repay_plan where repay_status=2 and date(repay_time)=@yesterday
  ) rp
  left join (
    select id as apply_id, user_id, product_id from tb_apply_info where date(create_time)=@yesterday
  ) ai on rp.user_id=ai.user_id
  where apply_id is not null
  group by product_id
) rp3 on ii.dt=rp3.dt and ai.product_id = rp3.product_id
\G;

