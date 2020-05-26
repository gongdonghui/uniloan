
use uniloan2;

select mobile, create_time as regist_time
from tb_user_regist_info
where date(create_time)>='2020-05-18' and date(create_time)<='2020-05-19' and id not in (
  select user_id from tb_apply_info
)
order by id desc
;
