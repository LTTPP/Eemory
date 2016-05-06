package com.lttpp.eemory.util;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageUtil {

    private static Robot robot;

    static {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            LogUtil.logError(e);
        }
    }

    public static BufferedImage mask(final BufferedImage image, final float scaleFactor) {
        RescaleOp ro = new RescaleOp(scaleFactor, 0, null);
        BufferedImage rescaledScreenshot = ro.filter(image, null);
        return rescaledScreenshot;
    }

    public static BufferedImage captureScreen(final Rectangle screenRect) throws AWTException {
        if (robot == null) {
            throw new AWTException("the platform configuration does not allow low-level input control");
        }
        return robot.createScreenCapture(screenRect);
    }

}
