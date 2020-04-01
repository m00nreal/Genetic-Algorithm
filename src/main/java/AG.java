import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class AG {

    /*                               SELECTION PROCESS
    **     ___  ___  _   _ _    ___ _____ _____ _____ ___  __      ___  _ ___ ___ _
    **    | _ \/ _ \| | | | |  | __|_   _|_   _|_   _| __| \ \    / / || | __| __| |
    **   |   / (_) | |_| | |__| _|  | |   | |   | | | _|   \ \/\/ /| __ | _|| _|| |__
    **  |_|_\\___/ \___/|____|___| |_|   |_|   |_| |___|   \_/\_/ |_||_|___|___|____|
    **
     */

    //=============CONFIGURATION=================
    public static int GENERATION            = 1;        // Number of generations
    public static int POPULATION            = 6;        // Size of population
    public static double CROSSOVER_RATE     = 0.25;     // Crossover ratio
    public static double MUTATION_RATE      = 0.10;     // Mutation ratio
    //=============EXAMPLE DATA==================
    public static int [][]data = {
            {12, 5, 23, 8},
            {2, 21, 18, 3},
            {10, 4, 13, 14},
            {20, 1, 10, 6},
            {1, 4, 13, 19},
            {20, 5, 17, 1}
    };
    public static double []rdms     = {0.201, 0.284, 0.099, 0.822, 0.398, 0.501};
    public static double []rdms2    = {0.191, 0.259, 0.76, 0.006, 0.159, 0.34};
    public static int [] cutPoints  = {1, 1, 2};

    //===============STATIC VARIABLES============================

    // Creates a population of individuals with their chromosomes.
    public static Chromosome[] cs = new Chromosome[POPULATION];
    // Total FITNESS, for now this variable is static. It may change later.
    public static double TOTAL              = 0;
    // Total genes.
    public static int   TOTAL_GENES         = POPULATION * 4;
    // Number of total mutations that will occur
    public static int   TOTAL_MUTATIONS     = (int)MUTATION_RATE * TOTAL_GENES;

    public static void main(String[] args) {

        // Initialize the chromosomes and obtain total
        for (int i = 0; i < POPULATION; i++) {
            cs[i] = new Chromosome(data[i][0], data[i][1], data[i][2], data[i][3]);  // new Chromosome();
            cs[i].setNumber(i+1);
            TOTAL += cs[i].getFitness();
        }

        //Prints the total sum of fitness
//        System.out.println("TOTAL => " + TOTAL);

        // Calculate probability of each chromosome given by fitness[i]/total
        for (Chromosome c: cs) {
            double probability = c.getFitness() / TOTAL;
            c.setProbability(probability);
        }

        // Calculate cumulative probability of each chromosome
        for (int i = 0; i < POPULATION; i++) {
            if (i == 0){
                cs[i].setCumulativeProb(cs[i].getProbability());
            }
            else{
                double cumProb = cs[i-1].getCumulativeProb() + cs[i].getProbability(); // (cumulative) Probability of previous chromosome + current chromosome probability
                cs[i].setCumulativeProb(cumProb);
            }
        }


        // Generating random numbers for roulette wheel selection process
        double []rdm = generateRandomNumbers(POPULATION); //Uncomment this to run with new values.
        for (int i = 0; i < POPULATION; i++) {
            rdm[i] = new Random().nextDouble();
        }

        // Here we verify whether the random number generated is below our cumulative probability.
        // If the condition is rejected for the current chromosome being tested, it will iterate until cumulativeProb > randomNumber[i]
        // When this condition is met, we'll overwrite our chromosome[i] values with chromosome[j] values and store it in a new array.
        Chromosome []cs2 = new Chromosome[POPULATION];
        for (int i = 0; i < POPULATION; i++) {
            for (int j = 0; j < POPULATION; j++) {
                if(cs[j].getCumulativeProb() > rdms[i]){
                    cs2[i] = new Chromosome(cs[j], cs[i]);
                    break;
                }
            }
        }

        // Print chromosomes after roulette wheel selection
        print(cs2, "After roulette wheel selection");


        //We will now select our parents for crossover
        //We need to generate a set of randoms[POPULATION] and compare them to the crossover rate
        //If the random number at ith position is below crossover rate, it will be selected.
        double []nextRandoms = rdms2;//generateRandomNumbers(POPULATION);
        ArrayList<Chromosome> selectedForCrossover = new ArrayList<>();
        for (int i = 0; i < POPULATION; i++){
            if(nextRandoms[i] < CROSSOVER_RATE){
                cs2[i].setSelected(true);
                selectedForCrossover.add(cs2[i]);
            }
        }


        //Now we're going to assing a chromosome to crossover to each of the previous chromosomes selected
        System.out.println("===========SELECTED FOR CROSSOVER=============");
        for(int i = 0; i < selectedForCrossover.size()-1; i++){
            System.out.printf("Chromosome %d is selected to crossover with chromosome %d\n", selectedForCrossover.get(i).getNumber(),
                                                                                             selectedForCrossover.get(i+1).getNumber());
        }
        System.out.printf("Chromosome %d is selected to crossover with chromosome %d\n", selectedForCrossover.get(selectedForCrossover.size()-1).getNumber(),
                                                                                         selectedForCrossover.get(0).getNumber());


        //To determine the crossover point, we need to generate random numbers between 1 and the length of a chromosome-1, so in this case
        //it would be between 1-3 due we have four fields (a, b, c, d).
        int []crossoverPoint = new int[3];
        for (int i = 0; i < crossoverPoint.length; i++) {
            crossoverPoint[i] = new Random().nextInt(1 + crossoverPoint.length);
//            System.out.println(crossoverPoint[i]);
        }

        // Once we have the random values for our crossover points, we need to merge the values between chromosomes that were selected to crossover
        // So for example, if our random generator gave us a number 2 for our first chromosome, we'll take the first two values of our chromosome1
        // and the resting values from our chromosome.
        // But first, we will need to create a copy of our values since we will be modifying them.
        Chromosome []auxArray = copy(selectedForCrossover);
        for(int i = 0; i < selectedForCrossover.size()-1; i++){
            //For each element, we will overwrite new values. Since we know that each element is going to crossover with the next element in the list
            //we can do this in a single loop and make the last crossover (last element with first element) 'manually'
            Chromosome firstChromosome = selectedForCrossover.get(i);
            Chromosome secondChromosome = auxArray[i+1];
            firstChromosome.crossover(secondChromosome, cutPoints[i]);
        }
        selectedForCrossover.get(selectedForCrossover.size()-1).crossover(auxArray[0], cutPoints[cutPoints.length-1]);

        //After crossover of chromosomes, if we print the result we can verify that our chromosomed has been merged with each other.
        print(cs2, "Chromosomes after merging");


        //So for our last step we need to generate random numbers (again) but in the range of the total genes available. We get this number
        //by doing CHROMOSOMESxPOPULATION, so in this example our chromosomes has 4 genes and we have a initial population of 6 ie random numbers
        // between 24.
        int []randomGenMutation = {12, 18}; // new int[TOTAL_MUTATIONS];
        int []newGenValue       = {2, 5};   // new int[TOTAL_MUTATIONS];
//        for(int i = 0; i < TOTAL_MUTATIONS; i++){
//            randomGenMutation[i] = new Random().nextInt(TOTAL_GENES);
//        }

        //Given the values in our arrays, now we need to find the each of the genes that needs to be "mutated" (replaced).
        //We'll try to find the index of each gen in order to perform a slightly faster search than a full iteration over all elements.
        for(int i = 0; i < randomGenMutation.length; i++){
            int chromosomeNeeded = 0;
            if(randomGenMutation[i] > 3){
                if(randomGenMutation[i] % 4 == 0){
                    chromosomeNeeded = (randomGenMutation[i] / 4) - 1;
                }else
                    chromosomeNeeded = randomGenMutation[i] / 4;
            }
            int chromosomeValue = randomGenMutation[i] % 4;
            //Case 0, it means that the value is in the last position, ie attribute 'd' of our chromosome
            //Case 1, it means that the value is in the first position, ie attribute 'a' of our chromosome
            //Case 2, it means that the value is in the second position, ie attribute 'b' of our chromosome
            //Case 3, it means that the value is in the second position, ie attribute 'c' of our chromosome
            switch (chromosomeValue){
                case 0:
                    cs2[chromosomeNeeded].setD(newGenValue[i]);
                    break;
                case 1:
                    cs2[chromosomeNeeded].setA(newGenValue[i]);
                    break;
                case 2:
                    cs2[chromosomeNeeded].setB(newGenValue[i]);
                    break;
                case 3:
                    cs2[chromosomeNeeded].setC(newGenValue[i]);
                    break;
            }
        }
        print(cs2, "FINAL RESULT OF GENERATION" + GENERATION);

    }

    public static double[] generateRandomNumbers(int size){
        double []rdm = new double[size];
        for (int i = 0; i < size; i++) {
            rdm[i] = new Random().nextDouble();
        }
        return rdm;
    }

    //Copy array
    public static Chromosome[] copy(ArrayList<Chromosome>arr){
        Chromosome []Arrnw = new Chromosome[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            Arrnw[i] = new Chromosome(arr.get(i));
        }
        return  Arrnw;
    }

    public static void print(Chromosome[]arr, String message){
        message = message.toUpperCase();
        System.out.println("===========" + message + "=============");
        for (Chromosome c:arr) {
            System.out.println(c);
        }
    }

 }
