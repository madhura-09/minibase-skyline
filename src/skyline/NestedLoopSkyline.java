package skyline;
import global.AttrType;
import global.RID;
import heap.*;
import iterator.*;
import global.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
public class NestedLoopSkyline {
    private  Heapfile hf;
    // private Scan outer;
    private Scan inner;


    public ArrayList<Tuple> NestedLoopsSky(AttrType[] in1, int len_in1, short[] t1_str_sizes,
                                           Iterator am1, java.lang.String
                                                   relationName, int[] pref_list, int pref_list_length,
                                           int n_pages){



        ArrayList<Integer> pruned_new = new ArrayList<Integer>();
        ArrayList<Tuple> skyline = new ArrayList<>();
        //Open HeapFile with Relation name
        try {
            hf = new Heapfile(relationName);
        }catch(Exception e){
            e.printStackTrace();
        }


        FileScan po = null;
        FileScan outer = null;
        FileScan inner = null;

        int rec_size = in1.length;


        FldSpec[] projlist = new FldSpec[rec_size];
        RelSpec rel = new RelSpec(RelSpec.outer);
        for (int i = 0; i < rec_size; i++) {
            projlist[i] = new FldSpec(rel, i + 1);
        }



        short[] Ssizes = new short[rec_size];
        try{

            outer = new FileScan(relationName, in1, Ssizes, (short) rec_size, rec_size, projlist, null);
            po = new FileScan(relationName, in1, Ssizes, (short) rec_size, rec_size, projlist, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // outer = am1;

        Tuple o = new Tuple();
        Tuple i = new Tuple();
        Tuple t = new Tuple();
        short[] sh = new short[rec_size];

        // Tuple setting
        try {
            o.setHdr((short) rec_size, in1, t1_str_sizes);
            i.setHdr((short) rec_size, in1, t1_str_sizes);
            t.setHdr((short) rec_size, in1, t1_str_sizes);

            // t.print();
        } catch (Exception e) {
            e.printStackTrace();
        }

        int size = o.size();


        o = new Tuple(size);
        i = new Tuple(size);
        t = new Tuple(size);
        try {
            o.setHdr((short) rec_size, in1, t1_str_sizes);
            i.setHdr((short) rec_size, in1, t1_str_sizes);
            t.setHdr((short) rec_size, in1, t1_str_sizes);
        }
        catch (Exception e) {
            System.err.println("*** error in Tuple.setHdr() ***");
            e.printStackTrace();
        }


        int q = 0, w = 0,z=0;


        try {
            while ((o = outer.get_next()) != null) {

                q++;
                w = 0;

                if(!pruned_new.contains(q))
                {

                    inner = new FileScan(relationName, in1, Ssizes, (short) rec_size, rec_size, projlist, null);
                    // inner = outer;
                    while((i= inner.get_next()) != null){
                        w++;
                        if(pruned_new.contains(i))continue;
                        else{
                            if(Methods.Dominates(o,in1,i,in1,(short)len_in1,sh,pref_list,pref_list_length)){
                                Tuple qa = i;
                                pruned_new.add(w);
                            }
                        }
                    } // j while end

                }



            } // i while end
        }catch(Exception e) {
            e.printStackTrace();
        }


        try{
            int m = 0;
            while((t = po.get_next()) != null) {
                if (!pruned_new.contains(++m)) {
                    skyline.add(new Tuple(t));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(outer !=null)
                outer.close();
            if(inner != null)
                inner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


        return skyline;
    }
}
