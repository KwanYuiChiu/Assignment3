import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A simple model of a mouse.
 * Mice age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kölling
 * @version 2016.02.29 (2)
 */
public class Mouse extends Consumer
{
    // Characteristics shared by all mice (class variables).

    // The age at which a mouse can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a mouse can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a mouse breeding.
    private static final double BREEDING_PROBABILITY = 0.16;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 3;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
    private static final int GRASS_FOOD_VALUE = 50;
    // Individual characteristics (instance fields).
    
    // The mouse's age.
    private int age;
    private int foodLevel;
    
    /**
     * Create a new mouse. A mouse may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the mouse will have a random age.
     * @param female whether or not the mouse is female
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Mouse(boolean randomAge,boolean female, Field field, Location location)
    {
        super(female, field, location);
        age = 0;
        this.foodLevel = GRASS_FOOD_VALUE;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
        }
    }
    
    /**
     * This is what the mouse does most of the time - it runs 
     * around and eats grass. Sometimes it will breed or die of old age.
     * @param newMice A list to return newly born mice.
     */
    public void act(List<Entity> newMice)
    {
        incrementAge();
        if(super.isAlive()) {
            if(isFemale()){
               giveBirth(newMice); 
            }        
            // Try to move into a free location.
            Location newLocation = findFood();
            //if  no food is found move to a new location
            if(newLocation ==  null){
                newLocation = getField().freeAdjacentLocation(getLocation());
            }
            //See if it is possible to move
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
     * Increase the age.
     * This could result in the mouse's death.
     */
    private void incrementAge()
    {
        age++;
        if(age > MAX_AGE) {
            setDead();
        }
    }
    
    /**
     * Check whether or not this mouse is to give birth at this step.
     * They breed when a male and female mouse meet and mate
     * New births will be made into free adjacent locations.
     * @param newMice A list to return newly born mice.
     */
    private void giveBirth(List<Entity> newMice)
    {
        // New mice are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation(), 2);
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            boolean gender = rand.nextBoolean();
            Mouse young = new Mouse(false,gender, field, loc);
            newMice.add(young);
        }
    }
        
    /**
     * This method checks if there is any male mouse nearby 
     * @return boolean there is a male nearby
     */
    private boolean canFindMaleMouse(int distance){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation(),distance);
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Mouse) {
                Mouse mouse = (Mouse) animal;
                if(!mouse.isFemale()) { 
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
        if (canFindMaleMouse(2)){
            return births;
        }
        else{
            return 0;
        }
    }

    /**
     * A mouse can breed if it has reached the breeding age.
     * @return true if the mouse can breed, false otherwise.
     */
    private boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }
    
    /**
     * Look for grass adjacent to the current location.
     * Only the first live grass is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    protected Location findFood(){
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object plant = field.getObjectAt(where);
            if(plant instanceof Grass) {
                Grass grass = (Grass) plant;
                if(grass.isAlive()) { 
                    grass.setDead();
                    foodLevel = GRASS_FOOD_VALUE;
                    return where;
                }
            }
        }
        return null;
    }
}
