package tests;

import global.AttrType;
import heap.*;
import global.*;
import iterator.*;
// import skyline.Methods;
import skyline.*;
import java.util.*;

public class Skyline {



    private static int data1[] = {4, 6, 99, 121, 32};
    private static int data2[] = {48, 16, 99, 21, 89};
    private static int data3[] = {14, 26, 39, 41, 52};
    private static int data4[] = {78, 16, 59, 81, 99};

    static int NUM_RECORDS = data2.length;

    public static void main(String[] a) {

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

    
        Tuple t = new Tuple();
        Tuple t1 = new Tuple();
        Tuple t2 = new Tuple();

        AttrType [] Stypes = new AttrType[4];
        Stypes[0] = new AttrType (AttrType.attrInteger);
        Stypes[1] = new AttrType (AttrType.attrString);
        Stypes[2] = new AttrType (AttrType.attrInteger);
        Stypes[3] = new AttrType (AttrType.attrReal);
        
        short[] Ssizes = new short[1];
        Ssizes[0] = 30; //first elt. is 30


        // create a tuple of appropriate size
        try {
            t.setHdr((short) 4, Stypes, Ssizes);
            t1.setHdr((short) 4, Stypes, Ssizes);
            t2.setHdr((short) 4, Stypes, Ssizes);


            // t.print(Stypes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = t.size();

        RID             rid;
        Heapfile        f = null;
        try {
            f = new Heapfile("sailors.in");
        }
        catch (Exception e) {
//            status = FAIL;
            e.printStackTrace();
        }

          t = new Tuple(size);
    try {
      t.setHdr((short) 4, Stypes, Ssizes);
      t1.setHdr((short) 4, Stypes, Ssizes);
      t2.setHdr((short) 4, Stypes, Ssizes);
    }
    catch (Exception e) {
      System.err.println("*** error in Tuple.setHdr() ***");
      // status = FAIL;
      e.printStackTrace();
    }

        Vector sailors;

        sailors  = new Vector();

        sailors.addElement(new Sailor(53, "Bob Holloway",       9, 53.6));
	    sailors.addElement(new Sailor(54, "Susan Horowitz",     1, 34.2));
	    sailors.addElement(new Sailor(57, "Yannis Ioannidis",   8, 40.2));
	    // sailors.addElement(new Sailor(59, "Deborah Joseph",    10, 39.8));
	    // sailors.addElement(new Sailor(61, "Landwebber",         8, 56.7));
	    // sailors.addElement(new Sailor(63, "James Larus",        9, 30.3));
	    // sailors.addElement(new Sailor(64, "Barton Miller",      5, 43.7));
	    // sailors.addElement(new Sailor(67, "David Parter",       1, 99.9));   
	    // sailors.addElement(new Sailor(69, "Raghu Ramakrishnan", 9, 37.1));
	    // sailors.addElement(new Sailor(71, "Guri Sohi",         10, 42.1));
	    // sailors.addElement(new Sailor(73, "Prasoon Tiwari",     8, 39.2));
	    // sailors.addElement(new Sailor(39, "Anne Condon",        3, 30.3));
	    // sailors.addElement(new Sailor(47, "Charles Fischer",    6, 46.3));
	    // sailors.addElement(new Sailor(49, "James Goodman",      4, 50.3));
	    // sailors.addElement(new Sailor(50, "Mark Hill",          5, 35.2));
	    // sailors.addElement(new Sailor(75, "Mary Vernon",        7, 43.1));
	    // sailors.addElement(new Sailor(79, "David Wood",         3, 39.2));
	    // sailors.addElement(new Sailor(84, "Mark Smucker",       9, 25.3));
	    // sailors.addElement(new Sailor(87, "Martin Reames",     10, 24.1));
	    // sailors.addElement(new Sailor(10, "Mike Carey",         9, 40.3));
	    // sailors.addElement(new Sailor(21, "David Dewitt",      10, 47.2));
	    // sailors.addElement(new Sailor(29, "Tom Reps",           7, 39.1));
	    // sailors.addElement(new Sailor(31, "Jeff Naughton",      5, 35.0));
	    // sailors.addElement(new Sailor(35, "Miron Livny",        7, 37.6));
	    // sailors.addElement(new Sailor(37, "Marv Solomon",      10, 48.9));

        int numsailors = 3;

        try{
         
        t1.setIntFld(1, ((Sailor)sailors.elementAt(0)).sid);
        t1.setStrFld(2, ((Sailor)sailors.elementAt(0)).sname);
        t1.setIntFld(3, ((Sailor)sailors.elementAt(0)).rating);
        t1.setFloFld(4, (float)((Sailor)sailors.elementAt(0)).age);

        t2.setIntFld(1, ((Sailor)sailors.elementAt(1)).sid);
        t2.setStrFld(2, ((Sailor)sailors.elementAt(1)).sname);
        t2.setIntFld(3, ((Sailor)sailors.elementAt(1)).rating);
        t2.setFloFld(4, (float)((Sailor)sailors.elementAt(1)).age);

        } catch (Exception e) {
            e.printStackTrace();
        }        

        for (int i=0; i<numsailors; i++) {
              try {
                    t.setIntFld(1, ((Sailor)sailors.elementAt(i)).sid);
                    t.setStrFld(2, ((Sailor)sailors.elementAt(i)).sname);
                    t.setIntFld(3, ((Sailor)sailors.elementAt(i)).rating);
                    t.setFloFld(4, (float)((Sailor)sailors.elementAt(i)).age);
              }
              catch (Exception e) {
                System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
                // status = FAIL;
                e.printStackTrace();
              }
              
              try {
                rid = f.insertRecord(t.returnTupleByteArray());
              }
              catch (Exception e) {
                System.err.println("*** error in Heapfile.insertRecord() ***");
                // status = FAIL;
                e.printStackTrace();
              }      
        }

        short[] str_sizes = new short[5];
        int[] pref_list = new int[1];
        pref_list[0] = 0;
        // pref_list[1] = 0;
        int pref_list_length = 1;
        // System.out.println("Result for CompareTupleWithTuplePref integer "+Methods.CompareTupleWithTuplePref(t1, Stypes, t2, Stypes, (short) 5, str_sizes, pref_list, pref_list_length));
// 
       // System.out.println("Result for String "+Methods.Dominates(t3, attrType1, t4, attrType1, (short) 5, str_sizes, pref_list, pref_list_length));
              // create an iterator by open a file scan
    FldSpec[] projlist = new FldSpec[4];
    RelSpec rel = new RelSpec(RelSpec.outer); 
    projlist[0] = new FldSpec(rel, 1);
    projlist[1] = new FldSpec(rel, 2);
    projlist[2] = new FldSpec(rel, 3);
    projlist[3] = new FldSpec(rel, 4);
    
    FileScan fscan = null;
    
    try {
        // System.out.println(fscan);

      fscan = new FileScan("sailors.in", Stypes, Ssizes, (short) 4, 4, projlist, null);
        // System.out.println("Fscan assignment");
      // System.out.println(fscan);
    }
    catch (Exception e) {
      // status = FAIL;
      e.printStackTrace();
    }



        // Iterator am1 = null;
        String test1 = null;
        try{
        System.out.println("Heapfile " + f.getRecCnt());
        } catch(Exception e) {
            e.printStackTrace();
        } 

        // NestedLoopSkyline ns = new NestedLoopSkyline();
        // ArrayList<Tuple> result = ns.NestedLoopsSky(Stypes, 3, str_sizes, fscan, "sailors.in", pref_list, pref_list_length, 10);
        
        BlockNestedLoopSkyline blns = new BlockNestedLoopSkyline();
        ArrayList<Tuple> result = blns.BlockNestedLoopsSky(Stypes, 3, str_sizes, fscan, "sailors.in", pref_list, pref_list_length, 10);

        
        System.out.println("**************** Skyline Result ********************");
        try{
        for (Tuple tt : result) {
            tt.print(Stypes);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
