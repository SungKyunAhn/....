package com.aimir.fep.meter.data;

import java.util.Comparator;

import com.aimir.fep.meter.data.BillingData;

public class BillingComparator 
{
  public static final Comparator TIMESTAMP_ORDER= new Comparator()
  {
      public int compare(Object o1,Object o2)
      {
          BillingData md1 = (BillingData)o1;
          BillingData md2 = (BillingData)o2;

          String ts1 = md1.getBillingTimestamp();
          String ts2 = md2.getBillingTimestamp();

          return ts1.compareTo(ts2);
      }
      public boolean equals(Object objs)
      {
          return this.equals(objs);
      }
  };
}
