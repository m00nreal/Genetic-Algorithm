import java.lang.reflect.Array;
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
    public static int       GENERATIONS         = 100;       // Number of generations
    public static int       POPULATION          = 10;       // Size of population
    public static int       CHROMOSOMES_SIZE    = 10;       // Number of chromosomes each individual has
    public static double    CROSSOVER_RATE      = 0.25;     // Crossover ratio
    public static double    MUTATION_RATE       = 0.10;     // Mutation ratio
    public static boolean   VERBOSE             = false;    // Whether show verbose output
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
    public static Individual[] population = new Individual[POPULATION];
    // Total FITNESS, for now this variable is static. It may change later.
    public static double TOTAL              = 0;
    // Total genes.
    public static int   TOTAL_GENES         = POPULATION * CHROMOSOMES_SIZE;
    // Number of total mutations that will occur
    public static int   TOTAL_MUTATIONS     = 2;//Math.round((MUTATION_RATE * TOTAL_GENES));
    //Generator
    private static Random generator = new Random();
    //Colors for printing at stdout
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void main(String[] args) {

        // Initialize the chromosomes and obtain total. We must do this only once
        for (int i = 0; i < POPULATION; i++) {
            population[i] = new Individual(CHROMOSOMES_SIZE);
            population[i].setId(i+1);
        }

        for(int z = 1; z <= GENERATIONS; z++)
        {
            TOTAL = 0;
            //Get the objective function for each of our elements
            objectiveFunction(population);
            // total sum fitness of our population
            TOTAL = fitness(population);
            if(VERBOSE){
                System.out.println("==========INITIAL POPULATION WITH FITNESS================");
                for (Individual i: population){
                    System.out.println(i);
                }
            }

            // Calculate probability of each chromosome given by fitness[i]/total
            probability(population, TOTAL);
            if (VERBOSE){
                System.out.println("==========INITIAL POPULATION WITH FITNESS AND PROBABILITY================");
                for (Individual i: population){
                    System.out.println(i);
                }
            }

            // Calculate cumulative probability of each chromosome
            cumulativeProbability(population);
            System.out.println("==========INITIAL POPULATION WITH FITNESS, PROBABILITY AND CUMPROB================");
            for (Individual i: population){
                System.out.println(i);
            }


            // We do the roulette wheel selection process to find which individual chromosomes will be replacing each other chromosomes.
            System.out.println(ANSI_GREEN+"==========POPULATION AFTER ROULETTE WHEEL SELECTION================");
            rouletteWheel(population);//Individual []individuals = rouletteWheel(initialPopulation);
            for (Individual i: population){
                System.out.println(i);
            }

            // We obtain our individuals who met the conditions to go through crossover
            System.out.println(ANSI_BLUE+"==========INDIVIDUALS SELECTED FOR CROSSOVER================");
            selectCrossoverIndividuals(population);
            for (Individual i:population) {
                System.out.println(i);
            }


            //After crossover of chromosomes, if we print the result we can verify that our chromosomed has been merged with each other.
            System.out.println("..........................................................");
            crossover(population);
            for (Individual i:population) {
                System.out.println(i);
            }


            System.out.println(ANSI_RED+"==========================MUTATION==========================");
            mutation(population);
            System.out.println(".....................POPULATION AFTER MUTATION.......................");
            for (Individual i: population) {
                System.out.println(ANSI_WHITE+i);
            }

            System.out.println(ANSI_RED+"==========FINAL RESULT OF GENERATION " +z+ "================");
            for (Individual i: population) {
                i.objectiveFunction();
                System.out.printf("%sIndividual %d\t|\tObjective function = %.2f\t|\tChromosomes %s\n", ANSI_YELLOW, i.getId(), i.getObjFunction(), Arrays.toString(i.getCromosomas()));
            }
            System.out.println(ANSI_RESET);
            System.gc();
        }


    }

    public static double[] generateRandomNumbers(int size) {
        double []rdm = new double[size];
        for (int i = 0; i < size; i++) {
            rdm[i] = generator.nextDouble();
        }
        return rdm;
    }

    public static void objectiveFunction(Individual[]c){
        for (Individual cr:c) {
            cr.objectiveFunction();
        }
    }

    public static double fitness(Individual[]c){
        double total = 0;
        for (Individual cr:c) {
            cr.fitness();
            total += cr.getFitness();
        }
        return total;
    }

    public static void probability(Individual[]c, double total){
        for (Individual cr:c) {
            cr.setProbability(cr.getFitness() / total);
        }
    }

    public static void cumulativeProbability(Individual[] ids) {
        for (int i = 0; i < ids.length; i++) {
            if (i == 0){
                ids[i].setCumulativeProb(ids[i].getProbability());
            }
            else{
                double cumProb = ids[i-1].getCumulativeProb() + ids[i].getProbability(); // (cumulative) Probability of previous chromosome + current chromosome probability
                ids[i].setCumulativeProb(cumProb);
            }
        }
    }

    // Here we verify whether the random number generated is below our cumulative probability.
    // If the condition is rejected for the current chromosome being tested, it will iterate until cumulativeProb > randomNumber[i]
    // When this condition is met, we'll overwrite our chromosome[i] values with chromosome[j] values and store it in a new array.
    public static void rouletteWheel(Individual[] ids){
        // Generating random numbers for roulette wheel selection process
        double []rdm = new double[POPULATION];//= generateRandomNumbers(POPULATION); //Uncomment this to run with new values.
        for (int i = 0; i < POPULATION; i++) {
            rdm[i] = generator.nextDouble() % 1.0;
        }

        int [][] values = new int[POPULATION][CHROMOSOMES_SIZE];
        for(int i = 0; i < POPULATION; i++){
            values[i] = ids[i].getCromosomas();
        }
        for (int i = 0; i < POPULATION; i++) { //for each element of our population
            for (int j = 0; j < POPULATION; j++) { // we will obtain the cumulative probability, if it is higher than the random number, we select this chromosome as parent
                if(ids[j].getCumulativeProb() > rdm[i]){ //if(ids[j].getCumulativeProb() > rdms[i]){
                    if(VERBOSE) System.out.println("Individual " + ids[i].getId() + " parent is individual " + ids[j].getId() + " | probability = " + rdm[i]);
//                    cs2[i] = new Individual(ids[j], ids[i]);
                    ids[i].setCromosomas(values[j]);
                    break;
                }
                if(j == POPULATION){
                    j = 0;
                }
            }
        }
    }

    //We will now select our parents for crossover
    //We need to generate a set of randoms[POPULATION] and compare them to the crossover rate
    //If the random number at ith position is below crossover rate, it will be selected.
    public static void selectCrossoverIndividuals(Individual[]ids){
        double []nextRandoms = generateRandomNumbers(POPULATION); //rdms2;
        int exists = Arrays.binarySearch(nextRandoms, MUTATION_RATE);
        while(exists == -1){
            nextRandoms = generateRandomNumbers(POPULATION);
            System.out.println("Generating new randoms set; no number satisfied the constraint");
            exists = Arrays.binarySearch(nextRandoms, MUTATION_RATE);
        }
        for (double nextRandom:nextRandoms) {
            System.out.printf(ANSI_BLUE+"random: %.4f |  ", nextRandom);
        }

        System.out.println();
        for (int i = 0; i < POPULATION; i++){
            if(nextRandoms[i] < CROSSOVER_RATE){
                if(VERBOSE) System.out.println(nextRandoms[i] + " is lower than " + CROSSOVER_RATE + ", Individual " + ids[i].getId() + " is selected for crossover");
                ids[i].setCrossoverState(1);
            }
        }
        System.out.println("...............INDIVIDUALS BEFORE CROSSOVER.................");
    }

    public static void crossover(Individual[]population){
        //To determine the crossover point, we need to generate random numbers between 1 and the length of a chromosome-1, so in this case
        //it would be between 1-3 due we have four fields (a, b, c, d).
        int []crossoverPoint = new int[population.length];
        for (int i = 0; i < crossoverPoint.length; i++) {
            crossoverPoint[i] = 1 + generator.nextInt(1 + (CHROMOSOMES_SIZE-1));
        }

        // Once we have the random values for our crossover points, we need to merge the values between chromosomes that were selected to crossover
        // So for example, if our random generator gave us a number 2 for our first chromosome, we'll take the first two values of our chromosome1
        // and the resting values from our chromosome.

        //TODO
        // FIX THIS UGLY CODE
        // But first, we will need to create a copy of our values since we will be modifying them. we also want to know how many items we have selected
        // for crossover, so we're going to take advantage of this loop to get this number.
        ArrayList<Individual> auxArray = new ArrayList<>();
        ArrayList<Individual> auxArray2 = new ArrayList<>();
        int TOTAL_CROSSOVERS = 0;
        for (int i = 0; i < POPULATION; i++) {
            if(population[i].getCrossoverState() == 1){
                population[i].setCrossoverState(0);
                auxArray.add(new Individual(population[i]));
                auxArray2.add(new Individual(population[i]));
                TOTAL_CROSSOVERS++;
            }
        }

        if(TOTAL_CROSSOVERS > 1){
            for (int i = 0; i < TOTAL_CROSSOVERS-1; i++) {
                auxArray.get(i).crossover(auxArray2.get(i+1), crossoverPoint[i]);
                System.out.printf("Individual %d is going to crossover with Individual %d at cut-point %d\n", auxArray.get(i).getId(), auxArray2.get(i+1).getId(), crossoverPoint[i]);
            }
            System.out.printf("Individual %d is going to crossover with Individual %d at cut-point %d\n", auxArray.get(auxArray.size()-1).getId(), auxArray2.get(0).getId(), crossoverPoint[crossoverPoint.length-1]);
            auxArray.get(auxArray.size()-1).crossover(auxArray2.get(0), crossoverPoint[crossoverPoint.length-1]);

            for (int i = 0; i < POPULATION; i++) {
                if(!auxArray.isEmpty() && auxArray.get(0).getId() == population[i].getId()){
                    population[i].setCromosomas(auxArray.remove(0).getCromosomas());
                }
            }
        }
        System.out.println("..........................................................");
    }

    public static void mutation(Individual[]individuals){
        //So for our last step we need to generate random numbers (again) but in the range of the total genes available. We get this number
        //by doing CHROMOSOMESxPOPULATION, so in this example our chromosomes has 4 genes and we have a initial population of 6 ie random numbers
        // between 24.
        int []randomGenMutation = new int[TOTAL_MUTATIONS]; //{12, 18};
        int []newGenValue       = new int[TOTAL_MUTATIONS]; //{2, 5};

        for(int i = 0; i < TOTAL_MUTATIONS; i++)
        {
            randomGenMutation[i] = generator.nextInt(1 + TOTAL_GENES);
            newGenValue[i] = 1 + generator.nextInt(30);
        }

        int []linearMatrix = new int[TOTAL_GENES];
        int count = 0;
        while(count < TOTAL_GENES) {
            for (int i = 0; i < POPULATION; i++) {
                for (int j = 0; j < CHROMOSOMES_SIZE; j++) {
                    linearMatrix[count] = individuals[i].getCromosomas()[j];
                    count++;
                }
            }
        }

        System.out.println("Before mutation\t|\t" + Arrays.toString(linearMatrix));
        for (int i = 0; i < TOTAL_MUTATIONS; i++) {
            System.out.printf("Chromosome at place %d will be replaced with new value %d\n", randomGenMutation[i], newGenValue[i]);
            if(randomGenMutation[i] > 0)
                linearMatrix[randomGenMutation[i]-1] = newGenValue[i];
            else
                linearMatrix[randomGenMutation[i]] = newGenValue[i];
        }
        System.out.println("After mutation\t|\t" + Arrays.toString(linearMatrix));

        //We return the new values to each individual's chromosome.
        int []values;
        count = 0;
        while(count < TOTAL_GENES){
            for(int i = 0; i < POPULATION; i++){
                values = new int[CHROMOSOMES_SIZE];
                for (int j = 0; j < CHROMOSOMES_SIZE; j++) {
                    values[j] = linearMatrix[count];
                    count++;
                    if(values.length == CHROMOSOMES_SIZE){
                        individuals[i].setCromosomas(values);
                    }
                }
            }
        }
    }
 }
