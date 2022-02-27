import java.util.List;

/**
 * Abstract class Plant 
 * This is a class that represents a plant in the simulation
 * Plants can grow at a specific rate and they do not move
 *
 * @author Reibjok Othow and Kwan Yui Chui
 * @version 27/02/2022
 */
public abstract class Plant extends Entity
{
    public Plant(Field field, Location location){
        super(field, location);
    }
    
    /**
     * This method set the plant dead when it is eaten
     */
    protected void eaten(){
        super.setDead();
    }
    
    /**
     * This is an abstract method that is called when the plant should grow and reproduce
     */
    protected abstract void grow(List<Entity> newPlants);
}
