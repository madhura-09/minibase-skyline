package tests;

import global.AttrType;
import heap.*;
import global.*;
import iterator.Iterator;
import skyline.Methods;
import skyline.NestedLoopSkyline;
import java.io.*;

public class NestedLoopSkylineTest {
    private static int data1[] = {4, 6, 99, 121, 32};
    private static int data2[] = {48, 16, 99, 21, 89};
    private static int data3[] = {14, 26, 39, 41, 52};
    private static int data4[] = {78, 16, 59, 81, 99};

    static int NUM_RECORDS = data2.length;

    public static void main(String[] a) {

        private Vector sailors;
        sailors  = new Vector();


    sailors.addElement(new Sailor(53, "Bob Holloway",       9, 53.6));
    sailors.addElement(new Sailor(54, "Susan Horowitz",     1, 34.2));
    sailors.addElement(new Sailor(57, "Yannis Ioannidis",   8, 40.2));
    sailors.addElement(new Sailor(59, "Deborah Joseph",    10, 39.8));
    sailors.addElement(new Sailor(61, "Landwebber",         8, 56.7));
    sailors.addElement(new Sailor(63, "James Larus",        9, 30.3));
    sailors.addElement(new Sailor(64, "Barton Miller",      5, 43.7));
    sailors.addElement(new Sailor(67, "David Parter",       1, 99.9));   
    sailors.addElement(new Sailor(69, "Raghu Ramakrishnan", 9, 37.1));
    sailors.addElement(new Sailor(71, "Guri Sohi",         10, 42.1));
    sailors.addElement(new Sailor(73, "Prasoon Tiwari",     8, 39.2));
    sailors.addElement(new Sailor(39, "Anne Condon",        3, 30.3));
    sailors.addElement(new Sailor(47, "Charles Fischer",    6, 46.3));
    sailors.addElement(new Sailor(49, "James Goodman",      4, 50.3));
    sailors.addElement(new Sailor(50, "Mark Hill",          5, 35.2));
    sailors.addElement(new Sailor(75, "Mary Vernon",        7, 43.1));
    sailors.addElement(new Sailor(79, "David Wood",         3, 39.2));
    sailors.addElement(new Sailor(84, "Mark Smucker",       9, 25.3));
    sailors.addElement(new Sailor(87, "Martin Reames",     10, 24.1));
    sailors.addElement(new Sailor(10, "Mike Carey",         9, 40.3));
    sailors.addElement(new Sailor(21, "David Dewitt",      10, 47.2));
    sailors.addElement(new Sailor(29, "Tom Reps",           7, 39.1));
    sailors.addElement(new Sailor(31, "Jeff Naughton",      5, 35.0));
    sailors.addElement(new Sailor(35, "Miron Livny",        7, 37.6));
    sailors.addElement(new Sailor(37, "Marv Solomon",      10, 48.9));

    int numsailors = 25;


        Tuple t1 = new Tuple();
        Tuple t2 = new Tuple();

        Tuple t3 = new Tuple();
        Tuple t4 = new Tuple();

        AttrType[] attrType = new AttrType[5];
        attrType[0] = new AttrType(AttrType.attrInteger);
        attrType[1] = new AttrType(AttrType.attrInteger);
        attrType[2] = new AttrType(AttrType.attrInteger);
        attrType[3] = new AttrType(AttrType.attrInteger);
        attrType[4] = new AttrType(AttrType.attrInteger);
        short[] attrSize = new short[5];

        // create a tuple of appropriate size
        try {
            t1.setHdr((short) 5, attrType, attrSize);
            t2.setHdr((short) 5, attrType, attrSize);
            t3.setHdr((short) 5, attrType, attrSize);
            t4.setHdr((short) 5, attrType, attrSize);

            t1.print(attrType);
            t2.print(attrType);
            t3.print(attrType);
            t4.print(attrType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RID             rid;
        Heapfile        f = null;
        try {
            f = new Heapfile("test1.in");
        }
        catch (Exception e) {
//            status = FAIL;
            e.printStackTrace();
        }

        for (int i = 0; i < NUM_RECORDS; i++) {
            try {
                t1.setIntFld(i+1, data1[i]);
                t2.setIntFld(i+1, data2[i]);
                t3.setIntFld(i+1, data3[i]);
                t4.setIntFld(i+1, data4[i]);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                f.insertRecord(t1.returnTupleByteArray());
                f.insertRecord(t2.returnTupleByteArray());
                f.insertRecord(t3.returnTupleByteArray());
                f.insertRecord(t4.returnTupleByteArray());
            }
            catch (Exception e) {
//                status = FAIL;
                e.printStackTrace();
            }

        }
        try {
            t1.print(attrType);
            t2.print(attrType);
            t3.print(attrType);
            t4.print(attrType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        short[] str_sizes = new short[5];
        int[] pref_list = new int[2];
        pref_list[0] = 1;
        pref_list[1] = 2;
        int pref_list_length = 2;
        //System.out.println("Result for integer "+Methods.Dominates(t1, attrType, t2, attrType, (short) 5, str_sizes, pref_list, pref_list_length));

//        System.out.println("Result for String "+Methods.Dominates(t3, attrType1, t4, attrType1, (short) 5, str_sizes, pref_list, pref_list_length));

         // create an iterator by open a file scan
   /* FldSpec[] projlist = new FldSpec[2];
    RelSpec rel = new RelSpec(RelSpec.outer); 
    projlist[0] = new FldSpec(rel, 1);
    projlist[1] = new FldSpec(rel, 2);*/
    
    FileScan fscan = null;
    
    try {
      fscan = new FileScan("test1.in", attrType, attrSize, (short) 5, 5, null, null);
    }
    catch (Exception e) {
      status = FAIL;
      e.printStackTrace();
    }

        Iterator am1 = null;
        String test1 = null;
        NestedLoopSkyline ns = new NestedLoopSkyline();
//        System.out.println(ns.NestedLoopsSky(attrType, NUM_RECORDS, str_sizes, 4,null, null, pref_list, pref_list_length));
        System.out.println(ns.NestedLoopsSky(attrType, 5, str_sizes, fscan, "test1.in", pref_list, pref_list_length, 10));
    }
}
