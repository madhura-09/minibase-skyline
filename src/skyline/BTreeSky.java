package skyline;

import btree.*;
import bufmgr.HashEntryNotFoundException;
import bufmgr.InvalidFrameNumberException;
import bufmgr.PageUnpinnedException;
import bufmgr.ReplacerException;
import global.AttrType;
import global.PageId;
import global.RID;
import heap.*;
import iterator.FileScan;
import iterator.FldSpec;
import iterator.Iterator;
import iterator.RelSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;


public class BTreeSky {

    // Btree index scan and make the indexes
    //Ready to use index files
    //Function to access the right most page to find the last element in the BTree and there after moving back wards in an index
    // As moving backwards we will be comparing with the other indexes which are available
    // and keep the dataRID of the index in the Hashmap
    // Once we check the other indexes for common rid and we find one we will stop the run
    // now all the list of RIDs that are available in the Hashmap would be our skyline candidates
    // All the RIDs will be send to block nested loop skyline so as to find the final skyline candidates

    public ArrayList<Tuple> BTreeSky(AttrType[] in1, int len_in1, short[] t1_str_sizes,
                                     Iterator am1, java.lang.String
                                             relationName, int[] pref_list, int pref_list_length,
                                     IndexFile[] index_file,
                                     int n_pages)
            throws ConstructPageException
            , GetFileEntryException
            , PinPageException, HashEntryNotFoundException, IteratorException, InvalidFrameNumberException, PageUnpinnedException, ReplacerException, IOException, HFDiskMgrException, HFBufMgrException, HFException {
        //
        Hashtable<String, Integer> ridDict = new Hashtable<String, Integer>();
        int ridDictSize;
        BTreeFile[] bTreeFile = new BTreeFile[index_file.length];
        Heapfile heap = new Heapfile("skylineCandidates");
        ArrayList<Tuple> result = new ArrayList<Tuple>();
        for(int i = 0;i< index_file.length; i++){
            bTreeFile[i] = (BTreeFile) index_file[i];
        }
        try{
            outerLoop:
            while(true){
                for (int i = 0;i < pref_list_length; i++) {
                    RID dataRID = new RID ();
                    dataRID = bTreeFile[i].getNextLastRID();
                    //Converting the RID into string so as to compare two RIDs in the dictionary
                    String dataRIDString = dataRID.pageNo+":"+dataRID.slotNo;
                    if (ridDict.containsKey(dataRIDString)) {
                        ridDict.put(dataRIDString, ridDict.get(dataRIDString) + 1);
                        if (ridDict.get(dataRIDString) == pref_list_length) {
                            break outerLoop;
                        }
                    } else {
                        ridDict.put(dataRIDString, 1);
                    }
                }
            }
            ridDictSize = ridDict.size();
            FileScan fileScan = null;
            FldSpec[] projlist = new FldSpec[len_in1];
            RelSpec rel = new RelSpec(RelSpec.outer);
            for(int i = 0; i < len_in1; i++){
                projlist[i] = new FldSpec(rel, i+1);
            }
            fileScan = new FileScan(relationName, in1, t1_str_sizes, (short) len_in1, len_in1, projlist, null);
            Tuple tuple = new Tuple();
            tuple.setHdr((short) len_in1,in1,t1_str_sizes);
            int size = tuple.size();
            tuple = new Tuple(size);
            tuple.setHdr((short) len_in1, in1, t1_str_sizes);
            RID rid = new RID();
            rid = fileScan.getRID();
            RID prevRID = new RID();
            prevRID = rid;
            tuple = fileScan.get_next();
            //Inserting data into new Hashfile so as to pass the new hashfile to the block nested loop algorithm
            while (tuple != null && ridDictSize> 0) {
                if (rid != null){
                    String ridToString = rid.pageNo+":"+rid.slotNo;
                    if(ridDict.containsKey(ridToString)){
                        Tuple newTuple = new Tuple(tuple);
                        heap.insertRecord(newTuple.returnTupleByteArray());
                        ridDict.remove(ridToString);
                        ridDictSize--;
                    }
                    prevRID = rid;
                    rid = fileScan.getRID();
                    tuple = fileScan.get_next();
                }
                else {
                    PageId pageId = new PageId(prevRID.pageNo.pid+1);
                    rid = new RID(pageId,0);
                    if(tuple==null)
                    {
                        tuple = fileScan.get_next();
                    }
                }
            }
            // Calling the nested loop function with heapfile with only skyline candidates
            NestedLoopSkyline ns = new NestedLoopSkyline();
            result = ns.NestedLoopsSky(in1, len_in1, t1_str_sizes, fileScan, "skylineCandidates", pref_list, pref_list_length, n_pages);
            fileScan.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return result;

    }
}
