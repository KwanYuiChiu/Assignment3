
/**
 * Abstract class ApexPredator
 * This is a class that represents an animal that is at the top off the food chain
 *
 * @author Reibjok Othow and Kwan Yui Chiu
 * @version 27/02/2022
 */
public abstract class ApexPredator extends Animal
{
    public ApexPredator(boolean female, Field field, Location location)
    {
        super(female, field, location);
    }
    
    /**
     * This abstract method is called when the apex predator should find food
     */
    protected abstract Location findFood();
}
