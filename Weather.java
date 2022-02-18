import java.util.Random;

/**
 * This class controls the weather in the field. Weather can only be set
 * to one of three state; raining, foggy or sunny.
 *
 * @author Kwan Yui Chiu
 * @version 19-2-2022
 */
public class Weather
{   
    private static final Random rand = Randomizer.getRandom();
    private static final  int MAX_WEATHER_LENGTH = 9;
    
    private int currentWeatherStep;
    private boolean isRaining;
    private boolean isFoggy;
    private boolean isSunny;
    /**
     * Constructor for objects of class Weather
     */
    public Weather()
    {
        randomiseWeather();
    }
    
    private void randomiseWeather(){
        isRaining = isFoggy = isSunny = false;
        int weather = rand.nextInt(4);
        switch(weather){
            case 1:
                isRaining = true;
                break;
            case 2:
                isFoggy = true;
                break;
            default:
                isSunny = true;
                break;
        }
        // currentWeatherStep cannot be 0
        currentWeatherStep = rand.nextInt(MAX_WEATHER_LENGTH) + 1;
    }
    
    public void update(){
        currentWeatherStep--;
        if (currentWeatherStep == 0){
            randomiseWeather();
        }
    }
    
    public String getDescription(){
        if (isRaining){
            return "raining";
        }else if (isFoggy){
            return "foggy";
        }
        return "sunny";
    }
}
