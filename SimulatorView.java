import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * A graphical view of the simulation grid.
 * The view displays a colored rectangle for each location 
 * representing its contents. It uses a default background color.
 * Colors for each type of species can be defined using the
 * setColor method.
 * 
 * @author David J. Barnes and Michael Kölling and Reibjok Othow and Kwan Yui Chui
 * @version 27/02/2022
 */
public class SimulatorView extends JFrame implements View
{
    // Colors used for empty locations.
    private static final Color EMPTY_COLOR = Color.white;

    // Color used for objects that have no defined color.
    private static final Color UNKNOWN_COLOR = Color.gray;

    private final String STEP_PREFIX = "Step: ";
    private final String POPULATION_PREFIX = "Population: ";
    private JLabel stepLabel, population, infoLabel;
    
    // Components used for the sidebar
    private final String WEATHER_PREFIX = "Weather: ";
    private final String TIME_PREFIX = "Time: ";
    private JLabel weatherLabel,timeLabel;
    private JButton simulateOneStepBtn,resetBtn, simulateLongRun;
    private FieldView fieldView;
    
    // A map for storing colors for participants in the simulation
    private Map<Class, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;
    private Simulator simulator;
    
    private static Thread longSimThread;
    private boolean threadStarted;
    /**
     * Create a view of the given width and height.
     * @param height The simulation's height.
     * @param width  The simulation's width.
     */
    public SimulatorView(int height, int width, Simulator simulator)
    {
        stats = new FieldStats();
        colors = new LinkedHashMap<>();

        setTitle("Predator/Prey Simulation");
        stepLabel = new JLabel(STEP_PREFIX, JLabel.CENTER);
        infoLabel = new JLabel("  ", JLabel.CENTER);
        population = new JLabel(POPULATION_PREFIX, JLabel.CENTER);
        
        setLocation(100, 50);
        /*
         * Initialisation for the GUI componenets for the sidebar
         */
        weatherLabel = new JLabel(WEATHER_PREFIX + produce20CharacterPadding(""), JLabel.CENTER);
        timeLabel = new JLabel(TIME_PREFIX + produce20CharacterPadding(""), JLabel.CENTER);
        
        threadStarted = false;
        
        simulateOneStepBtn = new JButton("Simulate one step");
        simulateOneStepBtn.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) {
                                   if(threadStarted){
                                       longSimThread.stop();
                                       threadStarted = false;
                                   }
                                   simulator.simulateOneStep(); 
                                }
                           });
        
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(new ActionListener() {
                               public void actionPerformed(ActionEvent e) {
                                   if(threadStarted){
                                       longSimThread.stop();
                                       threadStarted = false;
                                   }
                                   simulator.reset(); 
                                }
                           });
                           
        simulateLongRun = new JButton("Simulate Long Run");
        simulateLongRun.addActionListener(new ActionListener(){
                            public void actionPerformed(ActionEvent e) {
                                longSimThread = new Thread(simulator::runLongSimulation);
                                longSimThread.start();
                                threadStarted =  true;
                            }
        });
        
        setLocation(100, 50);
        
        fieldView = new FieldView(height, width);

        Container contents = getContentPane();
        
        JPanel infoPane = new JPanel(new BorderLayout());
            infoPane.add(stepLabel, BorderLayout.WEST);
            infoPane.add(infoLabel, BorderLayout.CENTER);
        JPanel sideBar = new JPanel(new GridLayout(6,1));
            sideBar.add(weatherLabel);
            sideBar.add(timeLabel);
            sideBar.add(simulateOneStepBtn);
            sideBar.add(resetBtn);
            sideBar.add(simulateLongRun);
            
        sideBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contents.add(infoPane, BorderLayout.NORTH);
        contents.add(sideBar, BorderLayout.WEST);
        contents.add(fieldView, BorderLayout.CENTER);
        contents.add(population, BorderLayout.SOUTH);
        
        pack();
        setVisible(true);
    }
    
    /**
     * A private method to produce a padded string text
     * 
     * @param String the string intended to be displayed
     * @return String padded with space on the right to make it a 20 chars long
     */
    private String produce20CharacterPadding(String s){
        String res = s;
        for (int i = 0; i < 20-s.length(); i++){
            res += " ";
        }
        return res;
    }
    
    /**
     * Define a color to be used for a given class of animal.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class animalClass, Color color)
    {
        colors.put(animalClass, color);
    }

    /**
     * Display a short information label at the top of the window.
     */
    public void setInfoText(String text)
    {
        infoLabel.setText(text);
    }

    /**
     * @return The color to be used for a given class of animal.
     */
    private Color getColor(Class animalClass)
    {
        Color col = colors.get(animalClass);
        if(col == null) {
            // no color defined for this class
            return UNKNOWN_COLOR;
        }
        else {
            return col;
        }
    }

    /**
     * Show the current status of the field.
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        stepLabel.setText(STEP_PREFIX + step);
        stats.reset();
        
        String weatherText = produce20CharacterPadding(WEATHER_PREFIX + field.getWeatherCondition());
        String timeString = produce20CharacterPadding(TIME_PREFIX + field.getTimeString());
        weatherLabel.setText(weatherText);
        timeLabel.setText(timeString);
        fieldView.preparePaint();

        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                Object animal = field.getObjectAt(row, col);
                if(animal != null) {
                    stats.incrementCount(animal.getClass());
                    fieldView.drawMark(col, row, getColor(animal.getClass()));
                }
                else {
                    fieldView.drawMark(col, row, EMPTY_COLOR);
                }
            }
        }
        stats.countFinished();

        population.setText(POPULATION_PREFIX + stats.getPopulationDetails(field));
        fieldView.repaint();
    }

    /**
     * Determine whether the simulation should continue to run.
     * @return true If there is more than one species alive.
     */
    public boolean isViable(Field field)
    {
        return stats.isViable(field);
    }
    
    /**
     * Provide a graphical view of a rectangular field. This is 
     * a nested class (a class defined inside a class) which
     * defines a custom component for the user interface. This
     * component displays the field.
     * This is rather advanced GUI stuff - you can ignore this 
     * for your project if you like.
     */
    private class FieldView extends JPanel
    {
        private final int GRID_VIEW_SCALING_FACTOR = 6;

        private int gridWidth, gridHeight;
        private int xScale, yScale;
        Dimension size;
        private Graphics g;
        private Image fieldImage;

        /**
         * Create a new FieldView component.
         */
        public FieldView(int height, int width)
        {
            gridHeight = height;
            gridWidth = width;
            size = new Dimension(0, 0);
        }

        /**
         * Tell the GUI manager how big we would like to be.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(gridWidth * GRID_VIEW_SCALING_FACTOR,
                                 gridHeight * GRID_VIEW_SCALING_FACTOR);
        }

        /**
         * Prepare for a new round of painting. Since the component
         * may be resized, compute the scaling factor again.
         */
        public void preparePaint()
        {
            if(! size.equals(getSize())) {  // if the size has changed...
                size = getSize();
                fieldImage = fieldView.createImage(size.width, size.height);
                g = fieldImage.getGraphics();

                xScale = size.width / gridWidth;
                if(xScale < 1) {
                    xScale = GRID_VIEW_SCALING_FACTOR;
                }
                yScale = size.height / gridHeight;
                if(yScale < 1) {
                    yScale = GRID_VIEW_SCALING_FACTOR;
                }
            }
        }
        
        /**
         * Paint on grid location on this field in a given color.
         */
        public void drawMark(int x, int y, Color color)
        {
            g.setColor(color);
            g.fillRect(x * xScale, y * yScale, xScale-1, yScale-1);
        }

        /**
         * The field view component needs to be redisplayed. Copy the
         * internal image to screen.
         */
        public void paintComponent(Graphics g)
        {
            if(fieldImage != null) {
                Dimension currentSize = getSize();
                if(size.equals(currentSize)) {
                    g.drawImage(fieldImage, 0, 0, null);
                }
                else {
                    // Rescale the previous image.
                    g.drawImage(fieldImage, 0, 0, currentSize.width, currentSize.height, null);
                }
            }
        }
    }
}
