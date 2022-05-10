package tests;

// import global.AttrType;
import btree.*;
import global.*;
// import global.SystemDefs;
import heap.*;
// import heap.Tuple;
import iterator.*;
import java.util.*;
import java.util.Scanner;
import java.io.*;
import skyline.*;



public class UserSkyline {

    static void read_from_file(String filepath,String relname) {


        String dbpath = "/tmp/"+System.getProperty("user.name")+".minibase.skylinedb4";
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
        RID rid;
        RID rid1;
        Heapfile hf = null;
        int size = 0;
        Heapfile sort_file = null;
        // File f = null;
        // Scanner s = null;
        try {
            hf = new Heapfile(relname);
            sort_file = new Heapfile("sorted_file.in");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try
        {
            File file = new File(filepath);
            FileReader fileReader = new FileReader(file); // A stream that connects to the text file
            BufferedReader bufferedReader = new BufferedReader(fileReader); // Connect the FileReader to the BufferedReader

            String line;

            // int cols = Integer.parseInt(bufferedReader.readLine());
            String ncols = bufferedReader.readLine();
            ArrayList<AttrType> attral = new ArrayList<>();

            // System.out.println("No. of cols " + ncols);
            int cols = Integer.parseInt(ncols.trim());
            System.out.println("No. of cols in the data " + cols);

            for(int i=0;i<cols;i++) {
                AttrType a = new AttrType(AttrType.attrReal);
                attral.add(a);
            }

            short[] attrSize = new short[cols];
            for(int i=0; i<attrSize.length; i++){
                attrSize[i] = 32;
            }


            AttrType[] attrArray = attral.toArray(new AttrType[0]);

            while((line = bufferedReader.readLine()) != null) {
                //System.out.println(line); // Display the file's contents on the screen, one line at a time
                String[] unparsed = line.split("\t");
                Tuple r = new Tuple();



                try {
                    r.setHdr((short) cols, attrArray, attrSize);
                    size = r.size();
                    // System.out.println("size " + size);
                    r = new Tuple(size);

                    r.setHdr((short) cols, attrArray, attrSize);

                    // System.out.println("*************tuple before*************");
                    // r.print(attrArray);
                    for(int i=0;i<cols;i++) {
                        r.setFloFld(i+1 , Float.parseFloat(unparsed[i]));
                    }
                    // System.out.println("*************tuple after*************");
                    // r.print(attrArray);
                    // diskmgr.pcounter.initialize();

                    try {
                        rid = hf.insertRecord(r.returnTupleByteArray());
                        // System.out.println("****************************************************");
                        // System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        // System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                    }
                    catch (Exception e) {
                        System.err.println("*** error in Heapfile.insertRecord() ***");
                        // status = FAIL;
                        e.printStackTrace();
                    }
                    // rid = hf.insertRecord(r.returnTupleByteArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }



            // File Scan

            FldSpec[] projlist = new FldSpec[cols];
            RelSpec rel = new RelSpec(RelSpec.outer);
            for (int i = 0; i < cols; i++) {
                projlist[i] = new FldSpec(rel, i + 1);
            }

            int records = hf.getRecCnt();
            System.out.println("record count " + hf.getRecCnt());
            FileScan fscan = null;
            FileScan fscan1 = null;


            try {
                fscan = new FileScan(relname, attrArray, attrSize, (short) cols, cols, projlist, null);
            }
            catch (Exception e) {
                // status = FAIL;
                e.printStackTrace();
            }


            // System.out.print("FileScan tuple");

            // Tuple tw = new Tuple();
            // while((tw = fscan.get_next()) != null) {
            //     tw.print(attrArray);
            // }



            // SystemDefs.javabaseBM.flushallpages();

            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the number of pages");
            int n_pages = sc.nextInt();
            System.out.println("Enter the number of preference list length");
            int pref_list_length = sc.nextInt();
            System.out.println("Enter the preference attributes");

            int[] pref_list = new int[pref_list_length];

            for (int i = 0; i < pref_list_length; i++) {
                pref_list[i] = sc.nextInt();
            }

            short[] str_sizes = new short[cols];


            TupleOrder[] order = new TupleOrder[2];
            order[0] = new TupleOrder(TupleOrder.Ascending);
            order[1] = new TupleOrder(TupleOrder.Descending);

            IndexFile[] index_file_list = new IndexFile[pref_list_length];

            /*
            // For index file creation
            BTreeFile[] bTreeFiles = new BTreeFile[pref_list_length];
            for (int i = 0; i < pref_list.length; i++) {
                bTreeFiles[i] = new BTreeFile("bTreeName" + i, AttrType.attrReal, 4, 1);
                FileScan fileScan = null;
                // FldSpec[] projlist = new FldSpec[attrArray.length];
                // RelSpec rel = new RelSpec(RelSpec.outer);
                // for(int j =0; j < attrArray.length; j++){
                //     projlist[j] = new FldSpec(rel, j+1);
                // }
                // fscan = new FileScan(relname, attrArray, attrSize, (short) cols, cols, projlist, null);

                fileScan = new FileScan(relname, attrArray, attrSize, (short) cols, cols, projlist, null);

                Tuple tuple = new Tuple(size);
                // r.setHdr((short) cols, attrArray, attrSize);

                tuple.setHdr((short) cols, attrArray, attrSize);
                RID rid2;
                rid2 = fileScan.getRID();
                tuple = fileScan.get_next();
                System.out.println("rid2 "+rid2.slotNo + " "+ rid2.pageNo);

                while (tuple != null) {
                    if (rid2 != null){
                        bTreeFiles[i].insert(new FloatKey(tuple.getFloFld(i + 1)), rid2);
                        rid2 = fileScan.getRID();
                        tuple = fileScan.get_next();
                    }
                    else {
                        tuple = fileScan.get_next();
                        rid2 = fileScan.getRID();
                        rid2.slotNo = 0;
                        
                    }
                }
            }

            // end of index file creation

            */

            // SystemDefs.JavabaseBM.flushAllPages();


            int choice = 0;

            while(choice != 3) {

                diskmgr.pcounter.initialize();

                System.out.println("-------------------------- MENU ------------------");
                System.out.println("\n\n[0]   NestedLoopSkyline");
                System.out.println("[1]   BlockNestedLoopSkyline");

                System.out.println("[2]   SortFirstSky");
                // System.out.println("[3]   BTreeSky");
                // System.out.println("[4]   BTreeSortedSky");

                System.out.println("[3]   Exit");

                choice = sc.nextInt();

                switch(choice) {
                    case 0:
                        System.out.println("****************** NestedLoopSkyline ************");
                        System.out.println("MINIBASE_BUFFER_POOL_SIZE " + GlobalConst.MINIBASE_PAGESIZE);
                        NestedLoopSkyline ns = new NestedLoopSkyline();
                        ArrayList<Tuple> result = ns.NestedLoopsSky(attrArray, records, str_sizes, fscan, "userskyline1.in", pref_list, pref_list_length, n_pages);
                        System.out.println("****************** NestedLoopSkyline ************");
                        for (Tuple qw : result) {
                            qw.print(attrArray);
                        }
                        System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        diskmgr.pcounter.initialize();

                        break;
                    case 1:
                        System.out.println("****************** BlockNestedLoopSkyline ************");
                        System.out.println("MINIBASE_BUFFER_POOL_SIZE " + GlobalConst.MINIBASE_PAGESIZE);
                        BlockNestedLoopSkyline blns = new BlockNestedLoopSkyline();
                        ArrayList<Tuple> result1 = blns.BlockNestedLoopsSky(attrArray, records, str_sizes, fscan, "userskyline1.in", pref_list, pref_list_length, n_pages);
                        for (Tuple qw : result1) {
                            qw.print(attrArray);
                        }

                        System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        diskmgr.pcounter.initialize();

                        break;
                    case 2:
                        System.out.println("****************** SortSkyFirst ************");
                        System.out.println("MINIBASE_BUFFER_POOL_SIZE " + GlobalConst.MINIBASE_PAGESIZE);
                        //System.out.println("****************** Sort tuples ************");
                        SortPref sort = null;
                        sort = new SortPref(attrArray, (short) cols, str_sizes, fscan, order[0], pref_list, pref_list_length, n_pages);
                        Tuple s = new Tuple();
                        while((s = sort.get_next()) != null) {
                            Tuple ss = new Tuple(s);
                            // ss.print(attrArray);
                            rid1 = sort_file.insertRecord(ss.returnTupleByteArray());
                        }

                        // System.out.println("after sort " + sort_file.getRecCnt());


                        //    System.out.println("***************** sort records *********");

                        //    try {
                        //   fscan1 = new FileScan("sorted_file.in", attrArray, attrSize, (short) cols, cols, projlist, null);
                        // }
                        // catch (Exception e) {
                        //   // status = FAIL;
                        //   e.printStackTrace();
                        // }

                        // Tuple we = new Tuple();
                        // try{
                        // while ((we = fscan1.get_next()) != null) {
                        //     we.print(attrArray);
                        // }
                        // } catch (Exception e) {
                        //     e.printStackTrace();
                        // }


                        sort.close();

                        // System.out.println("****************** Sort First Skyline ************");
                        SortFirstSky sfs = new SortFirstSky();
                        ArrayList<Tuple> result2 = sfs.SortFirstSkyline(attrArray, records, str_sizes, fscan, "sorted_file.in", pref_list, pref_list_length, n_pages);
                        for (Tuple qw : result2) {
                            qw.print(attrArray);
                        }


                        System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        diskmgr.pcounter.initialize();

                        //System.out.println("after sort " + sort_file.getRecCnt());
                        break;
                    /*    
                    case 3:
                        System.out.println("****************** BTreeSky ************");
                        BTreeSky bts = new BTreeSky();
                        ArrayList<Tuple> result3 = bts.BTreeSky(attrArray, records, str_sizes, fscan, "userskyline1.in", pref_list, pref_list_length, bTreeFiles,
                            n_pages);
                        for (Tuple qw : result3) {
                            qw.print(attrArray);
                        }


                        System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        diskmgr.pcounter.initialize();
                        break;
                    case 4:
                        System.out.println("****************** BTreeSortedSky ************");
                        // BTreeSortedSky bts = new bTreeSortedSky();
                        // ArrayList<Tuple> result3 = bts.bTreeSortedSky(attrArray, records, str_sizes, fscan, "sorted_file.in", pref_list, pref_list_length, n_pages);
                        // for (Tuple qw : result2) {
                        //     qw.print(attrArray);
                        // }


                        // System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        // System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        // diskmgr.pcounter.initialize();
                        // break;
                        */
                }

            }

            // System.out.println("MINIBASE_BUFFER_POOL_SIZE " + GlobalConst.MINIBASE_BUFFER_POOL_SIZE);
            // NestedLoopSkyline ns = new NestedLoopSkyline();
            // ArrayList<Tuple> result = ns.NestedLoopsSky(attrArray, records, str_sizes, fscan, "userskyline1.in", pref_list, pref_list_length, n_pages);
            // System.out.println("****************** NestedLoopSkyline ************");
            // for (Tuple qw : result) {
            //     qw.print(attrArray);
            // }

            // System.out.println("****************** BlockNestedLoopSkyline ************");
            //  BlockNestedLoopSkyline blns = new BlockNestedLoopSkyline();
            // ArrayList<Tuple> result = blns.BlockNestedLoopsSky(attrArray, records, str_sizes, fscan, "userskyline1.in", pref_list, pref_list_length, n_pages);
            //  for (Tuple qw : result) {
            //     qw.print(attrArray);
            //  }


            // System.out.println("****************** Sort tuples ************");
            // SortPref sort = null;
            // sort = new SortPref(attrArray, (short) cols, str_sizes, fscan, order[0], pref_list, pref_list_length, n_pages);
            // Tuple s = new Tuple();
            // while((s = sort.get_next()) != null) {
            //     Tuple ss = new Tuple(s);
            //     // ss.print(attrArray);
            //     rid1 = sort_file.insertRecord(ss.returnTupleByteArray());
            // }

            // System.out.println("after sort " + sort_file.getRecCnt());

            // System.out.println("****************** Sort First Skyline ************");
            // SortFirstSky sfs = new SortFirstSky();
            // ArrayList<Tuple> result = sfs.SortFirstSkyline(attrArray, records, str_sizes, fscan, "sorted_file.in", pref_list, pref_list_length, n_pages);
            // for (Tuple qw : result) {
            //     qw.print(attrArray);
            // }

            // try {
            //   fscan1 = new FileScan("sorted_file.in", attrArray, attrSize, (short) cols, cols, projlist, null);
            // }
            // catch (Exception e) {
            //   // status = FAIL;
            //   e.printStackTrace();
            // }

            // Tuple we = new Tuple();
            // try{
            // while ((we = fscan1.get_next()) != null) {
            //     we.print(attrArray);
            // }
            // } catch (Exception e) {
            //     e.printStackTrace();
            // }


            // sort.close();


            // System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
            // System.out.println("Write count " + diskmgr.pcounter.get_wcounter());




            bufferedReader.close(); // Close the stream

            sc.close();
            fscan.close();


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



    }
    public static void main(String[] args){
        UserSkyline.read_from_file("/afs/asu.edu/users/r/t/h/rthoraha/DBMSI/minjava/javaminibase/src/tests/TestInputs/pc_dec_3_500.txt","userskyline1.in");
    }

}

