import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;

/**
 * The GraphView provides a view of eight populations of entities in the field as a line graph
 * over time. In the current version, only eight species can be displayed.
 * 
 * @author Michael Kölling and David J. Barnes and Reibjok Othow and Kwan Yui Chiu
 * @version 28/02/2022
 */
public class GraphView implements View
{
    private static final Color LIGHT_GRAY = new Color(0, 0, 0, 40);

    private static JFrame frame;
    private static GraphPanel graph;
    private static JLabel stepLabel;
    private static JLabel countLabel;

    // The classes being tracked by this view
    private Set<Class<?>> classes;
    // A map for storing colors for participants in the simulation
    private Map<Class<?>, Color> colors;
    // A statistics object computing and storing simulation information
    private FieldStats stats;

    /**
     * Constructor.
     * 
     * @param width The width of the plotter window (in pixels).
     * @param height The height of the plotter window (in pixels).
     * @param startMax The initial maximum value for the y axis.
     */
    public GraphView(int width, int height, int startMax)
    {
        stats = new FieldStats();
        classes = new HashSet<>();
        colors = new HashMap<>();

        if (frame == null) {
            frame = makeFrame(width, height, startMax);
        }
        else {
            graph.newRun();
        }

        //showStatus(0, null);
    }

    /**
     * Define a color to be used for a given class of entity.
     * @param animalClass The animal's Class object.
     * @param color The color to be used for the given class.
     */
    public void setColor(Class<?> animalClass, Color color)
    {
        colors.put(animalClass, color);
        classes = colors.keySet();
    }

