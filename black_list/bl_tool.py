#!/usr/bin/python
# -*- coding: UTF-8 -*-
import os, sys
from sys import stdin, stdout
import urllib
import urllib2
import json 
import MySQLdb
import md5
import time 
import csv

reload(sys)
sys.setdefaultencoding('utf8')
#import  pandas  as  pd 


def addBl(cid, mobile, name, status):

  db = MySQLdb.connect(mysql_host, mysql_user, mysql_pwd, mysql_db, charset='utf8' )
  cursor = db.cursor()
  print("add", cid, mobile)
  lt = time.localtime()
  st = time.strftime("%Y-%m-%d %H:%M:%S",lt)
  print  st
  sql = " INSERT INTO `tb_blacklist` ( `cid_no`, `mobile`, `status`, `platform`,`create_time`)  \
        VALUES ('%s', '%s', %d, '%s', '%s')" % (cid, mobile, status, "inner", st)
  print("sql=%s" % sql)

  try:
     cursor.execute(sql)
     db.commit()
     
  except Exception  as e :
     print  e
  db.close()


#data  =    pd.read_csv("black_list.csv")
#print(data.shape[0], data.shape[1])
#data.apply(lambda x : addBl(x['cid'], x['mobile']), axis =1)

# rm-t4n37hf32058pbwmo.mysql.singapore.rds.aliyuncs.com
mysql_host="rm-t4n37hf32058pbwmo.mysql.singapore.rds.aliyuncs.com"
#mysql_host="172.21.190.227"
mysql_user="dev_online"
mysql_pwd="Dwl12#$%^"
mysql_db="uniloan"

file_name="black_list.csv"
#file_name="black_list.test.csv"

db = MySQLdb.connect(mysql_host, mysql_user, mysql_pwd, mysql_db, charset='utf8' )
cursor = db.cursor()
lt = time.localtime()
st = time.strftime("%Y-%m-%d %H:%M:%S",lt)
csv_file=open(file_name, 'r')
reader=csv.reader(csv_file)
for line in reader:
  if reader.line_num == 1:
    continue
  mobile=line[1]
  cid=line[2]
  name=line[3]
  status=2  # black list
  sql = " INSERT INTO `tb_blacklist` ( `cid_no`, `mobile`, `status`, `platform`,`create_time`)  \
        VALUES ('%s', '%s', %d, '%s', '%s')" % (cid, mobile, status, "inner", st)
  print("sql=%s" % sql)

  try:
     cursor.execute(sql)
     db.commit()
  except Exception  as e :
     print  e

csv_file.close()
db.close()

