package tests;

import global.AttrType;
import heap.*;
import global.*;
import iterator.*;
import skyline.*;
import java.util.*;

public class SortPrefTest {

  private static String   data1[] = {
    "op","we","qw","ml","lp"
  };


  private static String   datax[] = {
    "raghu", "xbao", "cychan", "leela", "ketola", "soma", "ulloa", 
    "dhanoa", "dsilva", "kurniawa", "dissoswa", "waic", "susanc", "kinc", 
    "marc", "scottc", "yuc", "ireland", "rathgebe", "joyce", "daode", 
    "yuvadee", "he", "huxtable", "muerle", "flechtne", "thiodore", "jhowe",
    "frankief", "yiching", "xiaoming", "jsong", "yung", "muthiah", "bloch",
    "binh", "dai", "hai", "handi", "shi", "sonthi", "evgueni", "chung-pi",
    "chui", "siddiqui", "mak", "tak", "sungk", "randal", "barthel", 
    "newell", "schiesl", "neuman", "heitzman", "wan", "gunawan", "djensen",
    "juei-wen", "josephin", "harimin", "xin", "zmudzin", "feldmann", 
    "joon", "wawrzon", "yi-chun", "wenchao", "seo", "karsono", "dwiyono", 
    "ginther", "keeler", "peter", "lukas", "edwards", "mirwais","schleis", 
    "haris", "meyers", "azat", "shun-kit", "robert", "markert", "wlau",
    "honghu", "guangshu", "chingju", "bradw", "andyw", "gray", "vharvey", 
    "awny", "savoy", "meltz"}; 
    
    private static String   data2[] = {
      "andyw", "awny", "azat", "barthel", "binh", "bloch", "bradw", 
      "chingju", "chui", "chung-pi", "cychan", "dai", "daode", "dhanoa", 
      "dissoswa", "djensen", "dsilva", "dwiyono", "edwards", "evgueni", 
      "feldmann", "flechtne", "frankief", "ginther", "gray", "guangshu", 
      "gunawan", "hai", "handi", "harimin", "haris", "he", "heitzman", 
      "honghu", "huxtable", "ireland", "jhowe", "joon", "josephin", "joyce",
      "jsong", "juei-wen", "karsono", "keeler", "ketola", "kinc", "kurniawa",
      "leela", "lukas", "mak", "marc", "markert", "meltz", "meyers", 
      "mirwais", "muerle", "muthiah", "neuman", "newell", "peter", "raghu", 
      "randal", "rathgebe", "robert", "savoy", "schiesl", "schleis", 
      "scottc", "seo", "shi", "shun-kit", "siddiqui", "soma", "sonthi", 
      "sungk", "susanc", "tak", "thiodore", "ulloa", "vharvey", "waic",
      "wan", "wawrzon", "wenchao", "wlau", "xbao", "xiaoming", "xin", 
      "yi-chun", "yiching", "yuc", "yung", "yuvadee", "zmudzin" };

      private static int data3[] = {34, 12, 90, 22};
      static int INT_NUM_RECORDS = data3.length;

      static int NUM_RECORDS = data1.length;


  private static short REC_LEN1 = 32; 
  private static short REC_LEN2 = 160; 
  private static int   SORTPGNUM = 12; 
    private static int   SORTPGNUM1 = 12; 


