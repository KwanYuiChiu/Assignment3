import java.util.List;

/**
 * Write a description of class Entity here.
 * An entity is any living organism in the Simulation
 *
 * @author Reibjok Othow and Kwan Yui Chiu
 * @version 27/02/2022
 */
public abstract class Entity
{
    // Whether the entity is alive or not.
    private boolean alive;
    // The entity's field.
    private Field field;
    // The entity's position in the field.
    private Location location;
    
    /**
     * Create a new entity at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Entity(Field field, Location location)
    {
        alive = true;
        this.field = field;
        setLocation(location);
    }

    /**
     * Return the entity's location.
     * @return The entity's location.
     */
    protected Location getLocation()
    {
        return location;
    }
    
    /**
     * Place the entity at the new location in the given field.
     * @param newLocation The entity's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Check whether the entity is alive or not.
     * @return true if the entity is still alive.
     */
    protected boolean isAlive()
    {
        return alive;
    }

    /**
     * Indicate that the entity is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }
    
    /**
     * Return the entity's field.
     * @return The entity's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Make this entity act - that is: make it do
     * whatever it wants/needs to do.
     * @param newentitys A list to receive newly born entitys.
     */
    abstract public void act(List<Entity> newEntities);
}
