package tests;

import java.io.*;
import java.util.*;
import java.lang.*;
import heap.*;
import bufmgr.*;
import diskmgr.*;
import global.*;
import btree.*;
import iterator.*;
import skyline.*;

/** Note that in JAVA, methods can't be overridden to be more private.
 Therefore, the declaration of all private functions are now declared
 protected as opposed to the private type in C++.
 */

//watching point: RID rid, some of them may not have to be newed.

class BTDriver  implements GlobalConst
{

    public BTreeFile file;
    public int postfix=0;
    public int keyType;
    public BTFileScan scan;

    protected String dbpath;
    protected String logpath;
    public int deleteFashion;

    public void runTests () throws PageUnpinnedException, ReplacerException, IteratorException, ConstructPageException, InvalidFrameNumberException, HashEntryNotFoundException {
        Random random = new Random();
        dbpath = "BTREE" + random.nextInt() + ".minibase-db";
        logpath = "BTREE" + random.nextInt() + ".minibase-log";


        SystemDefs sysdef = new SystemDefs( dbpath, 5000 ,5000,"Clock");
        System.out.println ("\n" + "Running " + " tests...." + "\n");

        keyType=AttrType.attrInteger;

        // Kill anything that might be hanging around
        String newdbpath;
        String newlogpath;
        String remove_logcmd;
        String remove_dbcmd;
        String remove_cmd = "/bin/rm -rf ";

        newdbpath = dbpath;
        newlogpath = logpath;

        remove_logcmd = remove_cmd + logpath;
        remove_dbcmd = remove_cmd + dbpath;

        // Commands here is very machine dependent.  We assume
        // user are on UNIX system here
        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
        }
        catch (IOException e) {
            System.err.println ("IO error: "+e);
        }

        remove_logcmd = remove_cmd + newlogpath;
        remove_dbcmd = remove_cmd + newdbpath;

