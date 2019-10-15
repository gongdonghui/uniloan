package com.sup.backend.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xidongzhou1 on 2019/10/15.
 */

public class LangUtil {
  private Map<String, Map<String, Map<String, String>>> trans_map;

  public Map<String, Map<String, Map<String, String>>> getTrans_map() {
    return trans_map;
  }

  public void setTrans_map(Map<String, Map<String, Map<String, String>>> trans_map) {
    this.trans_map = trans_map;
  }

  public LangUtil(JSONObject obj_cfg) {
    trans_map = new HashMap<>();
    for (String field_name : obj_cfg.keySet()) {
      Map<String, Map<String, String>> field_map = new HashMap<>();
      JSONObject fields_map_obj = obj_cfg.getJSONObject(field_name);
      JSONArray ids = fields_map_obj.getJSONArray("id");
      for (String lang : fields_map_obj.keySet()) {
        if (lang.equals("id")) {
          continue;
        }
        JSONArray trans = fields_map_obj.getJSONArray(lang);
        Map<String, String> id_to_desc = new HashMap<>();
        for (int i = 0; i < ids.size(); ++i) {
            id_to_desc.put(ids.getString(i), trans.getString(i));
        }
        field_map.put(lang, id_to_desc);
      }
      trans_map.put(field_name, field_map);
    }
  }

  public static LangUtil of(JSONObject obj) {
    return new LangUtil(obj);
  }

  public String GetTrans(String field_name, String system_id, String lang) {
    if (StringUtils.isEmpty(lang)) {
      return system_id;
    }
    if (trans_map.containsKey(field_name)) {
      Map<String, Map<String, String>> trans = trans_map.get(field_name);
      if (trans.containsKey(lang)) {
        Map<String, String> tran = trans.get(lang);
        if (tran.containsKey(system_id)) {
          return tran.get(system_id);
        }
      }
    }
    return system_id;
  }
}
