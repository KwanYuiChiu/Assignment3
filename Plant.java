import java.util.List;

/**
 * Abstract class Plant - write a description of the class here
 *
 * @author (your name here)
 * @version (version number or date here)
 */
public abstract class Plant extends Entity
{
    public Plant(Field field, Location location){
        super(field, location);
    }
    
    protected void eaten(){
        super.setDead();
    }
    
    protected abstract void grow(List<Entity> newPlants);
}
