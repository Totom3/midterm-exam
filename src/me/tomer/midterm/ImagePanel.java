package me.tomer.midterm;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

	private BufferedImage image;

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage img) {
		image = img;
		repaint();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (image == null) {
			return;
		}

		int w = getWidth();
		int h = getHeight();

		float frameAspect = getWidth() / (float) getHeight();
		float imageAspect = image.getWidth() / (float) image.getHeight();

		if (imageAspect < frameAspect) {
			float mult = getHeight() / (float) image.getHeight();
			w = Math.round(mult * image.getWidth());
		} else {
			float mult = getWidth() / (float) image.getWidth();
			h = Math.round(mult * image.getHeight());
		}

		g.drawImage(image, 0, 0, w, h, null);
	}

}
