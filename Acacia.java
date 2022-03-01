import java.util.List;
import java.util.Random;

/**
 * This is class of Acacia which is a plant
 * They are eaten by giraffes
 *
 * @author Reibjok Othow and Kwan Yui Chiu
 * @version 27/02/2022
 */
public class Acacia extends Plant
{
    // instance variables - replace the example below with your own
    private static final int GROWTH_RATE = 1;
    private static final int MAX_AGE = 10;
    private static final double ACACIA_GROWTH_PROBABILITY = 0.10;
    private static final Random rand = Randomizer.getRandom();
    //the age of the acacia
    private int age;
    /**
     * Constructor for objects of class Acacia
     */
    public Acacia(Field field, Location location)
    {
        super(field, location);
        age = 0;
    }
    
    /**
     * This is what the Acacioa does most of the time: it grows
     * or die of old age.
     * @param field The field currently occupied.
     * @param newPlants A list to return newly born acacia.
     */
    public void act(List<Entity> newPlants){
        incrementAge();
        
        if(isAlive()) {
            grow(newPlants);            
        }
    }
    
    /**
     * This method increments the age of the acacia 
     * it also sets it dead if it ages
     */
    private void incrementAge(){
        this.age++;
        if(this.age > MAX_AGE) {
            super.setDead();
        }
    }
    
    /**
     * This method returns the number of acacia that the acacia should breed
     * The acacia breed faster when its raining
     * @return int the number of acacia to be bred
     */
    private int breed(){
        int total = 0;
        double growthBonusProbability = ACACIA_GROWTH_PROBABILITY;
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
     * This method is called twhen the acacia should grow
     * @param a list of newPlants
     */
    protected void grow(List<Entity> newAcacia){
        // New rabbits are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Plant young = new Acacia(field, loc);
            newAcacia.add(young);
        }
    }
}