      public static void main(String[] a) {

        System.out.println("Count of records " + NUM_RECORDS);

        String dbpath = "/tmp/"+System.getProperty("user.name")+".minibase.skylinedb"; 
        // System.out.println(System.getProperty("user.name"));
        String logpath = "/tmp/"+System.getProperty("user.name")+".skylinelog";

        String remove_cmd = "/bin/rm -rf ";
        String remove_logcmd = remove_cmd + logpath;
        String remove_dbcmd = remove_cmd + dbpath;
        String remove_joincmd = remove_cmd + dbpath;

        try {
          Runtime.getRuntime().exec(remove_logcmd);
          Runtime.getRuntime().exec(remove_dbcmd);
          Runtime.getRuntime().exec(remove_joincmd);
        }
        catch (Exception e) {
          System.err.println (""+e);
        }
        
        SystemDefs sysdef = new SystemDefs( dbpath, 10000, 100, "Clock" );

        AttrType[] attrType = new AttrType[2];
        attrType[0] = new AttrType(AttrType.attrString);
        attrType[1] = new AttrType(AttrType.attrString);
        short[] attrSize = new short[2];
        attrSize[0] = REC_LEN1;
        attrSize[1] = REC_LEN2;
        TupleOrder[] order = new TupleOrder[2];
        order[0] = new TupleOrder(TupleOrder.Ascending);
        order[1] = new TupleOrder(TupleOrder.Descending);

        // for int testing

         AttrType[] attrType1 = new AttrType[2];
        attrType1[0] = new AttrType(AttrType.attrInteger);
        attrType1[1] = new AttrType(AttrType.attrInteger);
        short[] attrSize1 = new short[2];
        attrSize1[0] = REC_LEN1;
        attrSize1[1] = REC_LEN2;
        TupleOrder[] order1 = new TupleOrder[2];
        order1[0] = new TupleOrder(TupleOrder.Ascending);
        order1[1] = new TupleOrder(TupleOrder.Descending);
        

        
        Tuple t = new Tuple();
        Tuple o = new Tuple();
        Tuple int_tuple = new Tuple();
        Tuple out_tuple = new Tuple();


        try {
          t.setHdr((short) 2, attrType, attrSize);
          o.setHdr((short) 2, attrType, attrSize);
          int_tuple.setHdr((short) 2, attrType1, attrSize1);
          out_tuple.setHdr((short) 2, attrType1, attrSize1);
        }
        catch (Exception e) {
          // //status = FAIL;
          e.printStackTrace();
        }

        int size = t.size();
        int int_size = int_tuple.size();

        
    // Create unsorted data file "test1.in"
        RID             rid;
        Heapfile        f = null;
        RID             rid1;
        Heapfile        f1 = null;
        try {
          f = new Heapfile("test1.in");
          f1 = new Heapfile("test2.in");
        }
        catch (Exception e) {
          // //status = FAIL;
          e.printStackTrace();
        }
        
        t = new Tuple(size);
        o = new Tuple(size);
        int_tuple = new Tuple(int_size);
        out_tuple = new Tuple(int_size);
        try {
          t.setHdr((short) 2, attrType, attrSize);
          o.setHdr((short) 2, attrType, attrSize);
          int_tuple.setHdr((short)2, attrType1, attrSize1);
          out_tuple.setHdr((short)2, attrType1, attrSize1);

        }
        catch (Exception e) {
          // //status = FAIL;
          e.printStackTrace();
        }
        
        for (int i=0; i<NUM_RECORDS; i++) {
          try {
            t.setStrFld(1, data1[i]);
            // t.print(attrType);
          }
          catch (Exception e) {
            // //status = FAIL;
            e.printStackTrace();
          }
          
          try {
            rid = f.insertRecord(t.returnTupleByteArray());
          }
          catch (Exception e) {
            // //status = FAIL;
            e.printStackTrace();
          }
        }


        for (int i=0; i<INT_NUM_RECORDS; i++) {
          try {
            int_tuple.setIntFld(1, data3[i]);
            // t.print(attrType);
          }
          catch (Exception e) {
            // //status = FAIL;
            e.printStackTrace();
          }
          
          try {
            rid1 = f1.insertRecord(int_tuple.returnTupleByteArray());
          }
          catch (Exception e) {
            // //status = FAIL;
            e.printStackTrace();
          }
        }

// create an iterator by open a file scan
        FldSpec[] projlist = new FldSpec[2];
        RelSpec rel = new RelSpec(RelSpec.outer); 
        projlist[0] = new FldSpec(rel, 1);
        projlist[1] = new FldSpec(rel, 2);


        FldSpec[] projlist1 = new FldSpec[2];
        RelSpec rel1 = new RelSpec(RelSpec.outer); 
        projlist1[0] = new FldSpec(rel, 1);
        projlist1[1] = new FldSpec(rel, 2);
        
        FileScan fscan = null;
        FileScan fscan1 = null;
        
        try {
          fscan = new FileScan("test1.in", attrType, attrSize, (short) 2, 2, projlist, null);
          fscan1 = new FileScan("test2.in", attrType1, attrSize1, (short) 2, 2, projlist1, null);
        }
        catch (Exception e) {
          //status = FAIL;
          e.printStackTrace();
        }

        System.out.println("Before Sorting --------- int_tuple Tuple");

        // try{
        //     int_tuple.print(attrType1);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }

        short[] str_sizes = new short[5];
        int[] pref_list = new int[2];
        pref_list[0] = 0;

        int[] pref_list1 = new int[1];
        pref_list1[0] = 0;
                // pref_list[1] = 1;

        // pref_list[1] = 3;
        int pref_list_length = 2;
        int pref_list_length1 = 1;

    // Sort "test1.in" 
        SortPref sort = null;
        SortPref sort1 = null;
        System.out.println("-------------------before int sort Object --------------------");
        System.out.println(sort1);
        try {
          // System.out.println("Before SortPrefTest");
          // System.out.println("SortPref Object " + sort);
          sort = new SortPref(attrType, (short) 2, attrSize, fscan, order[0], pref_list, pref_list_length, SORTPGNUM);
          // sort1 = new SortPref(attrType1, (short) 2, attrSize1, fscan1, order1[0], pref_list1, pref_list_length1, SORTPGNUM1);
                    
           //          System.out.println("attrType1 length " + attrType1.length);
           // for (AttrType at : attrType1) {
           //  System.out.print(at);
        System.out.println("-------------------after SortPref --------------------");


        Tuple qdq = new Tuple();
      try{
          while ((qdq = fscan1.get_next()) != null) {
              qdq.print(attrType1);
          }
      } catch (Exception e) {
        e.printStackTrace();
      }

           // }         

          System.out.println("-------------------after int sort Object --------------------");
          // System.out.println(sort1);
          Tuple qt = new Tuple();
          // qt = sort1.get_next();
                

          // qt.print(attrType1);

            while((qt = sort.get_next()) != null){
               System.out.println("*********************** sort in iterator *********************");
              qt.print(attrType);
          } 
        }
        catch (Exception e) {
          // //status = FAIL;
          e.printStackTrace();
        }

           System.out.println("------------------- after sort for int --------------------");

              

        //  try {
        //   Tuple qt = new Tuple();
        //     while((qt = sort1.get_next()) != null){
        //        System.out.println("*********************** sort in iterator *********************");
        //       qt.print(attrType1);
        //   } 
        // }
        // catch (Exception e) {
        //   // status = FAIL;
        //   e.printStackTrace(); 
        // }

        // System.out.println("------------------- after sort for string --------------------");


        //  try {
        //     while((o = sort.get_next()) != null){
        //       o.print(attrType);
        //   } 
        // }
        // catch (Exception e) {
        //   // status = FAIL;
        //   e.printStackTrace(); 
        // }

        

                     System.out.println("------------------- end of sort --------------------");

        //
            // System.out.println("********************************after sort*********************************");
            

            // try {
            //     Tuple tq = sort.get_next();
            //     tq.print(attrType);


            //     // while(t != null) {
            //     //       t = sort.get_next();
            //     // }
            // } catch (Exception e) {
            //    e.printStackTrace();
            // }


        //
            /*
        int count = 0;
        t = null;
        String outval = null;
        
        try {
          t = sort.get_next();
          // System.out.print("Tuples after sort");
          // t.print(attrType);
        }
        catch (Exception e) {
          // //status = FAIL;
          e.printStackTrace(); 
        }

        boolean flag = true;


        
        while (t != null) {
          if (count >= NUM_RECORDS) {
            System.err.println("Test1 -- OOPS! too many records");
            //status = FAIL;
            flag = false; 
            break;
          }
          
          try {
            outval = t.getStrFld(1);
          }
          catch (Exception e) {
            //status = FAIL;
            e.printStackTrace();
          }
          
          if (outval.compareTo(data2[count]) != 0) {
            System.err.println("outval = " + outval + "\tdata2[count] = " + data2[count]);
            
            System.err.println("Test1 -- OOPS! test1.out not sorted");
            //status = FAIL;
          }
          count++;

          try {
            t = sort.get_next();
          }
          catch (Exception e) {
            //status = FAIL;
            e.printStackTrace();
          }
        }
        if (count < NUM_RECORDS) {
          System.err.println("Test1 -- OOPS! too few records");
          //status = FAIL;
        }
        else if (flag) {
          System.err.println("Test1 -- Sorting OK");
        }

        */

    // clean up
        try {
          // sort.close();
          sort1.close();
        }
        catch (Exception e) {
          //status = FAIL;
          e.printStackTrace();
        }
        
        System.err.println("------------------- TEST 1 completed ---------------------\n");
        


      }
    }