        //This step seems redundant for me.  But it's in the original
        //C++ code.  So I am keeping it as of now, just in case I
        //I missed something
        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
        }
        catch (IOException e) {
            System.err.println ("IO error: "+e);
        }

        //Run the tests. Return type different from C++
        runAllTests();

        //Clean up again
        try {
            Runtime.getRuntime().exec(remove_logcmd);
            Runtime.getRuntime().exec(remove_dbcmd);
        }
        catch (IOException e) {
            System.err.println ("IO error: "+e);
        }

        System.out.print ("\n" + "..." + " Finished ");
        System.out.println (".\n\n");


    }

    private void menu() {
        System.out.println("-------------------------- MENU ------------------");
        System.out.println("\n\n[0]   Naive delete (new file)");
        System.out.println("[1]   Full delete(Default) (new file)");

        System.out.println("\n[2]   Print the B+ Tree Structure");
        System.out.println("[3]   Print All Leaf Pages");
        System.out.println("[4]   Choose a Page to Print");

        System.out.println("\n           ---Integer Key (for choices [6]-[14]) ---");
        System.out.println("\n[5]   Insert a Record");
        System.out.println("[6]   Delete a Record");
        System.out.println("[7]   Test1 (new file): insert n records in order");
        System.out.println("[8]   Test2 (new file): insert n records in reverse order");
        System.out.println("[9]   Test3 (new file): insert n records in random order");
        System.out.println("[10]  Test4 (new file): insert n records in random order");
        System.out.println("      and delete m records randomly");
        System.out.println("[11]  Delete some records");

        System.out.println("\n[12]  Initialize a Scan");
        System.out.println("[13]  Scan the next Record");
        System.out.println("[14]  Delete the just-scanned record");
        System.out.println("\n           ---String Key (for choice [15]) ---");
        System.out.println("\n[15]  Test5 (new file): insert n records in random order  ");
        System.out.println("        and delete m records randomly.");

        System.out.println("\n[16]  Close the file");
        System.out.println("[17]  Open which file (input an integer for the file name): ");
        System.out.println("[18]  Destroy which file (input an integer for the file name): ");
        System.out.println("\n[19] Testing the new code implemented for BTreeSky");
        System.out.println("\n[20] Testing the new code implemented for BTreeSortedSky");
        System.out.println("\n[21] Quit");
        System.out.print("Hi, make your choice :");
    }


    protected void runAllTests ()  throws PageUnpinnedException, ReplacerException, IteratorException, ConstructPageException, InvalidFrameNumberException, HashEntryNotFoundException {
        PageId pageno=new PageId();
        int  key, n,m, num, choice, lowkeyInt, hikeyInt;
        KeyClass lowkey, hikey;
        KeyDataEntry entry;
        RID rid;
        choice=1;
        deleteFashion=1; //full delete
        try{
            System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
            file=new BTreeFile("AAA"+postfix, keyType, 4, 1);//full delete
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }
        postfix=0;
        while(choice!=21) {
            menu();

            try{
                choice=GetStuff.getChoice();

                switch(choice) {
                    case 0:
                        postfix++;
                        deleteFashion=0; //naive delete
                        System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                        file=new BTreeFile("AAA"+postfix, keyType, 100, 0);//naive delete
                        break;
                    case 1:
                        postfix++;
                        deleteFashion=1; //full delete
                        System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                        file=new BTreeFile("AAA"+postfix, keyType, 100, 1);//full delete
                        break;
                    case 2:
                        BT.printBTree(file.getHeaderPage());
                        break;
                    case 3:
                        System.out.println("Please input the page number: ");
                        num=GetStuff.getChoice();
                        //BTFileScan.getLastLeafPage(file.getHeaderPage());
                        BT.printAllLeafPages(file.getHeaderPage());
                        break;
                    case 4:
                        System.out.println("Please input the page number: ");
                        num=GetStuff.getChoice();
                        if(num<0) break;
                        BT.printPage(new PageId(num), keyType);
                        break;
                    case 5:
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the integer key to insert: ");
                        key=GetStuff.getChoice();
                        if(key<0) break;
                        pageno.pid=key;
                        rid=new RID(pageno, key);
                        file.insert(new IntegerKey(key), rid);
                        break;
                    case 6:
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the integer key to delete: ");
                        key=GetStuff.getChoice();
                        if(key<0) break;
                        pageno.pid=key;
                        rid=new RID(pageno, key);
                        file.Delete(new IntegerKey(key), rid);
                        break;
                    case 7:
                        file.close();
                        postfix++;
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the number of keys to insert: ");
                        n=GetStuff.getChoice();
                        if(n<=0) break;
                        test1(n);
                        break;
                    case 8:
                        file.close();
                        postfix++;
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the number of keys to insert: ");
                        n=GetStuff.getChoice();
                        if(n<=0) break;
                        test2(n);
                        break;
                    case 9:
                        file.close();
                        postfix++;
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the number of keys to insert: ");
                        n=GetStuff.getChoice();
                        if(n<=0) break;
                        test3(n);
                        break;
                    case 10:
                        file.close();
                        postfix++;
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the number of keys to insert: ");
                        n=GetStuff.getChoice();
                        System.out.println("Please input the number of keys to delete: ");
                        m=GetStuff.getChoice();
                        if(n<=0 || m<0) break;
                        if( m>n) m=n;
                        test4(n,m);
                        break;
                    case 11:
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the LOWER integer key(>=0): ");
                        lowkeyInt=GetStuff.getChoice();
                        System.out.println("Please input the HIGHER integer key(>=0) ");
                        hikeyInt=GetStuff.getChoice();
                        if( hikeyInt<0 || lowkeyInt<0 )
                            break;
                        for( key=lowkeyInt; key<=hikeyInt; key++) {
                            pageno.pid=key;
                            rid=new RID(pageno, key);
                            file.Delete(new IntegerKey(key), rid);
                        }
                        break;

                    case 12:
                        keyType=AttrType.attrInteger;
                        System.out.println("Please input the LOWER integer key (null if -3): ");
                        lowkeyInt=GetStuff.getChoice();
                        lowkey=new IntegerKey(lowkeyInt);
                        System.out.println("Please input the HIGHER integer key (null if -2): ");
                        hikeyInt=GetStuff.getChoice();
                        hikey=new IntegerKey(hikeyInt);
                        if( lowkeyInt==-3)
                            lowkey=null;
                        if( hikeyInt==-2)
                            hikey=null;
                        if( hikeyInt==-1 || lowkeyInt==-1 )
                            break;
                        scan= file.new_scan(lowkey, hikey);
                        break;

                    case 13:
                        entry=scan.get_next();
                        if(entry!=null)
                            System.out.println("SCAN RESULT: "+ entry.key + " " + entry.data);
                        else
                            System.out.println("AT THE END OF SCAN!");
                        break;
                    case 14:
                        scan.delete_current();
                        break;
                    case 15:
                        file.close();
                        postfix++;
                        keyType=AttrType.attrString;
                        System.out.println("Please input the number of keys to insert: ");
                        n=GetStuff.getChoice();
                        System.out.println("Please input the number of keys to delete: ");
                        m=GetStuff.getChoice();
                        if(n<=0 || m<0) break;
                        if( m>n) m=n;
                        test5(n,m);
                        break;
                    case 16:
                        file.close();
                        System.out.println(" ***************** You close the file: "+ "AAA"+postfix +"  **********");
                        break;
                    case 17:
                        file.close();
                        n=GetStuff.getChoice();
                        System.out.println(" ***************** You open the file: "+ "AAA"+n +"  **********");
                        file = new BTreeFile("AAA"+n);
                        break;
                    case 18:
                        file.close();
                        n=GetStuff.getChoice();
                        System.out.println(" ***************** You destroy the file: "+ "AAA"+n +"  **********");
                        file = new BTreeFile("AAA"+n);
                        file.destroyFile();
                        break;
                    case 19:
                        //
                        //
                        // Code to pull data from the txt file and put it in the Heap file
                        //

                        String bTreeName = "btree";
                        String relationName = "largeData";
                        String filePath = "TestInputs/pc_dec_2_3000_multi11.txt";

                        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                        Scanner sc = new Scanner(System.in);
                        int attrCount = Integer.parseInt(bufferedReader.readLine().replace("\t",""));
                        System.out.println("Testing BTreeSky for Data in file "+filePath);
                        int pref_list_lenght;
                        int[] pref_list = new int[attrCount];
                        System.out.println("Enter the number of pages needed of the buffer");
                        int bufferCount = sc.nextInt();
                        System.out.println("Enter the preferred attributes count");
                        pref_list_lenght = sc.nextInt();

                        System.out.println("Enter the next preferred coloumns starting with 0");
                        for(int i = 0; i < pref_list_lenght;i++){
                            System.out.println("Enter the next preferred coloumn");
                            pref_list[i] = sc.nextInt();
                        }
                        sc.close();
                        String curLine;
                        Heapfile heapfile = new Heapfile(relationName);
                        // RID rid1 = new RID();
                        RID rid1;
                        Tuple t = new Tuple();
                        AttrType [] Stypes = new AttrType[attrCount];
                        for (int i=0;i<attrCount;i++){
                            Stypes[i] = new AttrType (AttrType.attrReal);
                        }
                        short[] Ssizes = new short[1];
                        Ssizes[0] = 0; //first elt. is 30

                        // create a tuple of appropriate size
                        try {
                            t.setHdr((short) attrCount, Stypes, Ssizes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int size = t.size();
                        t = new Tuple(size);
                        try {
                            t.setHdr((short) attrCount, Stypes, Ssizes);
                        }
                        catch (Exception e) {
                            System.err.println("*** error in Tuple.setHdr() ***");
                            // status = FAIL;
                            e.printStackTrace();
                        }
                        while ((curLine = bufferedReader.readLine()) != null) {
                            //process the line as required
                            String[] valueString = curLine.split("\t");
                            for (int j = 0; j < attrCount; j++) {
                                try {
                                    t.setFloFld(j + 1, Float.parseFloat(valueString[j]));
                                } catch (Exception e) {
                                    System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
                                    e.printStackTrace();
                                }
                            }
                            try {
                                rid1 = heapfile.insertRecord(t.returnTupleByteArray());
                            } catch (Exception e) {
                                System.err.println("*** error in Heapfile.insertRecord() ***");
                                e.printStackTrace();
                            }
                        }
                        bufferedReader.close();

                        //
                        //
                        //
                        // Code to insert the heap file data into the btree to create Indexes
                        //
                        //
                        try {

                            BTreeFile[] bTreeFiles = new BTreeFile[attrCount];
                            for (int i = 0; i < pref_list.length; i++) {
                                bTreeFiles[i] = new BTreeFile(bTreeName + i, AttrType.attrReal, 4, deleteFashion);
                                FileScan fileScan = null;
                                FldSpec[] projlist = new FldSpec[Stypes.length];
                                RelSpec rel = new RelSpec(RelSpec.outer);
                                for(int j =0; j < Stypes.length; j++){
                                    projlist[j] = new FldSpec(rel, j+1);
                                }
                                fileScan = new FileScan(relationName, Stypes, Ssizes, (short) attrCount, attrCount, projlist, null);
                                Tuple tuple = new Tuple(size);
                                tuple.setHdr((short) Stypes.length, Stypes, Ssizes);
                                // RID rid2 = new RID();
                                RID rid2;
                                rid2 = fileScan.getRID();
                                // RID prevRID2 = new RID();
                                RID prevRID2;
                                prevRID2 = rid2;
                                tuple = fileScan.get_next();
                                while (tuple != null) {
                                    if (rid2 != null){
                                        bTreeFiles[i].insert(new FloatKey(tuple.getFloFld(i + 1)), rid2);
                                        prevRID2 = rid2;
                                        rid2 = fileScan.getRID();
                                        tuple = fileScan.get_next();
                                    }
                                    else {
                                        PageId pageId = new PageId(prevRID2.pageNo.pid+1);
                                        rid2 = new RID(pageId,0);
										if(tuple==null)
                                        {
											tuple = fileScan.get_next();
										}
                                        
                                    }
                                }
                            }
                            //
                            //
                            // Code to find the skyline candidates from the Btree
                            //
                            // /
                            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                            ArrayList<Tuple> result = new ArrayList<Tuple>();
                            BTreeSky bTreeSky = new BTreeSky();
                            result = bTreeSky.BTreeSky(Stypes,Stypes.length,Ssizes,null,"largeData",pref_list,attrCount,bTreeFiles,bufferCount);
                            for(Tuple tup: result){
                                tup.print(Stypes);
                            }

                        System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        diskmgr.pcounter.initialize();
                            // System.out.println("result.size(): "+result.size());
                        }
                    catch(Exception e){
                                // status = FAIL;
                                e.printStackTrace();
                            }
                        /*String filePath = "src/data_large_skyline.txt";
                        System.out.println("data_large_skyline.txt");
                        System.out.println(new File(".").getAbsolutePath());*/
                        break;

                        case 20:
                            String bTreeName1 = "btree";
                            String relationName1 = "largeData";
                            String filePath1 = "TestInputs/pc_dec_2_3000_multi11.txt";
                            BufferedReader bufferedReader1 = new BufferedReader(new FileReader(filePath1));
                            Scanner sc1 = new Scanner(System.in);
                            int attrCount1 = Integer.parseInt(bufferedReader1.readLine().replace("\t",""));
                            System.out.println("Testing BTreeSky for Data in file "+filePath1);
                            int pref_list_length;
                            int[] pref_list1 = new int[attrCount1];
                            System.out.println("Enter the number of pages needed of the buffer");
                            int bufferCount1 = sc1.nextInt();
                            System.out.println("Enter the preffered attributes count");
                            pref_list_length = sc1.nextInt();

                            System.out.println("Enter the next preffered coloumns starting with 0");
                            for(int i = 0; i < pref_list_length;i++){
                                System.out.println("Enter the next preffered coloumn");
                                pref_list1[i] = sc1.nextInt();
                            }
                            sc1.close();
                            System.out.println("Please input the name of document in tests folder");


                                String curLine1;
                                String name=new String(relationName1) ;
                                Heapfile heapfile1 = new Heapfile(name);
                                // RID rid11 = new RID();
                                RID rid11;


                            for(int i = 0; i < attrCount1;i++){
                                pref_list1[i] = i;
                            }
                                System.out.println("attrCount "+attrCount1);
                                Tuple t1 = new Tuple();
                                AttrType [] Stypes1 = new AttrType[attrCount1];
                                for (int i=0;i<attrCount1;i++){
                                    Stypes1[i] = new AttrType (AttrType.attrReal);
                                }
                                short[] Ssizes1 = new short[1];
                                Ssizes1[0] = 0; //first elt. is 30
                                // create a tuple of appropriate size
                                try {
                                    t1.setHdr((short) attrCount1, Stypes1, Ssizes1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                int size1 = t1.size();
                                t = new Tuple(size1);
                                try {
                                    t.setHdr((short) attrCount1, Stypes1, Ssizes1);
                                }
                                catch (Exception e) {
                                    System.err.println("*** error in Tuple.setHdr() ***");
                                    // status = FAIL;
                                    e.printStackTrace();
                                }
                                while ((curLine1 = bufferedReader1.readLine()) != null) {
                                    //process the line as required
                                    String[] valueString = curLine1.split("\t");
                                    for (int j = 0; j < attrCount1; j++) {
                                        try {
                                            t.setFloFld(j + 1, Float.parseFloat(valueString[j]));
                                        } catch (Exception e) {
                                            System.err.println("*** Heapfile error in Tuple.setStrFld() ***");
                                            e.printStackTrace();
                                        }
                                    }
                                    try {
                                        rid11 = heapfile1.insertRecord(t.returnTupleByteArray());
                                    } catch (Exception e) {
                                        System.err.println("*** error in Heapfile.insertRecord() ***");
                                        e.printStackTrace();
                                    }
                                }
                                bufferedReader1.close();
                                //
                                //
                                //
                                // Code to insert the heap file data into the btree to create Indexes
                                //
                                //
                                try {
                                    BTreeFile bTreeFile= new BTreeFile("btree" , AttrType.attrReal, 4, deleteFashion);
                                    FileScan fileScan1 = null;
                                    FldSpec[] projlist = new FldSpec[attrCount1];
                                    RelSpec rel = new RelSpec(RelSpec.outer);
                                    for(int j =0; j < Stypes1.length; j++){
                                        projlist[j] = new FldSpec(rel, j+1);
                                    }
                                    fileScan1 = new FileScan(name, Stypes1, Ssizes1, (short) attrCount1, attrCount1, projlist, null);
                                    Tuple tuple = new Tuple(size1);
                                    tuple.setHdr((short) attrCount1, Stypes1, Ssizes1);
                                    RID rid2 = new RID();
                                    rid2 = fileScan1.getRID();
                                    tuple = fileScan1.get_next();
                                    tuple.print(Stypes1);
                                    while (tuple != null) {
                                        float maxValue=(float) -1;
                                        int maxIndex=(int) -1;
                                        for(int f=0;f< Stypes1.length;f++){
                                            if(tuple.getFloFld(f+1)>maxValue){
                                                maxValue=tuple.getFloFld(f+1);
                                                maxIndex=f+1;
                                            }
                                        }
                                        if (rid2 != null){
                                            bTreeFile.insert(new FloatKey((float) maxIndex + maxValue) , rid2);
                                            rid2 = fileScan1.getRID();
                                            tuple = fileScan1.get_next();
                                        }
                                        else {
                                            rid2 = fileScan1.getRID();
                                            rid2.slotNo = 0;
                                            tuple = fileScan1.get_next();
                                        }
                                    }

                                    ArrayList<Tuple> result = new ArrayList<Tuple>();
                                    BTreeSortedSky test = new BTreeSortedSky();
                                    result = test.bTreeSortedSky(Stypes1,Stypes1.length,Ssizes1,null,name,pref_list1,pref_list1.length,bTreeFile,bufferCount1);
                                    for (Tuple qw : result) {
                                        qw.print(Stypes1);
                                    }
                                }
                                catch(Exception e){
                                    // status = FAIL;
                                    e.printStackTrace();
                                }

                        System.out.println("Read count " + diskmgr.pcounter.get_rcounter());
                        System.out.println("Write count " + diskmgr.pcounter.get_wcounter());
                        diskmgr.pcounter.initialize();
                                break;
                    case 21:
                        break;
                        }
break;

                }
            catch(Exception e) {
                    e.printStackTrace();
                    System.out.println("       !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    System.out.println("       !!         Something is wrong                    !!");
                    System.out.println("       !!     Is your DB full? then exit. rerun it!     !!");
                    System.out.println("       !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

                }
            }
        }



        void test1(int n)
            throws Exception
        {
            try {
                System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                file=new BTreeFile("AAA"+postfix, keyType, 4, deleteFashion);
                file.traceFilename("TRACE");

                KeyClass key;
                RID rid=new RID();
                PageId pageno=new PageId();
                for(int i=0;i<n;i++) {
                    key=new IntegerKey(i);
                    pageno.pid=i;
                    rid=new RID(pageno, i);

                    file.insert(key, rid);

                }

            }
            catch(Exception e) {
                throw e;
            }


        }



        void test2(int n)
            throws Exception
        {
            try {

                System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                file=new BTreeFile("AAA"+postfix, keyType, 4, deleteFashion);
                file.traceFilename("TRACE");

                KeyClass key;
                RID rid=new RID();
                PageId pageno=new PageId();
                for(int i=0;i<n;i++) {
                    key=new IntegerKey(n-i);
                    pageno.pid=n-i;
                    rid=new RID(pageno, n-i);

                    file.insert(key, rid);

                }

            }
            catch(Exception e) {
                throw e;
            }
        }



        void test3(int n)
            throws Exception
        {
            try {
                System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                file=new BTreeFile("AAA"+postfix, keyType, 4, deleteFashion);
                file.traceFilename("TRACE");

                int[] k=new int[n];
                for(int i=0;i<n;i++) {
                    k[i]=i;
                }
                Random ran=new Random();
                int random;
                int tmp;
                for(int i=0;i<n;i++) {

                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }


                KeyClass key;
                RID rid=new RID();
                PageId pageno=new PageId();
                for(int i=0;i<n;i++) {
                    key=new IntegerKey(k[i]);
                    pageno.pid=k[i];
                    rid=new RID(pageno, k[i]);

                    file.insert(key, rid);

                }

            }
            catch(Exception e) {
                throw e;
            }
        }



        void test4(int n, int m)
            throws Exception
        {
            try {
                System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                file=new BTreeFile("AAA"+postfix, keyType, 4, deleteFashion);
                file.traceFilename("TRACE");

                int[] k=new int[n];
                for(int i=0;i<n;i++) {
                    k[i]=i;
                }
                Random ran=new Random();
                int random;
                int tmp;
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }


                KeyClass key;
                RID rid=new RID();
                PageId pageno=new PageId();
                for(int i=0;i<n;i++) {
                    key=new IntegerKey(k[i]);
                    pageno.pid=k[i];
                    rid=new RID(pageno, k[i]);

                    file.insert(key, rid);

                }

                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }

                for(int i=0;i<m;i++) {
                    key=new IntegerKey(k[i]);
                    pageno.pid=k[i];
                    rid=new RID(pageno, k[i]);

                    if ( file.Delete(key, rid)==false) {
                        System.out.println("*********************************************************");
                        System.out.println("*     Your delete method has bug!!!                     *");
                        System.out.println("*     You insert a record, But you failed to delete it. *");
                        System.out.println("*********************************************************");
                    }
                }



            }
            catch(Exception e) {
                throw e;
            }
        }



        void test5(int n, int m)
            throws Exception
        {
            try {

                System.out.println(" ***************** The file name is: "+ "AAA"+postfix +"  **********");
                file=new BTreeFile("AAA"+postfix, keyType, 20, deleteFashion);
                file.traceFilename("TRACE");

                int[] k=new int[n];
                for(int i=0;i<n;i++) {
                    k[i]=i;
                }

                Random ran=new Random();
                int random;
                int tmp;
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }


                KeyClass key;
                RID rid=new RID();
                PageId pageno=new PageId();
                for(int i=0;i<n;i++) {
                    key=new StringKey("**"+k[i]);
                    pageno.pid=k[i];
                    rid=new RID(pageno, k[i]);

                    file.insert(key, rid);

                }

                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }
                for(int i=0;i<n;i++) {
                    random=(ran.nextInt())%n;
                    if (random<0) random=-random;
                    tmp=k[i];
                    k[i]=k[random];
                    k[random]=tmp;
                }

                for(int i=0;i<m;i++) {
                    key=new StringKey("**"+k[i]);
                    pageno.pid=k[i];
                    rid=new RID(pageno, k[i]);

                    if ( file.Delete(key, rid)==false) {
                        System.out.println("*********************************************************");
                        System.out.println("*     Your delete method has bug!!!                     *");
                        System.out.println("*     You insert a record, But you failed to delete it. *");
                        System.out.println("*********************************************************");
                    }

                }



            }
            catch(Exception e) {
                throw e;
            }
        }



    }


    /**
     * To get the integer off the command line
     */
    class GetStuff {
        GetStuff() {}

        public static int getChoice () {

            BufferedReader in = new BufferedReader (new InputStreamReader(System.in));
            int choice = -1;

            try {
                choice = Integer.parseInt(in.readLine());
            }
            catch (NumberFormatException e) {
                return -1;
            }
            catch (IOException e) {
                return -1;
            }

            return choice;
        }

        public static void getReturn () {

            BufferedReader in = new BufferedReader (new InputStreamReader(System.in));

            try {
                String ret = in.readLine();
            }
            catch (IOException e) {}
        }
    }

    public class BTTest implements  GlobalConst{

        public static void main(String [] argvs) {

            try{
                BTDriver bttest = new BTDriver();
                bttest.runTests();
            }
            catch (Exception e) {
                e.printStackTrace();
                System.err.println ("Error encountered during buffer manager tests:\n");
                Runtime.getRuntime().exit(1);
            }
        }

    }