package skyline;
import btree.*;
import global.AttrType;
import global.GlobalConst;
import global.PageId;
import global.TupleOrder;
import heap.FieldNumberOutOfBoundException;
import heap.Heapfile;
import heap.Tuple;
import iterator.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import global.*;
public class BTreeSortedSky {
    private AttrType[]  _in;
    private int[]       _pref_list;
    private int         _pref_len;
    private int         _len_in;

    float getMax(Tuple arr) throws IOException, FieldNumberOutOfBoundException {
        float ans= (float) 0;
        for(int i=0;i<_len_in;i++){
            for(int j=0;j<_pref_len;j++){
                if(_in[i].attrType==_pref_list[j]){
                    if( arr.getFloFld(i+1)>ans){
                        ans=arr.getFloFld(i+1);
                    }
                }
            }
        }
        return ans;
    }
    float getMin(Tuple arr) throws IOException, FieldNumberOutOfBoundException {
        float ans= (float) 2;
        for(int i=0;i<_len_in;i++){
            for(int j=0;j<_pref_len;j++){
                if(_in[i].attrType==_pref_list[j]){
                    if(arr.getFloFld(i+1)<ans){
                        ans=arr.getFloFld(i+1);
                    }
                }
            }
        }
        return ans;
    }
    float getMaxArray(float[] arr){
        float ans= (float) 0;
        for(int i=0;i<_pref_len;i++){
            if(arr[i]>ans){
                ans=arr[i];
            }
        }
        return ans;
    }
    boolean[] CheckFlag(boolean[] temp_flag, float[] temp_max, float temp_min, int[] arr){
        for(int i=0;i<_pref_len;i++){
            if(temp_min> temp_max[i] || arr[i]<0){
                temp_flag[i]=Boolean.FALSE;
            }
        }
        return temp_flag;
    }
    boolean flagTrue(boolean[] temp_flag){
        for(int i=0;i<_pref_len;i++){
            if(temp_flag[i]==Boolean.TRUE ){
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
    public ArrayList<Tuple> bTreeSortedSky(AttrType[] in1, int len_in1, short[] t1_str_sizes, Iterator am1, String
            relationName, int[] pref_list, int pref_list_length, BTreeFile index_file, int n_pages) throws Exception {
        _in = in1;
        _pref_list=pref_list;
        _pref_len=pref_list_length;
        _len_in= len_in1;
        Heapfile heapfile1 = new Heapfile(relationName);
        ArrayList<Tuple> finalSkyline = new ArrayList<Tuple>();
        boolean[] flag = new boolean[pref_list_length];
        float[] max= new float[pref_list_length];
        float[] min= new float[pref_list_length];
        Tuple top=null;
        KeyDataEntry[] keyDataArray = new KeyDataEntry[pref_list_length];
        BTFileScan scan = new BTFileScan();
        List<List<RID>> ridarray= new ArrayList<>();
        int[] arrayLength= new int[pref_list_length];
        BTreeFile  btfile = index_file;
        int counter1=0;
        for(int i=0;i<pref_list_length;i++){
            List<RID> currRidarray= new ArrayList<RID>();
            scan= btfile.new_scan(new FloatKey((float) pref_list[i]+1+ (float) 0.000000001), new FloatKey((float) (pref_list[i]+2)));
            KeyDataEntry kd;
            while((kd=scan.get_next())!=null){
                LeafData tempData=(LeafData) (kd.data);
                counter1+=1;
                try{
                    currRidarray.add(tempData.getData());
                }
                catch (Exception e) {
                    System.err.println("*** error in getData() ***");
                    // status = FAIL;
                    e.printStackTrace();
                }
            }
            ridarray.add(currRidarray);
            if(ridarray.get(i)!= null){
                arrayLength[i]= ridarray.get(i).size()-1;
            }
            else{
                arrayLength[i]=-1;
            }
        }
        Tuple t11 = new Tuple();
        int rec_size = in1.length;
        t11.setHdr((short)rec_size, in1, t1_str_sizes);
        int size1 = t11.size();
        List<Tuple> top_tuple = new ArrayList<>();
        for (int i = 0; i < pref_list_length; i++) {
            flag[i] = true;
            if(arrayLength[i]>-1){
                t11= heapfile1.getRecord( ridarray.get(0).get(0));
                top = new Tuple(size1);
                top.setHdr((short)rec_size, in1, t1_str_sizes);
                top.tupleCopy(t11);
                arrayLength[i] -=1;
                top_tuple.add(top);
                max[i] = getMax(top_tuple.get(i));
                min[i] = getMin(top_tuple.get(i));
            }
            else{
                Tuple lastElem= new Tuple();
                max[i] = -1;
                min[i] = -1;
            }
        }
        float mn = getMaxArray(max);
        float mx = getMaxArray(min);
        flag= CheckFlag(flag,max,mn,arrayLength);
        ArrayList<float[]> Skyline = new ArrayList<float[]>();
        int counter=0;
        while (flagTrue(flag)){
            ArrayList<Tuple> partition= new ArrayList<Tuple>();
            ArrayList<Tuple> localSkyline = new ArrayList<Tuple>();
            for(int i=0;i<pref_list_length;i++){
                if (max[i]==mx && arrayLength[i]>-1){
                    Tuple temp = new Tuple(size1);
                    temp.setHdr((short)rec_size, in1, t1_str_sizes);
                    temp = top_tuple.get(i);
                    partition.add(new Tuple(top_tuple.get(i)));
                    if(arrayLength[i]<0){
                        continue;
                    }
                    t11=heapfile1.getRecord(ridarray.get(i).get(arrayLength[i]));
                    arrayLength[i]-=1;
                    top.tupleCopy(t11);
                    top_tuple.set(i, top);
                    while(getMax(top_tuple.get(i))==mx && arrayLength[i]>-1){
                        mn = Math.max(mn,getMin(top_tuple.get(i)));
                        partition.add(new Tuple(top_tuple.get(i)));
                        t11=heapfile1.getRecord(ridarray.get(i).get(arrayLength[i]));
                        top.tupleCopy(t11);
                        top_tuple.set(i, top);
                        arrayLength[i]-=1;
                        if(getMax(top_tuple.get(i))==i+1){
                            partition.add(top_tuple.get(i));
                        }
                    }
                    max[i]=getMax(top_tuple.get(i));
                }
            }

            for (int t = 0; t < partition.size(); t++) {
                Tuple q = new Tuple(partition.get(t));
            }
            mx= getMaxArray(max);
            flag= CheckFlag(flag,max,mn,arrayLength);
            counter+=(partition.size());
            ///////Have partition array need to create the heap file
            //
            Heapfile skylineCandidate = new Heapfile("skylineSortCandidate");
            ArrayList<Tuple> tempSkyline = new ArrayList<Tuple>();
            NestedLoopSkyline ns = new NestedLoopSkyline();
            for ( int i = 0; i < partition.size(); i ++){
                Tuple newTuple = new Tuple(partition.get(i));
                skylineCandidate.insertRecord(newTuple.returnTupleByteArray());
            }
            localSkyline = ns.NestedLoopsSky(in1, len_in1, t1_str_sizes, null,"skylineSortCandidate",pref_list,pref_list_length,n_pages);
            skylineCandidate.deleteFile();
            tempSkyline.addAll(localSkyline);
            tempSkyline.addAll(finalSkyline);
            skylineCandidate = new Heapfile("skylineSortCandidate");
            for ( int i = 0; i < tempSkyline.size(); i ++){
                Tuple newTuple = new Tuple(tempSkyline.get(i));
                skylineCandidate.insertRecord(newTuple.returnTupleByteArray());
            }
            finalSkyline.addAll(ns.NestedLoopsSky(in1, len_in1, t1_str_sizes, null,"skylineSortCandidate",pref_list,pref_list_length,n_pages));
            skylineCandidate.deleteFile();
        }
        return  finalSkyline;
    }
}