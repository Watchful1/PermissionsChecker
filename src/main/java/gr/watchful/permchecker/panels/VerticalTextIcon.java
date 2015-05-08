package gr.watchful.permchecker.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Taken from http://www.jroller.com/santhosh/entry/adobe_like_tabbedpane_in_swing
 */
@SuppressWarnings("deprecation")
public class VerticalTextIcon implements Icon, SwingConstants{
    private Font font = UIManager.getFont("Label.font");
    private FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(font);

    private String text;
    private int width, height;
    private boolean clockwize;

    //added hacky magic numbers to make the tabs labels smaller and keep the text centered
    //will need to fix this up if anything changes with the layout
    public VerticalTextIcon(String text, boolean clockwize){
        this.text = text;
        width = SwingUtilities.computeStringWidth(fm, text)+10;
        height = fm.getHeight()-20;
        this.clockwize = clockwize;
    }

    public void paintIcon(Component c, Graphics g, int x, int y){
        Graphics2D g2 = (Graphics2D)g;
        Font oldFont = g.getFont();
        Color oldColor = g.getColor();
        AffineTransform oldTransform = g2.getTransform();

        g.setFont(font);
        g.setColor(Color.black);
        if(clockwize){
            g2.translate(x+getIconWidth()+8, y+5);
            g2.rotate(Math.PI/2);
        }else{
            g2.translate(x+5, y+getIconHeight()+8);
            g2.rotate(-Math.PI/2);
        }
        g.drawString(text, 0, fm.getLeading()+fm.getAscent());

        g.setFont(oldFont);
        g.setColor(oldColor);
        g2.setTransform(oldTransform);
    }

    public int getIconWidth(){
        return height;
    }

    public int getIconHeight(){
        return width;
    }

    public static void addTab(JTabbedPane tabPane, String text, Component comp){
        int tabPlacement = tabPane.getTabPlacement();
        switch(tabPlacement){
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                tabPane.addTab(null, new VerticalTextIcon(text, tabPlacement==JTabbedPane.RIGHT), comp);
                return;
            default:
                tabPane.addTab(text, null, comp);
        }
    }

    public static JTabbedPane createTabbedPane(int tabPlacement){
        switch(tabPlacement){
            case JTabbedPane.LEFT:
            case JTabbedPane.RIGHT:
                Object textIconGap = UIManager.get("TabbedPane.textIconGap");
                Insets tabInsets = UIManager.getInsets("TabbedPane.tabInsets");
                UIManager.put("TabbedPane.textIconGap", new Integer(1));
                UIManager.put("TabbedPane.tabInsets", new Insets(tabInsets.left, tabInsets.top, tabInsets.right, tabInsets.bottom));
                JTabbedPane tabPane = new JTabbedPane(tabPlacement);
                UIManager.put("TabbedPane.textIconGap", textIconGap);
                UIManager.put("TabbedPane.tabInsets", tabInsets);
                return tabPane;
            default:
                return new JTabbedPane(tabPlacement);
        }
    }
}