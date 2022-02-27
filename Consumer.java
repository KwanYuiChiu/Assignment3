
/**
 * Abstract class Consumer 
 * This is a class that represents an consumer in the food chain ie an animal 
 * is both eaten and can eat another animal
 *
 * @author Reibjok Othow and Kwan Yui Chui
 * @version 27/02/2022
 */
public abstract class Consumer extends Animal
{
    public Consumer(boolean female, Field field, Location location)
    {
        super(female, field, location);
    }
    
    /**
     * This method is called when the consumer should find food if it is able to
     */
    protected abstract Location findFood();
    
    /**
     * This method sets the consumer dead when it is eaten
     */
    protected void eaten(){
        super.setDead();
    }
}