    /**
     * Show the current status of the field. The status is shown by displaying a line graph for the
     * eight classes in the field.
     * 
     * @param step Which iteration step it is.
     * @param field The field whose status is to be displayed.
     */
    public void showStatus(int step, Field field)
    {
        graph.update(step, field, stats);
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
     * Prepare for a new run.
     */
    public void reset()
    {
        stats.reset();
        graph.newRun();
    }
    
    /**
     * Prepare the frame for the graph display.
     */
    private JFrame makeFrame(int width, int height, int startMax)
    {
        JFrame frame = new JFrame("Graph View");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        Container contentPane = frame.getContentPane();

        graph = new GraphPanel(width, height, startMax);
        contentPane.add(graph, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(new JLabel("Step:"));
        stepLabel = new JLabel("");
        bottom.add(stepLabel);
        countLabel = new JLabel(" ");
        bottom.add(countLabel);
        contentPane.add(bottom, BorderLayout.SOUTH);

        frame.pack();
        frame.setLocation(20, 600);

        frame.setVisible(true);

        return frame;
    }

    // ============================================================================
    /**
     * Nested class: a component to display the graph.
     */
    class GraphPanel extends JComponent
    {
        private static final double SCALE_FACTOR = 0.8;

        // An internal image buffer that is used for painting. For
        // actual display, this image buffer is then copied to screen.
        private BufferedImage graphImage;
        private int lastVal1, lastVal2, lastVal3, lastVal4, lastVal5, lastVal6, lastVal7, lastVal8;
        private int yMax;

        /**
         * Create a new, empty GraphPanel.
         */
        public GraphPanel(int width, int height, int startMax)
        {
            graphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            clearImage();
            lastVal1 = height;
            lastVal2 = height;
            lastVal3 = height;
            lastVal4 = height;
            lastVal5 = height;
            lastVal6 = height;
            lastVal7 = height;
            lastVal8 = height;
            yMax = startMax;
        }

        /**
         * Indicate a new simulation run on this panel.
         */
        public void newRun()
        {
            int height = graphImage.getHeight();
            int width = graphImage.getWidth();
            
            clearImage();
            Graphics g = graphImage.getGraphics();
            g.copyArea(4, 0, width-4, height, -4, 0);            
            g.setColor(Color.BLACK);
            g.drawLine(width-4, 0, width-4, height);
            g.drawLine(width-2, 0, width-2, height);
            lastVal1 = height;
            lastVal2 = height;
            lastVal3 = height;
            lastVal4 = height;
            lastVal5 = height;
            lastVal6 = height;
            lastVal7 = height;
            lastVal8 = height;
            repaint();
        }

        /**
         * Dispay a new point of data.
         */
        public void update(int step, Field field, FieldStats stats)
        {
            if (classes.size() >= 2) {
                Iterator<Class<?>> it = classes.iterator();
                Class<?> class1 = it.next();
                Class<?> class2 = it.next();
                Class<?> class3 = it.next();
                Class<?> class4 = it.next();
                Class<?> class5 = it.next();
                Class<?> class6 = it.next();
                Class<?> class7 = it.next();
                Class<?> class8 = it.next();

                stats.reset();
                int count1 = stats.getPopulationCount(field, class1);
                int count2 = stats.getPopulationCount(field, class2);
                int count3 = stats.getPopulationCount(field, class3);
                int count4 = stats.getPopulationCount(field, class4);
                int count5 = stats.getPopulationCount(field, class5);
                int count6 = stats.getPopulationCount(field, class6);
                int count7 = stats.getPopulationCount(field, class7);
                int count8 = stats.getPopulationCount(field, class8);

                Graphics g = graphImage.getGraphics();

                int height = graphImage.getHeight();
                int width = graphImage.getWidth();

                // move graph one pixel to left
                g.copyArea(1, 0, width-1, height, -1, 0);

                // calculate y, check whether it's out of screen. scale down if necessary.
                int y = height - ((height * count1) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count1) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class1));
                g.drawLine(width-3, lastVal1, width-2, y);
                lastVal1 = y;

                y = height - ((height * count2) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count2) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class2));
                g.drawLine(width-3, lastVal2, width-2, y);
                lastVal2 = y;
                
                y = height - ((height * count3) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count3) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class3));
                g.drawLine(width-3, lastVal3, width-2, y);
                lastVal3 = y;
                
                y = height - ((height * count4) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count4) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class4));
                g.drawLine(width-3, lastVal4, width-2, y);
                lastVal4 = y;
                
                y = height - ((height * count5) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count5) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class5));
                g.drawLine(width-3, lastVal5, width-2, y);
                lastVal5 = y;
                
                y = height - ((height * count6) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count6) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class6));
                g.drawLine(width-3, lastVal6, width-2, y);
                lastVal6 = y;
                
                y = height - ((height * count7) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count7) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class7));
                g.drawLine(width-3, lastVal7, width-2, y);
                lastVal7 = y;
                
                y = height - ((height * count8) / yMax) - 1;
                while (y<0) {
                    scaleDown();
                    y = height - ((height * count8) / yMax) - 1;
                }
                g.setColor(LIGHT_GRAY);
                g.drawLine(width-2, y, width-2, height);
                g.setColor(colors.get(class8));
                g.drawLine(width-3, lastVal8, width-2, y);
                lastVal8 = y;

                repaint();

                stepLabel.setText("" + step);
                countLabel.setText(stats.getPopulationDetails(field));
            }
        }

        /**
         * Scale the current graph down vertically to make more room at the top.
         */
        public void scaleDown()
        {
            Graphics g = graphImage.getGraphics();
            int height = graphImage.getHeight();
            int width = graphImage.getWidth();

            BufferedImage tmpImage = new BufferedImage(width, (int)(height*SCALE_FACTOR), 
                                                       BufferedImage.TYPE_INT_RGB);
            Graphics2D gtmp = (Graphics2D) tmpImage.getGraphics();

            gtmp.scale(1.0, SCALE_FACTOR);
            gtmp.drawImage(graphImage, 0, 0, null);

            int oldTop = (int) (height * (1.0-SCALE_FACTOR));

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, oldTop);
            g.drawImage(tmpImage, 0, oldTop, null);

            yMax = (int) (yMax / SCALE_FACTOR);
            lastVal1 = oldTop + (int) (lastVal1 * SCALE_FACTOR);
            lastVal2 = oldTop + (int) (lastVal2 * SCALE_FACTOR);
            lastVal3 = oldTop + (int) (lastVal3 * SCALE_FACTOR);
            lastVal4 = oldTop + (int) (lastVal4 * SCALE_FACTOR);
            lastVal5 = oldTop + (int) (lastVal5 * SCALE_FACTOR);
            lastVal6 = oldTop + (int) (lastVal6 * SCALE_FACTOR);
            lastVal7 = oldTop + (int) (lastVal7 * SCALE_FACTOR);
            lastVal8 = oldTop + (int) (lastVal8 * SCALE_FACTOR);

            repaint();
        }

        /**
         * Clear the image on this panel.
         */
        final public void clearImage()
        {
            Graphics g = graphImage.getGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, graphImage.getWidth(), graphImage.getHeight());
            repaint();
        }

        // The following methods are redefinitions of methods
        // inherited from superclasses.

        /**
         * Tell the layout manager how big we would like to be.
         * (This method gets called by layout managers for placing
         * the components.)
         * 
         * @return The preferred dimension for this component.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(graphImage.getWidth(), graphImage.getHeight());
        }

        /**
         * This component is opaque.
         */
        public boolean isOpaque()
        {
            return true;
        }

        /**
         * This component needs to be redisplayed. Copy the internal image 
         * to screen. (This method gets called by the Swing screen painter 
         * every time it want this component displayed.)
         * 
         * @param g The graphics context that can be used to draw on this component.
         */
        public void paintComponent(Graphics g)
        {
            if(graphImage != null) {
                g.drawImage(graphImage, 0, 0, null);
            }
        }
    }
}