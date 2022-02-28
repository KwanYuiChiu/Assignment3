import java.util.List;
import java.util.Random;

/**
 * This is a class of Grass which is a plant 
 * It is eaten by rabbits and rats
 *
 * @author Reibjok Othow and Kwan Yui Chiu
 * @version 27/02/2022
 */
public class Grass extends Plant
{
    // instance variables - replace the example below with your own
    private static final int GROWTH_RATE = 1;
    private static final int MAX_AGE = 10;
    private static final double GRASS_GROWTH_PROBABILITY = 0.10;
    private static final Random rand = Randomizer.getRandom();
    // the age of the plant
    private int age;
    /**
     * Constructor for objects of class Grass
     */
    public Grass(Field field, Location location)
    {
        super(field, location);
        age = 0;
    }
    
    /**
     * This is what the Grass does most of the time: it grows
     * or die of old age.
     * @param field The field currently occupied.
     * @param newPlants A list to return newly born grass.
     */
    public void act(List<Entity> newPlants){
        incrementAge();
        
        if(isAlive()) {
            grow(newPlants);            
        }
    }
    
    /**
     * This method increments the age of the grass
     * It also sets the grass dead when it ages
     */
    private void incrementAge(){
        this.age++;
        if(this.age > MAX_AGE) {
            super.setDead();
        }
    }
    
    /**
     * This method returns the number of acacia that the acacia should breed
     * The grass breed better when it rains
     * @return int the number of grass to be bred
     */
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
    
    /**
     * This method is called twhen the grass should grow
     * @param a list of newPlants
     */
    protected void grow(List<Entity> newGrass){
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Grass(field, loc);
            newGrass.add(young);
        }
    }
}
