package tests;

import global.AttrType;
import heap.*;
import global.*;
import iterator.*;
import skyline.*;
import java.util.*;

public class SortPrefInt {

    private static int   LARGE = 5; 

  private static short REC_LEN1 = 32; 
  private static short REC_LEN2 = 160; 
  private static int   SORTPGNUM = 12; 
    private static int   SORTPGNUM1 = 12; 

    private static String   data1[] = {
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

    private static int   NUM_RECORDS = data1.length; 




      public static void main(String[] a) {

        // System.out.println("Count of records " + NUM_RECORDS);

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



    Random random1 = new Random();
    Random random2 = new Random();
  

    AttrType[] attrType = new AttrType[4];
    attrType[0] = new AttrType(AttrType.attrString);
    attrType[1] = new AttrType(AttrType.attrString);
    attrType[2] = new AttrType(AttrType.attrInteger);
    attrType[3] = new AttrType(AttrType.attrReal);
    short[] attrSize = new short[2];
    attrSize[0] = REC_LEN1;
    attrSize[1] = REC_LEN1;
    TupleOrder[] order = new TupleOrder[2];
    order[0] = new TupleOrder(TupleOrder.Ascending);
    order[1] = new TupleOrder(TupleOrder.Descending);

        Tuple t = new Tuple();

    try {
      t.setHdr((short) 4, attrType, attrSize);
    }
    catch (Exception e) {
      System.err.println("*** error in Tuple.setHdr() ***");
      // status = FAIL;
      e.printStackTrace();
    }

    int size = t.size();

    // Create unsorted data file "test3.in"
    RID             rid;
    Heapfile        f = null;
    try {
      f = new Heapfile("test3.in");
    }
    catch (Exception e) {
      // status = FAIL;
      e.printStackTrace();
    }
    
    t = new Tuple(size);
    try {
      t.setHdr((short) 4, attrType, attrSize);
    }
    catch (Exception e) {
      // status = FAIL;
      e.printStackTrace();
    }

    int inum = 0;
    float fnum = 0;
    int count = 0;

     for (int i=0; i<LARGE; i++) {
      // setting fields
      inum = random1.nextInt(100);
      fnum = random2.nextFloat();
      try {
  t.setStrFld(1, data1[i%NUM_RECORDS]);
  t.setIntFld(3, inum);
  t.setFloFld(4, fnum);
      }
      catch (Exception e) {
  // status = FAIL;
  e.printStackTrace();
      }

      try {
  rid = f.insertRecord(t.returnTupleByteArray());
      }
      catch (Exception e) {
  // status = FAIL;
  e.printStackTrace();
      }
    }

    // create an iterator by open a file scan
    FldSpec[] projlist = new FldSpec[4];
    RelSpec rel = new RelSpec(RelSpec.outer); 
    projlist[0] = new FldSpec(rel, 1);
    projlist[1] = new FldSpec(rel, 2);
    projlist[2] = new FldSpec(rel, 3);
    projlist[3] = new FldSpec(rel, 4);
    
    FileScan fscan = null;

        System.out.println(" -- Sorting in ascending order on the int field -- ");

            try {
      fscan = new FileScan("test3.in", attrType, attrSize, (short) 4, 4, projlist, null);
    }
    catch (Exception e) {
      // status = FAIL;
      e.printStackTrace();
    }

      int[] pref_list = new int[1];
        pref_list[0] = 2;
        // pref_list[1] = 2;
        int pref_list_length = 1;


    SortPref sort = null;
    try {
      sort = new SortPref(attrType, (short) 4, attrSize, fscan, order[1], pref_list, pref_list_length, SORTPGNUM);
      // sort = new SortPref(attrType, (short) 2, attrSize, fscan, order[0], pref_list, pref_list_length, SORTPGNUM);

    }
    catch (Exception e) {
      // status = FAIL;
      e.printStackTrace();
    }

    Tuple po = new Tuple();

    System.out.println("********** After Sorting **************");
    try{
      while((po = sort.get_next()) != null) {
          po.print(attrType);
      }
    } catch (Exception e) {
        e.printStackTrace();
    }


    // clean up
        try {
          // sort.close();
          sort.close();
        }
        catch (Exception e) {
          // status = FAIL;
          e.printStackTrace();
        }
        
        System.err.println("------------------- TEST 1 completed ---------------------\n");
        


      }
    }
