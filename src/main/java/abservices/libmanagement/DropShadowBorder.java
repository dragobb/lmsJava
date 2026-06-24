
package abservices.libmanagement;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/**
 *
 * @author ariel
 */
// Custom DropShadow Border
class DropShadowBorder extends AbstractBorder {
    private final Color shadowColor;
    private final int shadowSize;
    private final float shadowOpacity;
    private final int shadowOffset;
    private final boolean showTopShadow;
    private final boolean showLeftShadow;
    private final boolean showBottomShadow;
    private final boolean showRightShadow;

    public DropShadowBorder(Color shadowColor, int shadowSize, float shadowOpacity, int shadowOffset, 
            boolean showTopShadow, boolean showLeftShadow, boolean showBottomShadow, boolean showRightShadow) {
        this.shadowColor = shadowColor;
        this.shadowSize = shadowSize;
        this.shadowOpacity = shadowOpacity;
        this.shadowOffset = shadowOffset;
        this.showTopShadow = showTopShadow;
        this.showLeftShadow = showLeftShadow;
        this.showBottomShadow = showBottomShadow;
        this.showRightShadow = showRightShadow;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int shadowAlpha = (int)(shadowOpacity * 255);
        
        for (int i = 0; i < shadowSize; i++) {
            int currentAlpha = shadowAlpha * (shadowSize - i) / shadowSize;
            g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), 
                               shadowColor.getBlue(), currentAlpha));
            
            // Bottom shadow
            if (showBottomShadow) {
                g2.fillRect(x + shadowOffset, y + height - i, 
                          width - shadowOffset * 2, 1);
            }
            
            // Right shadow
            if (showRightShadow) {
                g2.fillRect(x + width - i, y + shadowOffset, 
                          1, height - shadowOffset * 2);
            }
            
            // Top shadow
            if (showTopShadow) {
    g2.fillRect(x + shadowOffset, y + i, 
                width - shadowOffset * 2, 1);
}

            
            // Left shadow
            if (showLeftShadow) {
                g2.fillRect(x + i, y + shadowOffset, 
                          1, height - shadowOffset * 2);
            }
        }
        
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int top = showTopShadow ? shadowSize : 0;
        int left = showLeftShadow ? shadowSize : 0;
        int bottom = showBottomShadow ? shadowSize : 0;
        int right = showRightShadow ? shadowSize : 0;
        
        return new Insets(top, left, bottom, right);
    }
    
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.top = showTopShadow ? shadowSize : 0;
        insets.left = showLeftShadow ? shadowSize : 0;
        insets.bottom = showBottomShadow ? shadowSize : 0;
        insets.right = showRightShadow ? shadowSize : 0;
        
        return insets;
    }
}
