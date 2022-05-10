package skyline;
import global.AttrType;
import heap.Tuple;
public class Methods {
   public static boolean Dominates(Tuple t1, AttrType[] type1,
                           Tuple t2,
                           AttrType[] type2,
                           short len_in,
                           short[] str_sizes,
                           int[] pref_list,
                           int pref_list_length)
    {
       boolean result = true;
       for(int i=1;i<=pref_list_length;i++){
          int index = pref_list[i-1];

          int data_type = type1[index].attrType;
          switch(data_type){
             case 0:
            try {
              // for string data
               String value1 = t1.getStrFld(index + 1);
               String value2 = t2.getStrFld(index + 1);
               int res = value1.compareTo(value2);
               if (res <= 0) {
                  result = false;
                  break;
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
             break;
             case 1:
            try {
                // for integer data
               int value1 = t1.getIntFld(index + 1);
               int value2 = t2.getIntFld(index + 1);
               if (value1 <= value2) {
                  result = false;
                  break;
               }
            } catch (Exception e) {
               e.printStackTrace();
            }
             break;
             case 2:
             // for real numbers
            try {
               float value1 = t1.getFloFld(index + 1);
               float value2 = t2.getFloFld(index + 1);
               if (value1 <= value2) {
                  result = false;
                  break;
               }
            } catch (Exception e) {
               System.out.println(e);
            }
             break;
             case 3:
             // for ascii characters
             try {
               char c1 = t1.getCharFld(index);
               char c2 = t2.getCharFld(index);
               if (c1 <= c2) {
                  result = false;
                  break;
               }
            } catch (Exception e) {
                e.printStackTrace();
            }
             break;
          }
   }
      return result;
   }
   
   
   public static int strCount(String str){
      int result = 0;
      for(int i=0;i<str.length();i++){
         result += str.charAt(i);
      }
      return result;
   }
   
   public static int CompareTupleWithTuplePref(Tuple t1,
                                    AttrType[] type1,
                                    Tuple t2,
                                    AttrType[] type2,
                                    short len_in,
                                    short[] str_sizes,
                                    int[] pref_list,
                                    int pref_list_length){
      
      double t1value = 0.0;
      double t2value = 0.0;
      // for (int a : pref_list) {
      //   System.out.println(a);
      // }
      for(int i=1;i<=pref_list_length;i++){
         int index = pref_list[i - 1];

         int data_type = type1[index].attrType;
         switch(data_type){
            case 0:
               try {
               // for string
               String value1 = t1.getStrFld(index + 1);
               String value2 = t2.getStrFld(index + 1);
               t1value += strCount(value1);
               t2value += strCount(value2);
               break;
               }catch(Exception e){
                  e.printStackTrace();
               }
            case 1:
               // for integer
               try {
                  int value1 = t1.getIntFld(index + 1);
                  int value2 = t2.getIntFld(index + 1);
                  t1value += value1;
                  t2value += value2;
                  break;
               } catch (Exception e) {
                  e.printStackTrace();
               }
            case 2:
               // for real numbers
               try {
                  float value1 = t1.getFloFld(index + 1);
                  float value2 = t2.getFloFld(index + 1);
                  t1value += value1;
                  t2value += value2;
                  break;
               } catch (Exception e) {
                  System.out.println(e);
               }
         }
      }
      if(t1value > t2value) return 1;
      if(t1value < t2value) return -1;
      return 0;
   }
}


