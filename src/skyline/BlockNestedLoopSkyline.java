package skyline;
import bufmgr.BufMgr;
import global.*;
import heap.*;
import iterator.*;
import java.util.ArrayList;
public class BlockNestedLoopSkyline {
    private Heapfile hf;
    private Scan sc;
    FileScan fs = null;
    //for window
    private Heapfile overflow=null;
    ArrayList<Tuple> window = new ArrayList<Tuple>();
    ArrayList<Tuple> result = new ArrayList<Tuple>();
    public ArrayList<Tuple> BlockNestedLoopsSky(AttrType[] in1, int len_in1, short[] t1_str_sizes,
                                                Iterator am1, java.lang.String
                                                        relationName, int[] pref_list, int pref_list_length,
                                                int n_pages){
        // System.out.println("******************* block nested skyline  ***************");
        try {
            hf = new Heapfile(relationName);
            FileScan overflow_scan =  null;
            overflow = new Heapfile("overflow1.in");
            int rec_size = in1.length;
            FldSpec[] projlist = new FldSpec[rec_size];
            RelSpec rel = new RelSpec(RelSpec.outer);
            for (int i = 0; i < rec_size; i++) {
                projlist[i] = new FldSpec(rel, i + 1);
            }
            short[] Ssizes = new short[rec_size];
            fs = new FileScan(relationName, in1, Ssizes, (short) rec_size, rec_size, projlist, null);
            int total_space_buffer = (n_pages-3) * GlobalConst.MINIBASE_PAGESIZE;
            Tuple first = new Tuple();
            RID rid;
            first = fs.get_next();
            short size_of_tuple = first.size();
            int max_no_of_recs_in_window = total_space_buffer/size_of_tuple;
            window.add(new Tuple(first));
            int i=1;
            while (true){
                Tuple t = new Tuple();
                t = fs.get_next();
                if (t == null) break;
                Boolean dominated = true; // To Check if Tuple Oi is not being dominated by all window tuples.
                if(window.isEmpty()){
                    window.add(new Tuple(t));
                    i++;
                }
                java.util.Iterator it = window.iterator();
                while(it.hasNext()) {
                    Tuple win = (Tuple) it.next();
                    if(Methods.Dominates(t,in1,win,in1,(short)len_in1,t1_str_sizes,pref_list,pref_list_length)){
                        it.remove();
                        i--;
                    }
                    if(Methods.Dominates(win,in1,t,in1,(short)len_in1,t1_str_sizes,pref_list,pref_list_length)){
                        dominated= false;
                        break;
                    }
                }
                if (dominated) {
                    if (i  > max_no_of_recs_in_window) {
                        Tuple tt = new Tuple(t);
                        rid = overflow.insertRecord(tt.returnTupleByteArray());
                    } else {
                        window.add(new Tuple(t)); // kast element in the data
                        i++;
                    }
                }
            }
            // System.out.println("Overflow Objects ended");
            //Adding vetted values to result
            result.addAll(window);
            window.clear();
            fs.close();
            i=0;
            // generate runs
            int overflow_size = overflow.getRecCnt();
            int modulus = 0;
            //System.out.println("overflow size "+overflow_size);
            //System.out.println("Window recods max in window "+max_no_of_recs_in_window);
            if ((overflow_size % max_no_of_recs_in_window) != 0) {
                modulus = 1;
            }
            int generate_runs = (overflow_size / max_no_of_recs_in_window) + modulus; // these many iterations
            //System.out.println("generate_runs " + generate_runs);
            overflow_scan = new FileScan("overflow1.in", in1, Ssizes, (short) rec_size, rec_size, projlist, null);
            for (int gr = 0; gr < generate_runs; gr++)
            {
                // System.out.println(gr + " generate runs");
                FileScan fs1 = new FileScan(relationName, in1, Ssizes, (short) rec_size, rec_size, projlist, null);
                // copy data from overflow to window
                for(int lim=0; lim<max_no_of_recs_in_window;lim++){
                    Tuple tq = overflow_scan.get_next();
                    if(tq!=null){
                    window.add(new Tuple(tq));
                    i++;}
                }
                Tuple t1 ;
                while((t1= fs1.get_next())!=null){
                java.util.Iterator it = window.iterator();
                while(it.hasNext()) {
                    Tuple win = (Tuple) it.next();
                    if(Methods.Dominates(t1,in1,win,in1,(short)len_in1,t1_str_sizes,pref_list,pref_list_length)){
                        it.remove();
                        i--;
                    }
                    //if(Methods.Dominates(win,in1,t1,in1,(short)len_in1,t1_str_sizes,pref_list,pref_list_length)){
                      //  break;
                    //}
                }
                }
                result.addAll(window);
                window.clear();
                fs1.close();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}