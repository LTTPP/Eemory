package com.prairie.eevernote.util;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

public class ImageUtil {

    private static Robot robot;

    public static BufferedImage mask(final BufferedImage image, final float scaleFactor) {
        RescaleOp ro = new RescaleOp(scaleFactor, ConstantsUtil.ZERO, null);
        BufferedImage rescaledScreenshot = ro.filter(image, null);
        return rescaledScreenshot;
    }

    public static BufferedImage captureScreen(final Rectangle screenRect) throws HeadlessException, AWTException {
        if (robot == null) {
            robot = new Robot();
        }
        return robot.createScreenCapture(screenRect);
    }

}
