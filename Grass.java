import java.util.List;
import java.util.Random;

/**
 * Write a description of class Grass here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Grass extends Plant
{
    // instance variables - replace the example below with your own
    private static final int GROWTH_RATE = 1;
    private static final int MAX_AGE = 10;
    private static final double GRASS_GROWTH_PROBABILITY = 0.10;
    private static final Random rand = Randomizer.getRandom();
    
    private int age;
    /**
     * Constructor for objects of class Grass
     */
    public Grass(Field field, Location location)
    {
        super(field, location);
        age = 0;
    }
    
    public void act(List<Entity> newPlants){
        incrementAge();
        
        if(isAlive()) {
            grow(newPlants);            
        }
    }
    
    private void incrementAge(){
        this.age++;
        if(this.age > MAX_AGE) {
            super.setDead();
        }
    }
    private int breed(){
        int total = 0;
        double growthBonusProbability = GRASS_GROWTH_PROBABILITY;
        if(getField().getWeatherCondition().equals("raining")){
            growthBonusProbability += 0.01;
        }
    
        for (int i = 0; i < GROWTH_RATE;i++){
            if (rand.nextDouble() <= growthBonusProbability){
                total++;
            }
        }
        return total;
    }
    protected void grow(List<Entity> newPlants){
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            boolean gender = rand.nextBoolean();
            Plant young = new Grass(field, loc);
            newPlants.add(young);
        }
    }
}
