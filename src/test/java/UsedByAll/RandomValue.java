package UsedByAll;

import java.util.Random;

/**
 * Created by User on 3/30/2015.
 */
public class RandomValue {
    public static String randomValue(){
        Random random = new Random();
        float f = random.nextFloat();
        f = f * 1000;
        int a = (int)Math.round(f);
        f = (float)a/100;
        return Float.toString(f);
    }
    public static int randomValue(int range){
        return new Random().nextInt(range);
    }
}
