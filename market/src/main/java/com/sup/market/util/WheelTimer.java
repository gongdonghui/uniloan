package com.sup.market.util;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xidongzhou1 on 2017/9/19.
 */
public class WheelTimer {
  public static long basic_timestamp = ToolUtils.NormTime("2000-01-01 00:00:00").getTime()/1000;
  public static  long GetNormTs(long ts) {
    return (ts/1000) - basic_timestamp;
  }
  public static String GetStrByNormTs(long ts) {
    return ToolUtils.NormTime(new Date((ts + basic_timestamp)*1000));
  }

  public static interface TimerCb {
    public void Run(Object ctx);
  }

  public class MTimer {
    private long expire;
    private TimerCb callback_fn;
    private Object callback_ctx;
    private MTimer next;

    public TimerCb getCallback_fn() {
      return callback_fn;
    }

    public void setCallback_fn(TimerCb callback_fn) {
      this.callback_fn = callback_fn;
    }

    public Object getCallback_ctx() {
      return callback_ctx;
    }

    public void setCallback_ctx(Object callback_ctx) {
      this.callback_ctx = callback_ctx;
    }

    public MTimer(long expire, TimerCb cb, Object ctx) {
      setExpire(GetNormTs(expire));
      setCallback_fn(cb);
      setCallback_ctx(ctx);
    }

    public long getExpire() {
      return expire;
    }

    public void setExpire(long expire) {
      this.expire = expire;
    }

    public MTimer getNext() {
      return next;
    }

    public void setNext(MTimer next) {
      this.next = next;
    }
  }

  private static class TimeVec {
    private String name = "";
    private MTimer[] timer_list;
    public TimeVec(int len, String nm) {
      timer_list = new MTimer[len];
      name = nm;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public MTimer[] getTimer_list() {
      return timer_list;
    }

    public void setTimer_list(MTimer[] timer_list) {
      this.timer_list = timer_list;
    }

    public void AddToList(int slot, MTimer tm) {
      MTimer t = timer_list[slot];
      if (t == null) {
        tm.next = null;
      } else {
        tm.next = t;
      }
      timer_list[slot] = tm;
    }
  }

  private TimeVec tv1 = new TimeVec(1<<8, "tv1");
  private TimeVec tv2 = new TimeVec(1<<8, "tv2");
  private TimeVec tv3 = new TimeVec(1<<8, "tv3");
  private TimeVec tv4 = new TimeVec(1<<8, "tv4");
  private long mask = ((1<<8) - 1);
  private long wheel_time = 0;

  public WheelTimer() {
    wheel_time = GetNormTs(System.currentTimeMillis());
    System.out.println("create_wheel_timer: " + GetStrByNormTs(wheel_time));
  }

  public WheelTimer(long ts) {
    wheel_time = GetNormTs(ts);
    System.out.println("create_wheel_timer: " + GetStrByNormTs(wheel_time));
  }

  public void AddTimer(MTimer mt) {
    synchronized (this) {
      long time_offset = mt.getExpire() - wheel_time;
      if (time_offset < 0) {
        time_offset = 1;
      }
      long ts = mt.getExpire();
      if (time_offset < (1 << 8)) {
        long pos = (ts >> 0) & mask;
        tv1.AddToList((int) pos, mt);
      } else if (time_offset < (1 << 16)) {
        long pos = (ts >> 8) & mask;
        tv2.AddToList((int) pos, mt);
      } else if (time_offset < (1 << 24)) {
        long pos = (ts >> 16) & mask;
        tv3.AddToList((int) pos, mt);
      } else {
        long pos = (ts >> 24) & mask;
        tv4.AddToList((int) pos, mt);
      }
    }
  }

  public void AddTimer(long expire, TimerCb cb, Object ctx) {
    MTimer mt = new MTimer(expire, cb, ctx);
    AddTimer(mt);
  }

  private int cascade(TimeVec tv, int index) {
    List<MTimer> timer_need_move = new ArrayList<MTimer>();
    if (tv.getTimer_list()[index] != null) {
      MTimer mt = tv.getTimer_list()[index];
      while (mt != null) {
        timer_need_move.add(mt);
        mt = mt.next;
      }
      tv.getTimer_list()[index] = null;
    }
    for (MTimer mt : timer_need_move) {
      AddTimer(mt);
    }
    return index;
  }

  public void Run(long ts) {
    long rt_ts = GetNormTs(ts);
    while (wheel_time <= rt_ts) {
      List<MTimer> running_timer = new ArrayList<MTimer>();
      synchronized (this) {
        int index = (int) (wheel_time & mask);
        if (index == 0 && cascade(tv2, (int) ((wheel_time >> 8) & mask)) == 0 && cascade(tv3, (int) ((wheel_time >> 16) & mask)) == 0) {
          cascade(tv4, (int) ((wheel_time >> 24) & mask));
        }
        // process current list !!
        MTimer mt = tv1.getTimer_list()[index];
        if (mt != null) {
          while (mt != null) {
            running_timer.add(mt);
            mt = mt.next;
          }
          tv1.getTimer_list()[index] = null;
        }
        wheel_time++;
      }
      for (MTimer run_mt : running_timer) {
        run_mt.getCallback_fn().Run(run_mt.getCallback_ctx());
      }
    }
  }

  public void Run() {
    Run(System.currentTimeMillis());
  }
}
