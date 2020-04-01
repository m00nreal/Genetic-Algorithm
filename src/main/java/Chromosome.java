import java.util.Random;
import lombok.Data;

@Data
public class Chromosome {

    private int number;
    private int a;
    private int b;
    private int c;
    private int d;
    private double objFunction;
    private double fitness;
    private double  probability;
    private double cumulativeProb;
    private boolean selected = false;

    public Chromosome(){
        this.a = new Random().nextInt(30);
        this.b = new Random().nextInt(30);
        this.c = new Random().nextInt(30);
        this.d = new Random().nextInt(30);
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
        this.number = c2.number;
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
        this.objFunction = (this.a + 2*this.b + 3*this.c + 4*this.d) - 30;
    }

    public void fitness(){
        this.fitness = 1 / (1 + this.objFunction);
    }

    public Chromosome(Chromosome c) {
        this.number = c.number;
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
