package com.prairie.eemory.ui;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.lang3.SystemUtils;

import com.prairie.eemory.Messages;
import com.prairie.eemory.ui.GeomRectangle.Position;
import com.prairie.eemory.util.ColorUtil;
import com.prairie.eemory.util.ImageUtil;
import com.prairie.eemory.util.LogUtil;
import com.prairie.eemory.util.Times;

@SuppressWarnings("serial")
public class CaptureView extends JFrame {

    private final BufferedImage fullScreen;
    private final GeomRectangle rectangle = new GeomRectangle();
    private boolean isCapturing = false;
    private boolean isCaptured = false;
    private boolean isCaptureFullScreenViaClick = false;
    private final Times times = new Times();
    private GeomPoint datumPoint;

    private final static Cursor DRAW_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    private static final float PLUGIN_SCREENSHOT_HINT_SCALEFACTOR = 0.3F;
    private static final float PLUGIN_SCREENSHOT_MASK_FULLSCREEN_SCALEFACTOR = 0.7F;

    public CaptureView() throws HeadlessException, AWTException {

        fullScreen = ImageUtil.captureScreen(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        setSize(Toolkit.getDefaultToolkit().getScreenSize());
        setUndecorated(true);
        setResizable(true); // Fix bug: fullscreen doesn't cover tool bars on Ubuntu.
        setAlwaysOnTop(true);
        setCursor(DRAW_CURSOR);

        resetView();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    rectangle.clear();
                    escape();
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (e.getClickCount() == 1) {
                        if (!isCaptured) {
                            isCaptureFullScreenViaClick = true;
                            rectangle.getStartPoint().setLocation(0, 0);
                            rectangle.getEndPoint().setLocation(new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
                            maskFullScreen(PLUGIN_SCREENSHOT_MASK_FULLSCREEN_SCALEFACTOR);
                            repaint();
                            isCaptured = true;
                        }
                    } else if (e.getClickCount() == 2) {
                        escape();
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) {
                    if (e.getClickCount() == 1) {
                        if (isCaptured) {
                            resetView();
                            isCaptured = false;
                            isCaptureFullScreenViaClick = false;
                            setCursor(DRAW_CURSOR);
                        } else {
                            rectangle.clear();
                            escape();
                        }
                    }
                }
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                if (!isCaptured && e.getButton() == MouseEvent.BUTTON1) {
                    rectangle.getStartPoint().setLocation(e.getX(), e.getY());
                    isCapturing = true;
                    times.resetTimes(1);
                } else if (isResize()) {
                    datumPoint = new GeomPoint(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (isCapturing && e.getButton() == MouseEvent.BUTTON1) {
                    rectangle.getEndPoint().setLocation(e.getX(), e.getY());
                    isCapturing = false;
                    isCaptured = rectangle.getWidth() > 0 && rectangle.getHeight() > 0;
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (isCapturing) {
                    if (times.hasTimes()) {
                        maskFullScreen(PLUGIN_SCREENSHOT_MASK_FULLSCREEN_SCALEFACTOR);
                    }
                    rectangle.getEndPoint().setLocation(e.getX(), e.getY());
                    if (rectangle.isRealRectangle()) {
                        repaint();
                    }
                } else if (isResize()) {
                    if (getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
                        rectangle.resize(Position.EAST, e.getX() - datumPoint.getX(), 0);
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)) {
                        rectangle.resize(Position.SOUTH, 0, e.getY() - datumPoint.getY());
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) {
                        rectangle.resize(Position.WEST, e.getX() - datumPoint.getX(), 0);
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)) {
                        rectangle.resize(Position.NORTH, 0, e.getY() - datumPoint.getY());
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)) {
                        rectangle.resize(Position.NORTHEAST, e.getX() - datumPoint.getX(), e.getY() - datumPoint.getY());
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)) {
                        rectangle.resize(Position.NORTHWEST, e.getX() - datumPoint.getX(), e.getY() - datumPoint.getY());
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)) {
                        rectangle.resize(Position.SOUTHEAST, e.getX() - datumPoint.getX(), e.getY() - datumPoint.getY());
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)) {
                        rectangle.resize(Position.SOUTHWEST, e.getX() - datumPoint.getX(), e.getY() - datumPoint.getY());
                    } else if (getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)) {
                        rectangle.move(e.getX() - datumPoint.getX(), e.getY() - datumPoint.getY());
                    }
                    datumPoint.move(e.getX() - datumPoint.getX(), e.getY() - datumPoint.getY());
                    repaint();
                }
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                if (!isCapturing && !isCaptured) {
                    rectangle.getStartPoint().setLocation(e.getX(), e.getY());
                } else if (isCaptured) {
                    if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.EAST) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.SOUTH) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.WEST) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.NORTH) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.NORTHEAST) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.NORTHWEST) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.SOUTHEAST) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.SOUTHWEST) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
                    } else if (rectangle.positionOf(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.INSIDE) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    } else {
                        setCursor(DRAW_CURSOR);
                    }
                }
            }
        });
    }

    @Override
    public void paint(final Graphics graphics) {
        super.paint(graphics);
        if (isCapturing || isCaptureFullScreenViaClick || isResize()) {
            Image cropedScreenshot = fullScreen.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
            Graphics2D g2 = (Graphics2D) graphics;
            g2.drawImage(cropedScreenshot, rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), null);

            g2.setColor(ColorUtil.AWT_EVERNOTE_GREEN);
            g2.drawRect(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());

            g2.drawRect(rectangle.getTopLeftRectangle().getTopLeftPoint().getX(), rectangle.getTopLeftRectangle().getTopLeftPoint().getY(), rectangle.getTopLeftRectangle().getWidth(), rectangle.getTopLeftRectangle().getHeight());
            g2.fillRect(rectangle.getTopLeftRectangle().getTopLeftPoint().getX(), rectangle.getTopLeftRectangle().getTopLeftPoint().getY(), rectangle.getTopLeftRectangle().getWidth(), rectangle.getTopLeftRectangle().getHeight());

            g2.drawRect(rectangle.getTopRightRectangle().getTopLeftPoint().getX(), rectangle.getTopRightRectangle().getTopLeftPoint().getY(), rectangle.getTopRightRectangle().getWidth(), rectangle.getTopRightRectangle().getHeight());
            g2.fillRect(rectangle.getTopRightRectangle().getTopLeftPoint().getX(), rectangle.getTopRightRectangle().getTopLeftPoint().getY(), rectangle.getTopRightRectangle().getWidth(), rectangle.getTopRightRectangle().getHeight());

            g2.drawRect(rectangle.getBottomLeftRectangle().getTopLeftPoint().getX(), rectangle.getBottomLeftRectangle().getTopLeftPoint().getY(), rectangle.getBottomLeftRectangle().getWidth(), rectangle.getBottomLeftRectangle().getHeight());
            g2.fillRect(rectangle.getBottomLeftRectangle().getTopLeftPoint().getX(), rectangle.getBottomLeftRectangle().getTopLeftPoint().getY(), rectangle.getBottomLeftRectangle().getWidth(), rectangle.getBottomLeftRectangle().getHeight());

            g2.drawRect(rectangle.getBottomRightRectangle().getTopLeftPoint().getX(), rectangle.getBottomRightRectangle().getTopLeftPoint().getY(), rectangle.getBottomRightRectangle().getWidth(), rectangle.getBottomRightRectangle().getHeight());
            g2.fillRect(rectangle.getBottomRightRectangle().getTopLeftPoint().getX(), rectangle.getBottomRightRectangle().getTopLeftPoint().getY(), rectangle.getBottomRightRectangle().getWidth(), rectangle.getBottomRightRectangle().getHeight());

            g2.drawRect(rectangle.getTopRectangle().getTopLeftPoint().getX(), rectangle.getTopRectangle().getTopLeftPoint().getY(), rectangle.getTopRectangle().getWidth(), rectangle.getTopRectangle().getHeight());
            g2.fillRect(rectangle.getTopRectangle().getTopLeftPoint().getX(), rectangle.getTopRectangle().getTopLeftPoint().getY(), rectangle.getTopRectangle().getWidth(), rectangle.getTopLeftRectangle().getHeight());

            g2.drawRect(rectangle.getBottomRectangle().getTopLeftPoint().getX(), rectangle.getBottomRectangle().getTopLeftPoint().getY(), rectangle.getBottomRectangle().getWidth(), rectangle.getBottomRectangle().getHeight());
            g2.fillRect(rectangle.getBottomRectangle().getTopLeftPoint().getX(), rectangle.getBottomRectangle().getTopLeftPoint().getY(), rectangle.getBottomRectangle().getWidth(), rectangle.getBottomRectangle().getHeight());

            g2.drawRect(rectangle.getLeftRectangle().getTopLeftPoint().getX(), rectangle.getLeftRectangle().getTopLeftPoint().getY(), rectangle.getLeftRectangle().getWidth(), rectangle.getLeftRectangle().getHeight());
            g2.fillRect(rectangle.getLeftRectangle().getTopLeftPoint().getX(), rectangle.getLeftRectangle().getTopLeftPoint().getY(), rectangle.getLeftRectangle().getWidth(), rectangle.getLeftRectangle().getHeight());

            g2.drawRect(rectangle.getRightRectangle().getTopLeftPoint().getX(), rectangle.getRightRectangle().getTopLeftPoint().getY(), rectangle.getRightRectangle().getWidth(), rectangle.getRightRectangle().getHeight());
            g2.fillRect(rectangle.getRightRectangle().getTopLeftPoint().getX(), rectangle.getRightRectangle().getTopLeftPoint().getY(), rectangle.getRightRectangle().getWidth(), rectangle.getRightRectangle().getHeight());

            // draw hint
            int stringWidth = graphics.getFontMetrics(getFont()).stringWidth(Messages.Plugin_Runtime_ClipScreenshotToEvernote_Hint);
            int stringHeight = graphics.getFontMetrics(getFont()).getHeight() + 1;
            int charWidth = stringWidth / Messages.Plugin_Runtime_ClipScreenshotToEvernote_Hint.length();
            int start_x = charWidth * 1;
            int start_y = -(2 + charWidth);
            stringWidth += charWidth * 8;

            if (stringWidth > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                stringWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
            }
            GeomPoint p = rectangle.getTopLeftPoint();
            if (p.getY() - (stringHeight + 2) < 0) {
                p = new GeomPoint(p.getX(), p.getY() + stringHeight + 2);
            }
            if (p.getX() + stringWidth > Toolkit.getDefaultToolkit().getScreenSize().getWidth()) {
                p = new GeomPoint((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - stringWidth, p.getY());
            }
            g2.drawImage(ImageUtil.mask(fullScreen.getSubimage(p.getX(), p.getY() - (stringHeight + 2), stringWidth, stringHeight), PLUGIN_SCREENSHOT_HINT_SCALEFACTOR), p.getX(), p.getY() - (stringHeight + 2), null);
            g2.setColor(Color.WHITE);
            g2.setFont(getFont().deriveFont(Font.BOLD));
            g2.drawString(Messages.Plugin_Runtime_ClipScreenshotToEvernote_Hint, p.getX() + start_x, p.getY() + start_y);
        }
    }

    private boolean isResize() {
        return getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR) || getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    private void maskFullScreen(final float scaleFactor) {
        setContentPane(new JPanel() {
            @Override
            public void paintComponent(final Graphics graphics) {
                super.paintComponent(graphics);
                ((Graphics2D) graphics).drawImage(ImageUtil.mask(fullScreen, scaleFactor), 0, 0, null);
            }
        });
        validate();
    }

    private void resetView() {
        setContentPane(new JPanel() {
            @Override
            public void paintComponent(final Graphics graphics) {
                super.paintComponent(graphics);
                Graphics2D g2 = (Graphics2D) graphics;
                g2.drawImage(fullScreen, 0, 0, null);
                g2.setColor(ColorUtil.AWT_EVERNOTE_GREEN);
                g2.setStroke(new BasicStroke(6));
                g2.drawRect(0, 0, new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
            }
        });
        validate();
    }

    private void escape() {
        setVisible(false);
        dispose();
        synchronized (notifier) {
            notifier.notifyAll();
        }
    }

    public BufferedImage getScreenshot() {
        if (!rectangle.isRealRectangle()) {
            return null;
        }
        return fullScreen.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    // Starter method to launch Capture View //

    private static CaptureView view = null;
    private static Object notifier = new Object();

    public static BufferedImage showView() throws InterruptedException {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    view = new CaptureView();
                    if (SystemUtils.IS_OS_WINDOWS) {
                        view.setVisible(true);
                    } else {
                        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                        LogUtil.debug(Messages.bind(Messages.Plugin_Debug_IsFullScreenSupported, device.isFullScreenSupported()));
                        if (device.isFullScreenSupported()) {
                            device.setFullScreenWindow(view);
                        } else {
                            view.setVisible(true);
                        }
                    }
                } catch (Exception e) {
                    view = null;
                    notifier.notifyAll();
                }
            }
        });

        synchronized (notifier) {
            LogUtil.debug(Messages.Plugin_Debug_WaitingCaptureScreenshot);
            notifier.wait();
        }

        BufferedImage capturedScreenshot = view != null ? view.getScreenshot() : null;
        LogUtil.debug(Messages.bind(Messages.Plugin_Debug_CapturedScreenshot, capturedScreenshot));

        return capturedScreenshot;
    }
}
