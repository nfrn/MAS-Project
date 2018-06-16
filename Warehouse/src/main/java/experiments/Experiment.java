package experiments;

import DelgMas.AgvExample;

/**
 * @author Matija Kljun
 */
public class Experiment {

    public static void main(String[] args) {
        int[] avg_nums = {1,3,6,9,12};
        for (int avg_num : avg_nums) {
            for (int j = 0; j < 5; j++) {
                AgvExample test = new AgvExample(avg_num, 3*60*60*1000);
                test.run(false);
            }
        }

//        try {
//
//        } catch (Exception e) {
//            System.out.println("Error running test");
//        }

    }
}
