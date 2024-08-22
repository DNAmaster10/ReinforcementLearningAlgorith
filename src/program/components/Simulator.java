package program.components;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Simulator {
    List<World> worlds = new ArrayList<>();
    float averageScore = 0f;

    public Simulator() {
        initialize();
    }

    public void iterateOnce() {
        //Mutates will take place at the start of the iteration.

        //Remove top 30%
        List<World> nextGeneration = new ArrayList<>();
        int topAmount = (int) (worlds.size() * 0.3);
        for (int i = 0; i < topAmount; i++) {
            nextGeneration.add(worlds.getFirst());
            worlds.removeFirst();
        }

        //Randomly mutate the remaining ones
        for (World world : worlds) {
            world.mutateNetwork();
        }

        nextGeneration.addAll(worlds);
        worlds = nextGeneration;

        //Next, run the simulations for 1000 ticks each
        for (World world : worlds) {
            for (int tick = 0; tick < 1000; tick++) {
                world.tick();
            }
        }

        //Sort worlds by their score in descending order
        worlds.sort(Comparator.comparing(World::getScore).reversed());
        //Calculate average score

        //Reset all worlds and calculate average score
        averageScore = 0f;
        for (World world : worlds) {
            averageScore += world.getScore();
            world.reset();
        }
        averageScore = averageScore / worlds.size();
    }

    public void initialize() {
        worlds.clear();
        for (int i = 0; i < 1000; i++) {
            worlds.add(new World());
        }
    }

    public World getBestWorld() {
        return worlds.getFirst();
    }

    public World getWorstWorld() {
        return worlds.getLast();
    }

    public float getAverageScore() {
        return averageScore;
    }
}
