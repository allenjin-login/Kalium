package org.superredrock.Kalium;

import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ThreadLocalRandom;

public class NameUtils {
    private static final ConcurrentSkipListSet<Integer> existId = new ConcurrentSkipListSet<>();

    public static int getId(){
        int result = ThreadLocalRandom.current().nextInt();
        if (existId.contains(result)){
            return getId();
        }else {
            existId.add(result);
            return result;
        }
    }

    public static void release(int id){
        existId.remove(id);
    }
}
