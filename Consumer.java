
/**
 * Abstract class Consumer - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Consumer extends Animal
{
    public Consumer(boolean female, Field field, Location location)
    {
        super(female, field, location);
    }
    
    protected abstract Location findFood();
    
    protected void eaten(){
        super.setDead();
    }
}
