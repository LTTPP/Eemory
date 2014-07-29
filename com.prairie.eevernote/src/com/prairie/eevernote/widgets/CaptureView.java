package com.prairie.eevernote.widgets;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
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

	private BufferedImage fullScreen;
	private GeomRectangle rectangle = new GeomRectangle();
	private boolean isCapturing = false;
	private boolean isCaptured = false;
	private boolean isCaptureFullScreenViaClick = false;
	private int runTimes;

	public CaptureView() throws HeadlessException, AWTException {

		this.fullScreen = captureFullScreen();
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		resetView();

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
					if (e.getClickCount() == Constants.ONE) {
						if (!isCaptured) {
							isCaptureFullScreenViaClick = true;
							rectangle.getStartPoint().setLocation(Constants.ZERO, Constants.ZERO);
							rectangle.getEndPoint().setLocation(new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
							repaint();
							isCaptured = true;
						}
					} else if (e.getClickCount() == Constants.TWO) {
						setVisible(false);
						dispose();
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (e.getClickCount() == Constants.ONE) {
						if (isCaptured) {
							resetView();
							isCaptured = false;
							isCaptureFullScreenViaClick = false;
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						} else {
							setVisible(false);
							dispose();
						}
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (!isCaptured && (e.getButton() == MouseEvent.BUTTON1)) {
					rectangle.getStartPoint().setLocation(e.getX(), e.getY());
					isCapturing = true;
					resetTimes(Constants.ONE);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isCapturing && (e.getButton() == MouseEvent.BUTTON1)) {
					rectangle.getEndPoint().setLocation(e.getX(), e.getY());
					isCapturing = false;
					isCaptured = rectangle.getWidth() > Constants.ZERO && rectangle.getHeight() > Constants.ZERO;
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (isCapturing) {
					if (runTimes()) {
						maskFullScreen(Constants.ONE_DOT_SEVEN_F);
					}
					rectangle.getEndPoint().setLocation(e.getX(), e.getY());
					repaint();
				} else if (isCaptured) {
					//
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (!isCapturing && !isCaptured) {
					rectangle.getStartPoint().setLocation(e.getX(), e.getY());
				} else if (isCaptured) {
					if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.EAST) {
						setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.SOUTH) {
						setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.WEST) {
						setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.NORTH) {
						setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.NORTHEAST) {
						setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.NORTHWEST) {
						setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.SOUTHEAST) {
						setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.SOUTHWEST) {
						setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
					} else if (rectangle.positionOfPoint(new GeomPoint(e.getX(), e.getY())) == GeomRectangle.Position.INSIDE) {
						setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
					} else {
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		});
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		if (isCapturing || isCaptureFullScreenViaClick) {
			Image cropedScreenshot = fullScreen.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
			Graphics2D graphics2D = (Graphics2D) graphics;
			graphics2D.drawImage(cropedScreenshot, rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), null);
			graphics2D.setColor(Color.GREEN);
			graphics2D.drawRect(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
			for (GeomRectangle.Position p : GeomRectangle.Position.values()) {
				GeomPoint point = rectangle.pointAt(p);
				if (point != null) {
					graphics2D.drawRect(point.getX() - 2, point.getY() - 2, 4, 4);
					graphics2D.fillRect(point.getX() - 2, point.getY() - 2, 4, 4);
				}
			}
		}
	}

	private boolean runTimes() {
		return runTimes-- > Constants.ZERO;
	}

	private void resetTimes(int times) {
		this.runTimes = times;
	}

	private BufferedImage mask(BufferedImage image, float scaleFactor) {
		RescaleOp ro = new RescaleOp(scaleFactor, Constants.ZERO, null);
		BufferedImage rescaledScreenshot = ro.filter(image, null);
		return rescaledScreenshot;
	}

	private void maskFullScreen(final float scaleFactor) {
		setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				((Graphics2D) graphics).drawImage(mask(fullScreen, scaleFactor), Constants.ZERO, Constants.ZERO, null);
			}
		});
		requestFocus();
		setAlwaysOnTop(true);
		setVisible(true);
	}

	private void resetView() {
		setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				Graphics2D graphics2D = (Graphics2D) graphics;
				graphics2D.drawImage(fullScreen, Constants.ZERO, Constants.ZERO, null);
				graphics2D.setColor(Color.GREEN);
				graphics2D.setStroke(new BasicStroke(Constants.SIX));
				graphics2D.drawRect(Constants.ZERO, Constants.ZERO, new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
			}
		});
		requestFocus();
		setAlwaysOnTop(true);
		setVisible(true);
	}

	private BufferedImage captureFullScreen() throws HeadlessException, AWTException {
		return new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

	public BufferedImage getScreenshot() {
		return this.fullScreen.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
	}

	public static BufferedImage showView() throws HeadlessException, AWTException {
		CaptureView view = new CaptureView();
		view.setVisible(true);
		return view.getScreenshot();
	}

}