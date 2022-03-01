import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A simple model of a snake.
 * Snakes age, move, eat rabbits and mice, and die.
 * 
 * @author David J. Barnes and Michael Kölling and Reibjok Othow and Kwan Yui Chiu
 * @version 27/02/2022
 */
public class Snake extends ApexPredator
{
    // Characteristics shared by all snakes (class variables).
    
    // The age at which a snake can start to breed.
    private static final int BREEDING_AGE = 20;
    // The age to which a snake can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a snake breeding.
    private static final double BREEDING_PROBABILITY = 0.13;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a snake can go before it has to eat again.
    private static final int MOUSE_FOOD_VALUE = 40;
    private static final int RABBIT_FOOD_VALUE = 50;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    // The snake's age.
    private int age;
    // The snake's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a snake. A snake can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the snake will have random age and hunger level.
     * @param female whether or not the snake is female
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Snake(boolean randomAge,boolean female, Field field, Location location)
    {
        super(female, field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(MOUSE_FOOD_VALUE);
        }
        else {
            age = 0;
            foodLevel = MOUSE_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the snake does most of the time: it hunts for
     * rabbits and mice. In the process, it might breed, die of hunger,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newSnakes A list to return newly born snakes.
     */
    public void act(List<Entity> newSnakes)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            if(isFemale()){
                giveBirth(newSnakes);
            }           
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            // See if it was possible to move.
            if(newLocation != null) {
                setLocation(newLocation);
            }
            else {
                // Overcrowding.
                setDead();
            }
        }
    }

    /**
     * Increase the age. This could result in the snake's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Make this snake more hungry. This could result in the snake's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
    
    /**
     * Look for rabbits and mice adjacent to the current location.
     * Only the first live rabbit or mouse is eaten.
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
            if(animal instanceof Mouse) {
                Mouse ratatouille = (Mouse) animal;
                if(ratatouille.isAlive()) { 
                    ratatouille.setDead();
                    foodLevel = MOUSE_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
    
    /**
     * Check whether or not this snake is to give birth at this step.
     * New births will be made into free adjacent locations.
     * They only breed if a male and female snake meet and mate
     * @param newSnakes A list to return newly born snakes.
     */
    private void giveBirth(List<Entity> newSnakes)
    {
        // New snakes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), 2);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            boolean gender = rand.nextBoolean();
            Snake young = new Snake(false,gender,field, loc);
            newSnakes.add(young);
        }
    }
        
    /**
     * This method checks if there is any male snakes nearby 
     * @return boolean there is a male nearby
     */
    private boolean canFindMaleSnake(int distance){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(),distance);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Snake) {
                Snake snake = (Snake) animal;
                if(!snake.isFemale()) { 
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
        if (canFindMaleSnake(2)){
            return births;
        }
        else{
            return 0;
        }
    }

    /**
     * A snake can breed if it has reached the breeding age.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
}
