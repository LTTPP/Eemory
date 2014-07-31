package com.prairie.eevernote.widgets;

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

import javax.swing.JPanel;
import javax.swing.JWindow;

import com.prairie.eevernote.Constants;
import com.prairie.eevernote.util.ColorUtil;
import com.prairie.eevernote.util.ImageUtil;
import com.prairie.eevernote.util.RunningCounter;
import com.prairie.eevernote.widgets.GeomRectangle.Position;

@SuppressWarnings("serial")
public class CaptureView extends JWindow implements Constants {

	private BufferedImage fullScreen;
	private GeomRectangle rectangle = new GeomRectangle();
	private boolean isCapturing = false;
	private boolean isCaptured = false;
	private boolean isCaptureFullScreenViaClick = false;
	private RunningCounter counter = new RunningCounter();
	private GeomPoint datumPoint;

	public CaptureView() throws HeadlessException, AWTException {

		this.fullScreen = ImageUtil.captureScreen(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		resetView();

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					setVisible(false);
					dispose();
				}
			}
		});
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == ONE) {
						if (!isCaptured) {
							isCaptureFullScreenViaClick = true;
							rectangle.getStartPoint().setLocation(ZERO, ZERO);
							rectangle.getEndPoint().setLocation(new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
							repaint();
							isCaptured = true;
						}
					} else if (e.getClickCount() == TWO) {
						System.out.println(">>>>>>>" + rectangle);
						setVisible(false);
						dispose();
					}
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					if (e.getClickCount() == ONE) {
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
					counter.resetTimes(ONE);
				} else if (isResize()) {
					datumPoint = new GeomPoint(e.getX(), e.getY());
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isCapturing && (e.getButton() == MouseEvent.BUTTON1)) {
					rectangle.getEndPoint().setLocation(e.getX(), e.getY());
					isCapturing = false;
					isCaptured = rectangle.getWidth() > ZERO && rectangle.getHeight() > ZERO;
				}
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				if (isCapturing) {
					if (counter.hasTimes()) {
						maskFullScreen(ONE_DOT_SEVEN_F);
					}
					rectangle.getEndPoint().setLocation(e.getX(), e.getY());
					if (rectangle.isRealRectangle()) {
						repaint();
					}
				} else if (isResize()) {
					if (getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) {
						rectangle.resize(Position.EAST, e.getX() - datumPoint.getX(), ZERO);
					} else if (getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)) {
						rectangle.resize(Position.SOUTH, ZERO, e.getY() - datumPoint.getY());
					} else if (getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) {
						rectangle.resize(Position.WEST, e.getX() - datumPoint.getX(), ZERO);
					} else if (getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)) {
						rectangle.resize(Position.NORTH, ZERO, e.getY() - datumPoint.getY());
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
			public void mouseMoved(MouseEvent e) {
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
						setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					}
				}
			}
		});
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);
		if (isCapturing || isCaptureFullScreenViaClick || isResize()) {
			Image cropedScreenshot = fullScreen.getSubimage(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());
			Graphics2D graphics2D = (Graphics2D) graphics;
			graphics2D.drawImage(cropedScreenshot, rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), null);

			graphics2D.setColor(ColorUtil.EVERNOTE_GREEN);
			graphics2D.drawRect(rectangle.getTopLeftPoint().getX(), rectangle.getTopLeftPoint().getY(), rectangle.getWidth(), rectangle.getHeight());

			graphics2D.drawRect(rectangle.getTopLeftRectangle().getTopLeftPoint().getX(), rectangle.getTopLeftRectangle().getTopLeftPoint().getY(), rectangle.getTopLeftRectangle().getWidth(), rectangle.getTopLeftRectangle().getHeight());
			graphics2D.fillRect(rectangle.getTopLeftRectangle().getTopLeftPoint().getX(), rectangle.getTopLeftRectangle().getTopLeftPoint().getY(), rectangle.getTopLeftRectangle().getWidth(), rectangle.getTopLeftRectangle().getHeight());

			graphics2D.drawRect(rectangle.getTopRightRectangle().getTopLeftPoint().getX(), rectangle.getTopRightRectangle().getTopLeftPoint().getY(), rectangle.getTopRightRectangle().getWidth(), rectangle.getTopRightRectangle().getHeight());
			graphics2D.fillRect(rectangle.getTopRightRectangle().getTopLeftPoint().getX(), rectangle.getTopRightRectangle().getTopLeftPoint().getY(), rectangle.getTopRightRectangle().getWidth(), rectangle.getTopRightRectangle().getHeight());

			graphics2D.drawRect(rectangle.getBottomLeftRectangle().getTopLeftPoint().getX(), rectangle.getBottomLeftRectangle().getTopLeftPoint().getY(), rectangle.getBottomLeftRectangle().getWidth(), rectangle.getBottomLeftRectangle().getHeight());
			graphics2D.fillRect(rectangle.getBottomLeftRectangle().getTopLeftPoint().getX(), rectangle.getBottomLeftRectangle().getTopLeftPoint().getY(), rectangle.getBottomLeftRectangle().getWidth(), rectangle.getBottomLeftRectangle().getHeight());

			graphics2D.drawRect(rectangle.getBottomRightRectangle().getTopLeftPoint().getX(), rectangle.getBottomRightRectangle().getTopLeftPoint().getY(), rectangle.getBottomRightRectangle().getWidth(), rectangle.getBottomRightRectangle().getHeight());
			graphics2D.fillRect(rectangle.getBottomRightRectangle().getTopLeftPoint().getX(), rectangle.getBottomRightRectangle().getTopLeftPoint().getY(), rectangle.getBottomRightRectangle().getWidth(), rectangle.getBottomRightRectangle().getHeight());

			graphics2D.drawRect(rectangle.getTopRectangle().getTopLeftPoint().getX(), rectangle.getTopRectangle().getTopLeftPoint().getY(), rectangle.getTopRectangle().getWidth(), rectangle.getTopRectangle().getHeight());
			graphics2D.fillRect(rectangle.getTopRectangle().getTopLeftPoint().getX(), rectangle.getTopRectangle().getTopLeftPoint().getY(), rectangle.getTopRectangle().getWidth(), rectangle.getTopLeftRectangle().getHeight());

			graphics2D.drawRect(rectangle.getBottomRectangle().getTopLeftPoint().getX(), rectangle.getBottomRectangle().getTopLeftPoint().getY(), rectangle.getBottomRectangle().getWidth(), rectangle.getBottomRectangle().getHeight());
			graphics2D.fillRect(rectangle.getBottomRectangle().getTopLeftPoint().getX(), rectangle.getBottomRectangle().getTopLeftPoint().getY(), rectangle.getBottomRectangle().getWidth(), rectangle.getBottomRectangle().getHeight());

			graphics2D.drawRect(rectangle.getLeftRectangle().getTopLeftPoint().getX(), rectangle.getLeftRectangle().getTopLeftPoint().getY(), rectangle.getLeftRectangle().getWidth(), rectangle.getLeftRectangle().getHeight());
			graphics2D.fillRect(rectangle.getLeftRectangle().getTopLeftPoint().getX(), rectangle.getLeftRectangle().getTopLeftPoint().getY(), rectangle.getLeftRectangle().getWidth(), rectangle.getLeftRectangle().getHeight());

			graphics2D.drawRect(rectangle.getRightRectangle().getTopLeftPoint().getX(), rectangle.getRightRectangle().getTopLeftPoint().getY(), rectangle.getRightRectangle().getWidth(), rectangle.getRightRectangle().getHeight());
			graphics2D.fillRect(rectangle.getRightRectangle().getTopLeftPoint().getX(), rectangle.getRightRectangle().getTopLeftPoint().getY(), rectangle.getRightRectangle().getWidth(), rectangle.getRightRectangle().getHeight());
		}
	}

	private boolean isResize() {
		return (getCursor() == Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR)) || (getCursor() == Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	private void maskFullScreen(final float scaleFactor) {
		setContentPane(new JPanel() {
			@Override
			public void paintComponent(Graphics graphics) {
				super.paintComponent(graphics);
				((Graphics2D) graphics).drawImage(ImageUtil.mask(fullScreen, scaleFactor), ZERO, ZERO, null);
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
				graphics2D.drawImage(fullScreen, ZERO, ZERO, null);
				graphics2D.setColor(ColorUtil.EVERNOTE_GREEN);
				graphics2D.setStroke(new BasicStroke(SIX));
				graphics2D.drawRect(ZERO, ZERO, new Double(Toolkit.getDefaultToolkit().getScreenSize().getWidth()).intValue(), new Double(Toolkit.getDefaultToolkit().getScreenSize().getHeight()).intValue());
			}
		});
		requestFocus();
		setAlwaysOnTop(true);
		setVisible(true);
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
