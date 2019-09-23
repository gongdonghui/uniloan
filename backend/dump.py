#!/bin/env python


import sys 
import json 

province_map = {}
city_map = {}
country_map = {}

for line in sys.stdin:
  buf = line.strip("\n")
  province_id, province_nm, city_id, city_nm, country_id, country_nm = buf.split("\t")
  if country_id == "":
    country_id = city_id + "00"
    country_nm = city_nm
  province_map[province_id] = province_nm 
  city_map[city_id] = city_nm
  country_map[country_id] = country_nm

print(len(province_map))
province_list = province_map.items()
province_list.sort(key = lambda x: x[0])
province_obj = {"id": [], "vi": [], "en": []}
for (k, v )in province_list:
  province_obj["id"].append(int(k))
  province_obj["vi"].append(v)
  province_obj["en"].append(k) 
print(json.dumps(province_obj, ensure_ascii=False, encoding = "utf-8"))


print(len(city_map))
province_list = city_map.items()
province_list.sort(key = lambda x: x[0])
province_obj = {"id": [], "vi": [], "en": []}
for (k, v )in province_list:
  province_obj["id"].append(int(k))
  province_obj["vi"].append(v)
  province_obj["en"].append(k) 
print(json.dumps(province_obj, ensure_ascii=False, encoding = "utf-8"))

print(len(country_map))
province_list = country_map.items()
province_list.sort(key = lambda x: x[0])
province_obj = {"id": [], "vi": [], "en": []}
for (k, v )in province_list:
  province_obj["id"].append(int(k))
  province_obj["vi"].append(v)
  province_obj["en"].append(k) 
print(json.dumps(province_obj, ensure_ascii=False, encoding = "utf-8"))




