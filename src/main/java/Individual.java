import java.util.Random;
import lombok.*;

@Data
public class Individual {

    private int id;
    private int []cromosomas;
    private double objFunction;
    private double fitness;
    private double  probability;
    private double cumulativeProb;
    private int crossoverState; // 0 not selected | 1 waiting for crossover | 2 crossover
    @Getter(AccessLevel.NONE) @Setter(AccessLevel.NONE)
    private static Random generator = new Random();

    public Individual(int chromosome_size){
        this.cromosomas = new int[chromosome_size];
        for (int i = 0; i < chromosome_size; i++) {
            this.cromosomas[i] = generator.nextInt(31);
        }
    }

    //Copy constructor
    public Individual(Individual from){
        this.cromosomas = new int[from.cromosomas.length];
        this.id = from.id;
        for (int i = 0; i < from.cromosomas.length; i++) {
            this.cromosomas[i] = from.cromosomas[i];
        }
    }

    public void crossover(Individual c1, int cutPoint){
        switch (cutPoint){
            case 1:
                this.cromosomas[1] = c1.getCromosomas()[1];
                this.cromosomas[2] = c1.getCromosomas()[2];
                this.cromosomas[3] = c1.getCromosomas()[3];
                break;
            case 2:
                this.cromosomas[2] = c1.getCromosomas()[2];
                this.cromosomas[3] = c1.getCromosomas()[3];
                break;
            case 3:
                this.cromosomas[3] = c1.getCromosomas()[3];
                break;
            case 0:
                break;
        }
    }

    public void objectiveFunction(){
        if(this.cromosomas.length > 0){
            this.objFunction = Math.abs((this.cromosomas[0] + (2*this.cromosomas[1]) + (3*this.cromosomas[2]) + (4*this.cromosomas[3])) - 30);
        }
    }

    public void fitness(){
        this.fitness = 1 / (1 + this.objFunction);
    }

}
