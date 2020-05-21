
use uniloan2;

select ii.dt
  ,product_id
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
  ,repay_early_num
  ,repay_normal_num
  ,repay_late_num
  ,repay_should_num
  ,contract_amount
  ,inhand_amount
from (
  select date(install_begin_date) as dt, count(distinct deviceid) as download_num
  from tb_install_click_info where date(install_begin_date) >= '2020-05-18'
  group by date(install_begin_date)
) ii
left join (
  select date(create_time) as dt, count(distinct mobile) as regist_num
  from tb_user_regist_info where date(create_time) >= '2020-05-18'
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
  count(distinct case when status=14 then apply_id end) as repay_total,
  count(distinct case when status=14 and date(expire_time)>date(create_time) then apply_id end) as repay_early_num,
  count(distinct case when status=14 and date(expire_time)=date(create_time) then apply_id end) as repay_normal_num,
  count(distinct case when status=14 and date(expire_time)<date(create_time) then apply_id end) as repay_late_num,
  count(distinct case when status in (12, 13, 14) and date(expire_time)=date(create_time) then apply_id end) as repay_should_num,
  sum(case when status=12 then grant_quota end)  as contract_amount,
  sum(case when status=12 then inhand_quota end) as inhand_amount
  from (
    select id, grant_quota, inhand_quota from tb_apply_info where date(create_time) >= '2020-05-18'
  ) ai
  left join (
    select * from tb_apply_info_history where date(create_time) >= '2020-05-18'
  ) aih on ai.id = aih.apply_id
  group by date(create_time), product_id
) ai on ii.dt=ai.dt
order by ii.dt

