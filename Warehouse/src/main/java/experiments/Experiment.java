package experiments;

import DelgMas.AgvExample;

/**
 * @author Matija Kljun
 */
public class Experiment {

    public static void main(String[] args) {

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                AgvExample test = new AgvExample(1, 3*60*60*1000);
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
