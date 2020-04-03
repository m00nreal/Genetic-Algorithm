import java.util.Random;

import lombok.*;

@Data
public class Chromosome {

    private int id;
    private int a;
    private int b;
    private int c;
    private int d;
    private double objFunction;
    private double fitness;
    private double  probability;
    private double cumulativeProb;
    private boolean selected = false;
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private static Random generator = new Random();

    public Chromosome(){
        this.a = (int)generator.nextDouble()*30;
        this.b = (int)generator.nextDouble()*30;
        this.c = (int)generator.nextDouble()*30;
        this.d = (int)generator.nextDouble()*30;
        objectiveFunction();
        fitness();
    }

    public Chromosome(int a, int b, int c, int d){
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        objectiveFunction();
        fitness();
    }

    public Chromosome(Chromosome c1, Chromosome c2){
        this.id = c2.id;
        this.a = c1.a;
        this.b = c1.b;
        this.c = c1.c;
        this.d = c1.d;
        this.objFunction = c2.objFunction;
        this.fitness = c2.fitness;
        this.probability = c2.probability;
        this.cumulativeProb = c2.cumulativeProb;
    }

    public void crossover(Chromosome c1, int cutPoint){
        switch (cutPoint){
            case 1:
                this.b = c1.getB();
                this.c = c1.getC();
                this.d = c1.getD();
                break;
            case 2:
                this.c = c1.getC();
                this.d = c1.getD();
                break;
            case 3:
                this.d = c1.getD();
                break;
            case 0:
                break;
        }
    }

    public void objectiveFunction(){
        this.objFunction = Math.abs((this.a + 2*this.b + 3*this.c + 4*this.d) - 30);
    }

    public void fitness(){
        this.fitness = 1 / (1 + this.objFunction);
    }

    public Chromosome(Chromosome c) {
        this.id = c.id;
        this.a = c.a;
        this.b = c.b;
        this.c = c.c;
        this.d = c.d;
        this.objFunction = c.objFunction;
        this.fitness = c.fitness;
        this.probability = c.probability;
        this.cumulativeProb = c.cumulativeProb;
        this.selected = c.selected;
    }
}
