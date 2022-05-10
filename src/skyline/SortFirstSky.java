package skyline;
import bufmgr.BufMgr;
import global.*;
import heap.*;
import iterator.*;
import java.util.ArrayList;
import skyline.*;

public class SortFirstSky {
	 public ArrayList<Tuple> SortFirstSkyline(AttrType[] in1, int len_in1, short[] t1_str_sizes,
                                        Iterator am1, java.lang.String
                                relationName, int[] pref_list, int pref_list_length,
                                        int n_pages) {
	 	
	 	 BlockNestedLoopSkyline blns = new BlockNestedLoopSkyline();
        ArrayList<Tuple> result = blns.BlockNestedLoopsSky(in1, len_in1, t1_str_sizes, am1, relationName, pref_list, pref_list_length, n_pages);
        return result;

	 }
}
