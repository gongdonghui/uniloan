package com.sup.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * gongshuai
 * <p>
 * 2019/9/15
 */
public class RiskVariableConstants {
    public static final String AGE = "age";
    public static final String DAYS_BETWEEN_LAST_REFUSE = "days_between_last_refuse";
    public static final String MAX_OVERDUE_DAYS = "max_overdue_days";
    public static final String LATEST_OVERDUE_DAYS = "latest_overdue_days";
    public static final String OVERDUE_TIMES = "overdue_times";
    public static final String NUM_OF_CONTRACT = "no_of_contract";
    public static final String NUM_OF_APPLY_IN_CONTRACT = "no_of_apply_in_contract";
    public static final String NUM_OF_OVDUE_IN_CONTRACT = "no_of_overdue_in_contract";
    public static final String NUM_OF_IDS_FOR_PIN = "no_of_ids_for_pin";
    public static final String NUM_OF_OVERDUE_TIMES_IN_EMMERGENCY_CONTRACT = "overdue_of_emergency_contract";
    public static final String NUM_OF_APPLY_TIMES_IN_EMMERGENCY_CONTRACT = "apply_of_emergency_contract";
    public static final String BLACKLIST_JR= "blacklist_jirong";
    public static final String BLACKLIST_XT= "blacklist_xingtan";
    public static final String BLACKLIST_INNER = "blacklist_inner";
    public static final String USER_ID= "user_identity";
    public static final String A001NegProb= "A001NegProb";

    public static List<String> getVariableList() {
        List<String> list = new ArrayList<>();
        list.add(AGE);
        list.add(DAYS_BETWEEN_LAST_REFUSE);
        list.add(MAX_OVERDUE_DAYS);
        list.add(LATEST_OVERDUE_DAYS);
        list.add(OVERDUE_TIMES);
        list.add(NUM_OF_CONTRACT);
        list.add(NUM_OF_APPLY_IN_CONTRACT);
        list.add(NUM_OF_OVDUE_IN_CONTRACT);
        list.add(NUM_OF_IDS_FOR_PIN);
        list.add(NUM_OF_OVERDUE_TIMES_IN_EMMERGENCY_CONTRACT);
        list.add(NUM_OF_APPLY_TIMES_IN_EMMERGENCY_CONTRACT);
        list.add(BLACKLIST_JR);
        list.add(BLACKLIST_XT);
        list.add(BLACKLIST_INNER);
        list.add(A001NegProb);
        return list;

    }

}
