package com.prairie.eevernote.widgets;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import javax.swing.JPanel;
import javax.swing.JWindow;

import com.prairie.eevernote.Constants;

@SuppressWarnings("serial")
public class CaptureView extends JWindow implements Constants {

	private BufferedImage screenshot;

	private GeomRectangle rectangle = new GeomRectangle();
	private GeomPoint mouseLocation = new GeomPoint();
	private boolean drawXLine = true;
	private boolean drawCropedScreenshot = false;

	public CaptureView() throws HeadlessException, AWTException {

		this.screenshot = captureScreenshot();
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				Graphics2D graphics2D = (Graphics2D) graphics;
				graphics2D.drawImage(mask(1F), 0, 0, null);
				graphics2D.setColor(Color.BLUE);
				graphics2D.setStroke(new BasicStroke(6));
				graphics2D.drawRect(0, 0, new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					CaptureView.this.setVisible(false);
					dispose();
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == 2) {
						setVisible(false);
						dispose();
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (e.getClickCount() == 1) {
						if (!drawXLine) {
							drawXLine = true;
						} else {
							setVisible(false);
							dispose();
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (drawXLine && e.getButton() == MouseEvent.BUTTON1) {
					rectangle.getTopLeftPoint().setLocation(e.getX(), e.getY());
					drawCropedScreenshot = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (drawXLine && e.getButton() == MouseEvent.BUTTON1) {
					rectangle.getBottomRightPoint().setLocation(e.getX(), e.getY());
					drawXLine = false;
					drawCropedScreenshot = false;
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (drawXLine) {
					drawCropedScreenshot = true;
					rectangle.getBottomRightPoint().setLocation(e.getX(), e.getY());
					repaint();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (drawXLine) {
					mouseLocation.setLocation(e.getX(), e.getY());
					rectangle.getTopLeftPoint().setLocation(e.getX(), e.getY());
					repaint();
				}
			}
		});

		setAlwaysOnTop(true);
		requestFocus();
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		if (drawCropedScreenshot) {
			graphics.setColor(Color.WHITE);
			Image cropedScreenshot = screenshot.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
			graphics.drawImage(cropedScreenshot, rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), null);
			graphics.setColor(Color.BLACK);
			graphics.drawRect(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), Math.abs(rectangle.getBottomRightPoint().getX() - rectangle.getTopLeftPoint().getX()), Math.abs(rectangle.getBottomRightPoint().getY() - rectangle.getTopLeftPoint().getY()));
		} else if (drawXLine) {
			graphics.drawLine(this.mouseLocation.getX(), 0, this.mouseLocation.getX(), getHeight());
			graphics.drawLine(0, this.mouseLocation.getY(), getWidth(), this.mouseLocation.getY());
		}
	}

	private BufferedImage mask(float scaleFactor) {
		RescaleOp ro = new RescaleOp(scaleFactor, 0, null);
		BufferedImage rescaledScreenshot = ro.filter(CaptureView.this.screenshot, null);
		return rescaledScreenshot;
	}

	private BufferedImage captureScreenshot() throws HeadlessException, AWTException {
		return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

	public static BufferedImage showView() throws HeadlessException, AWTException {
		CaptureView view = new CaptureView();
		view.setVisible(true);
		return null;// view.getScreenshot();
	}

	public BufferedImage getScreenshot() {
		System.out.println(rectangle.getTopLeftPoint().getX() + " " + rectangle.getTopLeftPoint().getY() + " " + rectangle.getWidth() + " " + rectangle.getHeight());
		return this.screenshot.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
	}

}