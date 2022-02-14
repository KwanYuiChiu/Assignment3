
/**
 * Abstract class ApexPredator - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class ApexPredator extends Animal
{
    public ApexPredator(boolean female, Field field, Location location)
    {
        super(female, field, location);
    }
    
    protected abstract Location findFood();
}
