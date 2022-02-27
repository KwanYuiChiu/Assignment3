import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a Tiger.
 * Tigers age, move, eat consumers, and die.
 * 
 * @author Reibjok Othow and Kwan Yui Chiu
 * @version 27/02/2022)
 */
public class Tiger extends ApexPredator
{
    // Characteristics shared by all Tigers (class variables).
    
    // The age at which a Tiger can start to breed.
    private static final int BREEDING_AGE = 40;
    // The age to which a Tiger can live.
    private static final int MAX_AGE = 150;
    // The likelihood of a Tiger breeding.
    private static final double BREEDING_PROBABILITY = 0.051;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 5;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a Tiger can go before it has to eat again.
    private static final int RABBIT_FOOD_VALUE = 100;
    // number of steps a Tiger can go before it has to eat again.
    private static final int MOUSE_FOOD_VALUE = 100;
    // number of steps a Tiger can go before it has to eat again.
    private static final int FOX_FOOD_VALUE = 100;
    // number of steps a Tiger can go before it has to eat again.
    private static final int GIRAFFE_FOOD_VALUE = 100;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The Tiger's age.
    private int age;
    // The Tiger's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a Tiger. A Tiger can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Tiger will have random age and hunger level.
     * @param female whether or not the tiger is female
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Tiger(boolean randomAge,boolean female, Field field, Location location)
    {
        super(female, field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the Tiger does most of the time: it hunts for
     * consumers. In the process, it might breed, die of hunger,
     * or die of old age.
     * The tigers only hunt for food in the night
     * Only female tiger are able to breed
     * @param field The field currently occupied.
     * @param newTigers A list to return newly born Tigers.
     */
    public void act(List<Entity> newTigers)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if(isFemale()){
                giveBirth(newTigers);
            }          
            // Move towards a source of food if found.
            Location newLocation = null;
            if (!getField().isDay()){
                newLocation = findFood();
            }
            
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null && !getField().isDay()) {
                setLocation(newLocation);
            }
            else if(newLocation == null){
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age. This could result in the Tiger's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this Tiger more hungry. This could result in the Tiger's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for consumers adjacent to the current location.
     * Only the first live consumer is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if(mouse.isAlive()) { 
                    mouse.setDead();
                    foodLevel = MOUSE_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Fox) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) { 
                    fox.setDead();
                    foodLevel = FOX_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Giraffe) {
                Giraffe giraffe = (Giraffe) animal;
                if(giraffe.isAlive()) { 
                    giraffe.setDead();
                    foodLevel = GIRAFFE_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this Tiger is to give birth at this step.
     * Tigers give birth when a male and female tiger meet and mate
     * New births will be made into free adjacent locations.
     * @param newTigers A list to return newly born Tigers.
     */
    private void giveBirth(List<Entity> newTigers)
    {
        // New Tigers are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), 2);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            boolean gender = rand.nextBoolean();
            Tiger young = new Tiger(false,gender,field, loc);
            newTigers.add(young);
        }
    }
        
    /**
     * This method checks if there is any male mouse nearby so 
     * @return boolean there is a male nearby
     */
    private boolean canFindMaleTiger(int distance){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(),distance);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Tiger) {
                Tiger tiger = (Tiger) animal;
                if(!tiger.isFemale()) { 
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        if (canFindMaleTiger(2)){
            return births;
        }
        else{
            return 0;
        }
    }

    /**
     * A Tiger can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
