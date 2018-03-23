import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeSet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;

public class Coursework extends Component implements ActionListener {

	private static final String cwMessage = "Image Processing Coursework";

	String descs[] = { cwMessage, "-Lab 1-", "Original", "-Lab 2-", "Rescale", "Shifting", "Rescale&Shift", "-Lab 3-",
			"Add", "Takeaway", "Divide", "Multiply", "Not", "And", "Or", "Xor", "ROI", "-Lab 4-", "Log", "Power",
			"Random", "Slicing", "-Lab 5-", "Histogram", "-Lab 6-", "Averaging", "WeightedAveraging", "Laplacian4",
			"Laplacian8", "Laplacian4Enhancement", "Laplacian8Enhancement", "Roberts1", "Roberts2", "SobelX", "SobelY",
			"-Lab 7-", "Salt Pepper", "Min Filter", "Max Filter", "MidPoint Filter", "Median Filter", "-Lab 8-",
			"HistogramM&SD", "Simple Thresholding", "Automated Thresholding" };

	private int opIndex;
	private int lastOp;

	private static ArrayList<BufferedImage> undoImage = new ArrayList<BufferedImage>();// undo image
	private BufferedImage bi, bii, biFiltered, biiFiltered;

	private static int image1_w, image1_h;// first image
	private static int image2_w, image2_h;// second image

	private static final String PICTURE1 = "src/image/Cameraman.bmp";
	private static final String PICTURE2 = "src/image/g.jpeg";

	private static final String READ_RAW_IMAGE = "src/image/Peppers.raw";// read raw image
	private static final String PICTURE3 = "src/image/psg.png";// help raw image

	public Coursework() {
		try {

			// ###########################################################//
			// -------------------Lab 1 - Read Normal Image------------------//
			// ###########################################################//

			System.out.println("Press 1 to read Normal Images, Press 2 to read Raw Image");
			Scanner sc = new Scanner(System.in);
			int scan = sc.nextInt();

			if (scan == 2) {
				JOptionPane.showMessageDialog(null,
						"Due to the nature of .raw images, convoluting doesn't work on them!"
								+ "\nUsers are advised to press 1 (normal images) to apply all filtering on images");

			}

			switch (scan) {
			case 1:
				bi = ImageIO.read(new File(PICTURE1));
				image1_w = bi.getWidth(null);
				image1_h = bi.getHeight(null);

				bii = ImageIO.read(new File(PICTURE2));
				image2_w = bii.getWidth(null);
				image2_h = bii.getHeight(null);

				if (bii.getType() != BufferedImage.TYPE_INT_RGB || bi.getType() != BufferedImage.TYPE_INT_RGB) {
					BufferedImage bi2 = new BufferedImage(image1_w, image1_h, BufferedImage.TYPE_INT_RGB);
					Graphics big = bi2.getGraphics();
					big.drawImage(bi, 0, 0, this);
					biFiltered = bi = bi2;

					BufferedImage bii2 = new BufferedImage(image2_w, image2_h, BufferedImage.TYPE_INT_RGB);
					Graphics bigi = bii2.getGraphics();
					bigi.drawImage(bii, 0, 0, this);
					biiFiltered = bii = bii2;
				}
				return;
			case 2:
				// ###########################################################//
				// -------------------Lab 1 - Read RAW Image------------------//
				// ###########################################################//

				byte[] fB = Files.readAllBytes(Paths.get(READ_RAW_IMAGE));
				int x1 = 512, y1 = 512;
				int[][][] arr = new int[x1][y1][4];
				int x = 0;
				int y = 0;
				int k = 0;
				while (k < fB.length) {//length = dimension
					arr[x][y][0] = 255;

					for (int j = 1; j < 4; j++) {
						arr[x][y][j] = (int) fB[k] + 0xff;
					}
					x++;
					if (x == x1) {
						x = 0;
						y++;
					}
					k++;
				}

				bi = new BufferedImage(x1, y1, BufferedImage.TYPE_USHORT_GRAY);

				for (int i = 0; i < y1; i++) {
					for (int j = 0; j < x1; j++) {
						int a = 255;
						int r = arr[i][j][1];
						int g = arr[i][j][2];
						int b = arr[i][j][3];

						int p = (a << 24) | (r << 16) | (g << 8) | b;
						bi.setRGB(i, j, p);

					}
					bii = ImageIO.read(new File(PICTURE3));
				}

				image1_w = bi.getWidth(null);
				image1_h = bi.getHeight(null);

				image2_w = bii.getWidth(null);
				image2_h = bii.getHeight(null);

				if (bii.getType() != BufferedImage.TYPE_INT_RGB || bi.getType() != BufferedImage.TYPE_INT_RGB) {
					BufferedImage bi2 = new BufferedImage(image1_w, image1_h, BufferedImage.TYPE_INT_RGB);
					Graphics big = bi2.getGraphics();
					big.drawImage(bi, 0, 0, this);
					biFiltered = bi = bi2;

					BufferedImage bii2 = new BufferedImage(image2_w, image2_h, BufferedImage.TYPE_INT_RGB);
					Graphics bigi = bii2.getGraphics();
					bigi.drawImage(bii, 0, 0, this);
					biiFiltered = bii = bii2;
				}
				return;
			default:
				showDialog("Error: Enter 1 or 2");
				System.exit(0);
				return;
			}

		} catch (IOException e) {
			showDialog("Image could not be read");
			System.exit(1);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(image1_w, image1_h);
	}

	String[] getDescriptions() {
		return descs;
	}

	public String[] getFormats() {
		String[] formats = { "bmp", "gif", "jpeg", "jpg", "png" };
		TreeSet<String> formatSet = new TreeSet<String>();
		for (String s : formats) {
			formatSet.add(s.toLowerCase());
		}
		return formatSet.toArray(new String[0]);
	}

	void setOpIndex(int i) {
		opIndex = i;
	}

	public void paint(Graphics g) {
		filterImage();

		// ###########################################################//
		// ---------------------Lab 1 - Two Images--------------------//
		// ###########################################################//

		g.drawImage(biFiltered, 0, 0, this);// first image layout
		g.drawImage(biiFiltered, 544, 0, this);// second image layout
	}

	private static int[][][] convertToArray(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();

		int[][][] result = new int[width][height][4];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int p = image.getRGB(x, y);
				int a = (p >> 24) & 0xff;
				int r = (p >> 16) & 0xff;
				int g = (p >> 8) & 0xff;
				int b = p & 0xff;

				result[x][y][0] = a;
				result[x][y][1] = r;
				result[x][y][2] = g;
				result[x][y][3] = b;
			}
		}
		return result;
	}

	public BufferedImage convertToBimage(int[][][] TmpArray) {

		int width = TmpArray.length;
		int height = TmpArray[0].length;

		BufferedImage tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int a = TmpArray[x][y][0];
				int r = TmpArray[x][y][1];
				int g = TmpArray[x][y][2];
				int b = TmpArray[x][y][3];

				int p = (a << 24) | (r << 16) | (g << 8) | b;
				tmpimg.setRGB(x, y, p);

			}
		}
		return tmpimg;
	}

	public void filterImage() {

		if (opIndex == lastOp) {
			return;
		}

		lastOp = opIndex;
		switch (lastOp) {
		case 0:
			// Image Processing Coursework
			System.err.println(cwMessage);
			return;
		case 1:
			// LAB 1
			System.err.println("Lab 1");
			return;
		case 2:
			biFiltered = bi;
			undoImage.add(biFiltered);
			return;
		case 3:
			// LAB 2
			System.err.println("Lab 2");
			return;
		case 4:
			biFiltered = rescale(bi);
			undoImage.add(biFiltered);
			return;
		case 5:
			biFiltered = shifting(bi);
			undoImage.add(biFiltered);
			return;
		case 6:
			biFiltered = rescaleShifting(bi);
			undoImage.add(biFiltered);
			return;
		case 7:
			// LAB 3
			System.err.println("Lab 3");
			return;
		case 8:
			biFiltered = add(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 9:
			biFiltered = takeaway(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 10:
			biFiltered = divide(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 11:
			biFiltered = multiply(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 12:
			biFiltered = not(bi);
			undoImage.add(biFiltered);
			return;
		case 13:
			biFiltered = and(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 14:
			biFiltered = or(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 15:
			biFiltered = xor(bi, bii);
			undoImage.add(biFiltered);
			return;
		case 16:
			biFiltered = ROI(bi);
			undoImage.add(biFiltered);
			return;
		case 17:
			// LAB 4
			System.err.println("Lab 4");
			return;
		case 18:
			biFiltered = log(bi);
			undoImage.add(biFiltered);
			return;
		case 19:
			biFiltered = power(bi);
			undoImage.add(biFiltered);
			return;
		case 20:
			biFiltered = random(bi);
			undoImage.add(biFiltered);
			return;
		case 21:
			biFiltered = slicing(bi);
			undoImage.add(biFiltered);
			return;
		case 22:
			// LAB 5
			System.err.println("Lab 5");
			undoImage.add(biFiltered);
			return;
		case 23:
			biFiltered = histogram(bi);
			undoImage.add(biFiltered);
			return;
		case 24:
			// LAB 6
			System.err.println("Lab 6");
			return;
		case 25:
			biFiltered = averaging(bi);
			undoImage.add(biFiltered);
			return;
		case 26:
			biFiltered = weightedAveraging(bi);
			undoImage.add(biFiltered);
			return;
		case 27:
			biFiltered = laplacian4(bi);
			undoImage.add(biFiltered);
			return;
		case 28:
			biFiltered = laplacian8(bi);
			undoImage.add(biFiltered);
			return;
		case 29:
			biFiltered = laplacian4Enhancement(bi);
			undoImage.add(biFiltered);
			return;
		case 30:
			biFiltered = laplacian8Enhancement(bi);
			undoImage.add(biFiltered);
			return;
		case 31:
			biFiltered = roberts1(bi);
			undoImage.add(biFiltered);
			return;
		case 32:
			biFiltered = roberts2(bi);
			undoImage.add(biFiltered);
			return;
		case 33:
			biFiltered = sobelX(bi);
			undoImage.add(biFiltered);
			return;
		case 34:
			biFiltered = sobelY(bi);
			undoImage.add(biFiltered);
			return;
		case 35:
			// LAB 7
			System.err.println("Lab 7");
			return;
		case 36:
			biFiltered = saltAndPepper(bi);
			undoImage.add(biFiltered);
			return;
		case 37:
			biFiltered = minFilter(bi);
			undoImage.add(biFiltered);
			return;
		case 38:
			biFiltered = maxFilter(bi);
			undoImage.add(biFiltered);
			return;
		case 39:
			biFiltered = midPointFilter(bi);
			undoImage.add(biFiltered);
			return;
		case 40:
			biFiltered = medianFilter(bi);
			undoImage.add(biFiltered);
			return;
		case 41:
			// LAB 8
			System.err.println("Lab 8");
			return;
		case 42:
			biFiltered = histogramMandSD(bi);
			return;
		case 43:
			biFiltered = simpleThresholding(bi);
			undoImage.add(biFiltered);
			return;
		case 44:
			biFiltered = automatedThresh(bi);
			undoImage.add(biFiltered);
			return;
		case 45:
			System.out.println("Exta Features");
			return;
		}
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JComboBox) {
			JComboBox cb = (JComboBox) e.getSource();
			if (cb.getActionCommand().equals("SetFilter")) {
				setOpIndex(cb.getSelectedIndex());
				repaint();
			} else if (cb.getActionCommand().equals("Formats")) {
				String format = (String) cb.getSelectedItem();
				File saveFile = new File("savedimage." + format);
				JFileChooser chooser = new JFileChooser();
				chooser.setSelectedFile(saveFile);
				int rval = chooser.showSaveDialog(cb);
				if (rval == JFileChooser.APPROVE_OPTION) {
					saveFile = chooser.getSelectedFile();
					try {
						ImageIO.write(biFiltered, format, saveFile);
					} catch (IOException ex) {
					}
				}
			}
		} else {
			JButton undo = (JButton) e.getSource();
			undoImage();
			repaint();
		}
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String s[]) {

		JFrame f = new JFrame(cwMessage);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);// disable 'x' in window
			}
		});

		Coursework de = new Coursework();
		f.add("Center", de);

		JComboBox choices = new JComboBox(de.getDescriptions());
		((JLabel) choices.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);// ComboBox in middle
		choices.setFont(new java.awt.Font("Comic Sans MS", 0, 14));// change text font
		choices.setActionCommand("SetFilter");
		choices.addActionListener(de);

		JComboBox formats = new JComboBox(de.getFormats());
		formats.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		formats.setActionCommand("Formats");
		formats.addActionListener(de);

		JPanel panel = new JPanel();
		JLabel apply = new JLabel("Apply");
		apply.setFont(new java.awt.Font("Comic Sans MS", 1, 14));
		panel.add(apply);
		panel.add(choices);

		JButton undo = new JButton("Undo");
		undo.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		undo.setActionCommand("Undo");
		undo.addActionListener(de);
		panel.add(undo);

		JButton merge = new JButton("Merge ROI");
		merge.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		merge.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				de.mergeROI();
			}
		});
		panel.add(merge);
		JButton reset = new JButton("Reset");
		reset.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				de.reset();
			}
		});

		panel.add(reset);
		JButton exit = new JButton("Close");
		exit.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		panel.add(exit);
		exit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				exitButtonActionPerformed(evt);
			}
		});

		JLabel save = new JLabel("Save");
		save.setFont(new java.awt.Font("Comic Sans MS", 1, 14));
		panel.add(save);

		javax.swing.JMenuBar menuBar;
		javax.swing.JMenuItem help;
		javax.swing.JMenuItem description;

		menuBar = new javax.swing.JMenuBar();
		help = new javax.swing.JMenu();
		description = new javax.swing.JMenu();

		help.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		help.setText("Help");
		help.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				helpMenu();
			}
		});
		help.add(help);
		menuBar.add(help);
		f.setJMenuBar(menuBar);

		description.setFont(new java.awt.Font("Comic Sans MS", 0, 14));
		description.setText("Description");
		description.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				descriptionMenu();
			}
		});
		description.add(description);
		menuBar.add(description);
		f.setJMenuBar(menuBar);

		panel.add(formats);
		f.add("South", panel);
		f.pack();
		f.setLocationRelativeTo(null); // set screen middle of the screen
		f.setVisible(true);
	}

	private void reset() {
		try {
			BufferedImage original = ImageIO.read(new File(this.PICTURE1));
			this.undoImage.removeAll(this.undoImage);
			BufferedImage bi4 = original;
			Graphics big = bi4.getGraphics();
			big.drawImage(bi4, 0, 0, null);
			this.biFiltered = this.bi = bi4;
			this.repaint();
		} catch (Exception e1) {
		}
	}

	private void undoImage() {
		if (!undoImage.isEmpty()) {
			biFiltered = undoImage.get(undoImage.size() - 1);
			undoImage.remove(undoImage.size() - 1);
			if (undoImage.isEmpty()) {
				undoImage.add(bi);
			}
		} else {
			showDialog("Error: List is Empty");
		}
		return;
	}

	private static void showDialog(String msg) {
		JOptionPane.showMessageDialog(null, msg, cwMessage, JOptionPane.ERROR_MESSAGE);
	}

	private static void helpMenu() {
		showDialog("\nTo merge the ROI simple choose a filter and press the Merge ROI button."
				+ "\nThe 'undo' button allows users to go back to previous image(s)."
				+ "\nThe 'x' button is disabled, to close application press the 'close' button.");
	}

	private static void descriptionMenu() {
		showDialog("\nLab 1 contains reading Raw Image and graphic user interface."
				+ "\nLab 2 contains Shifting and rescaling."
				+ "\nLab 3 contains Arithmetic operations, Bitwise boolean and ROI based operations."
				+ "\nLab 4 contains Linear, Logarithmic, Power-law and Bit-plane slicing."
				+ "\nLab 5 contains Histogram Normalisation, Equalisation and displaying."
				+ "\nLab 6 contains Averaging, Laplacian, Robers and Sobel."
				+ "\nLab 7 contains Salt-and-Pepper, Min, Max Midpoint and Median Filtering."
				+ "\nLab 8 contains mean and Standard Deviation, Simple and Automated Thresholding.");
	}

	private static void exitButtonActionPerformed(ActionEvent e) {
		System.exit(0);
	}

	// ###########################################################//
	// -------LAB 2 - re-scaling / shifting of pixel values-------//
	// ###########################################################//

	public BufferedImage shifting(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray[x][y][1] = ImageArray[x][y][1] + 25;
				ImageArray[x][y][2] = ImageArray[x][y][2] + 25;
				ImageArray[x][y][3] = ImageArray[x][y][3] + 25;

				ImageArray[x][y][1] = boundChecker(ImageArray[x][y][1]);
				ImageArray[x][y][2] = boundChecker(ImageArray[x][y][2]);
				ImageArray[x][y][3] = boundChecker(ImageArray[x][y][3]);
			}
		}
		return convertToBimage(ImageArray);
	}

	public BufferedImage rescale(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray[x][y][1] = (int) (ImageArray[x][y][1] * 2);
				ImageArray[x][y][2] = (int) (ImageArray[x][y][2] * 2);
				ImageArray[x][y][3] = (int) (ImageArray[x][y][3] * 2);

				ImageArray[x][y][1] = boundChecker(ImageArray[x][y][1]);
				ImageArray[x][y][2] = boundChecker(ImageArray[x][y][2]);
				ImageArray[x][y][3] = boundChecker(ImageArray[x][y][3]);
			}
		}
		return convertToBimage(ImageArray);
	}

	private static int boundChecker(int RGB) {
		if (RGB > 255)
			RGB = 255;
		if (RGB < 0)
			RGB = 0;
		return RGB;
	}

	public BufferedImage rescaleShifting(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();
		
		int[][][] ImageArray1 = convertToArray(timg);
		int[][][] ImageArray2 = new int[width][height][4];
		int t = 5;
		int s = (int) (Math.random() * 255);
 
		int rmin, rmax, gmin, gmax, bmin, bmax;
		rmin = s * (ImageArray1[0][0][1] + t);
		rmax = rmin;
		gmin = s * (ImageArray1[0][0][2] + t);
		gmax = gmin;
		bmin = s * (ImageArray1[0][0][3] + t);
		bmax = bmin;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				ImageArray2[x][y][1] = s * (ImageArray1[x][y][1] + t); // r
				ImageArray2[x][y][2] = s * (ImageArray1[x][y][2] + t); // g
				ImageArray2[x][y][3] = s * (ImageArray1[x][y][3] + t); // b
				
				if (rmin > ImageArray2[x][y][1]) {
					rmin = ImageArray2[x][y][1];
				}
				if (gmin > ImageArray2[x][y][2]) {
					gmin = ImageArray2[x][y][2];
				}
				if (bmin > ImageArray2[x][y][3]) {
					bmin = ImageArray2[x][y][3];
				}
				if (rmax < ImageArray2[x][y][1]) {
					rmax = ImageArray2[x][y][1];
				}
				if (gmax < ImageArray2[x][y][2]) {
					gmax = ImageArray2[x][y][2];
				}
				if (bmax < ImageArray2[x][y][3]) {
					bmax = ImageArray2[x][y][3];
				}
			}
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][0] = ImageArray1[x][y][0];
				ImageArray2[x][y][1] = 255 * (ImageArray2[x][y][1] - rmin) / (rmax - rmin);
				ImageArray2[x][y][2] = 255 * (ImageArray2[x][y][2] - gmin) / (gmax - gmin);
				ImageArray2[x][y][3] = 255 * (ImageArray2[x][y][3] - bmin) / (bmax - bmin);
			}
		}
		return convertToBimage(ImageArray2);

	}

	// ###########################################################//
	// ----------LAB 3 - arithmetic and Boolean operations--------//
	// ###########################################################//

	public BufferedImage not(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];

				ImageArray[x][y][1] = (~r) & 0xFF;
				ImageArray[x][y][2] = (~g) & 0xFF;
				ImageArray[x][y][3] = (~b) & 0xFF;
			}
		}
		return convertToBimage(ImageArray);
	}

	public BufferedImage and(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				ImageArray1[x][y][1] = boundChecker(ImageArray1[x][y][1] & ImageArray2[x][y][1]);
				ImageArray1[x][y][2] = boundChecker(ImageArray1[x][y][2] & ImageArray2[x][y][2]);
				ImageArray1[x][y][3] = boundChecker(ImageArray1[x][y][3] & ImageArray2[x][y][3]);

			}
		}
		return convertToBimage(ImageArray1);
	}

	public BufferedImage xor(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				ImageArray1[x][y][1] = boundChecker(ImageArray1[x][y][1] ^ ImageArray2[x][y][1]);
				ImageArray1[x][y][2] = boundChecker(ImageArray1[x][y][2] ^ ImageArray2[x][y][2]);
				ImageArray1[x][y][3] = boundChecker(ImageArray1[x][y][3] ^ ImageArray2[x][y][3]);
			}
		}
		return convertToBimage(ImageArray1);
	}

	public BufferedImage or(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				ImageArray1[x][y][1] = boundChecker(ImageArray1[x][y][1] | ImageArray2[x][y][1]);
				ImageArray1[x][y][2] = boundChecker(ImageArray1[x][y][2] | ImageArray2[x][y][2]);
				ImageArray1[x][y][3] = boundChecker(ImageArray1[x][y][3] | ImageArray2[x][y][3]);
			}
		}
		return convertToBimage(ImageArray1);
	}

	public BufferedImage add(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				ImageArray1[x][y][1] = boundChecker(ImageArray1[x][y][1] + ImageArray2[x][y][1]);
				ImageArray1[x][y][2] = boundChecker(ImageArray1[x][y][2] + ImageArray2[x][y][2]);
				ImageArray1[x][y][3] = boundChecker(ImageArray1[x][y][3] + ImageArray2[x][y][3]);
			}
		}
		return convertToBimageRescaleShift(ImageArray1);
	}

	public BufferedImage takeaway(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				ImageArray1[x][y][1] = boundChecker(ImageArray1[x][y][1] - ImageArray2[x][y][1]);
				ImageArray1[x][y][2] = boundChecker(ImageArray1[x][y][2] - ImageArray2[x][y][2]);
				ImageArray1[x][y][3] = boundChecker(ImageArray1[x][y][3] - ImageArray2[x][y][3]);
			}
		}
		return convertToBimageRescaleShift(ImageArray1);

	}

	public BufferedImage divide(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		int r, g, b = 0;
		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				try {
					ImageArray1[x][y][1] = boundChecker(ImageArray2[x][y][1] / ImageArray1[x][y][1]);
					ImageArray1[x][y][2] = boundChecker(ImageArray2[x][y][2] / ImageArray1[x][y][2]);
					ImageArray1[x][y][3] = boundChecker(ImageArray2[x][y][3] / ImageArray1[x][y][3]);

				} catch (ArithmeticException e) {
				}
			}
		}
		return convertToBimageRescaleShift(ImageArray1);

	}

	public BufferedImage multiply(BufferedImage timg1, BufferedImage timg2) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] ImageArray1 = convertToArray(timg1);
		int[][][] ImageArray2 = convertToArray(timg2);

		for (int y = 0; y < height1; y++) {
			for (int x = 0; x < width1; x++) {
				ImageArray1[x][y][1] = boundChecker(ImageArray1[x][y][1] * ImageArray2[x][y][1]);
				ImageArray1[x][y][2] = boundChecker(ImageArray1[x][y][2] * ImageArray2[x][y][2]);
				ImageArray1[x][y][3] = boundChecker(ImageArray1[x][y][3] * ImageArray2[x][y][3]);
			}
		}
		return convertToBimageRescaleShift(ImageArray1);
	}

	public BufferedImage convertToBimageRescaleShift(int[][][] TmpArray) {
		int width = TmpArray.length;
		int height = TmpArray[0].length;

		BufferedImage tmpimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int a = 255;
				int r = Math.abs((int) (TmpArray[x][y][1] * .7) + 25);
				int g = Math.abs((int) (TmpArray[x][y][2] * .7) + 25);
				int b = Math.abs((int) (TmpArray[x][y][3] * .7) + 25);

				int p = (a << 24) | (r << 16) | (g << 8) | b;
				tmpimg.setRGB(x, y, p);
			}
		}
		return tmpimg;
	}

	public BufferedImage ROI(BufferedImage timg1) {
		int width1 = timg1.getWidth();
		int height1 = timg1.getHeight();

		int[][][] cameraman = convertToArray(timg1);
		int[][][] MASK = new int[width1][height1][4];
		System.out.println("Press 1 for ROIadd, Press 2 for ROImulitply");
		Scanner sc = new Scanner(System.in);
		int scan = sc.nextInt();

		int rangeLength = 125;
		int rangeHeight = 125;
		int spaceBound = 50;
		if (scan == 1) {
			System.out.println("ROIadd");
			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					if (!(x >= spaceBound && x <= rangeLength && y >= spaceBound && y <= rangeHeight)) {
						MASK[x][y][0] = 255;
						MASK[x][y][1] = 0;
						MASK[x][y][2] = 0;
						MASK[x][y][3] = 0;
					} else {
						MASK[x][y][0] = 255;
						MASK[x][y][1] = 255;
						MASK[x][y][2] = 255;
						MASK[x][y][3] = 255;
					}
				}
			}

			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					cameraman[x][y][0] = cameraman[x][y][0] & MASK[x][y][0];
					cameraman[x][y][1] = cameraman[x][y][1] & MASK[x][y][1];
					cameraman[x][y][2] = cameraman[x][y][2] & MASK[x][y][2];
					cameraman[x][y][3] = cameraman[x][y][3] & MASK[x][y][3];
				}
			}
		} else if (scan == 2) {
			System.out.println("ROImultiply");
			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					if (!(x >= spaceBound && x <= rangeLength && y >= spaceBound && y <= rangeHeight)) {
						MASK[x][y][0] = 255;
						MASK[x][y][1] = 0;
						MASK[x][y][2] = 0;
						MASK[x][y][3] = 0;
					} else {
						MASK[x][y][0] = 1;
						MASK[x][y][1] = 1;
						MASK[x][y][2] = 1;
						MASK[x][y][3] = 1;
					}
				}
			}

			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					cameraman[x][y][0] = cameraman[x][y][0] * MASK[x][y][0];
					cameraman[x][y][1] = cameraman[x][y][1] * MASK[x][y][1];
					cameraman[x][y][2] = cameraman[x][y][2] * MASK[x][y][2];
					cameraman[x][y][3] = cameraman[x][y][3] * MASK[x][y][3];
				}
			}
		} else {
			System.err.println("Error");
		}
		return convertToBimage(cameraman);
	}

	// ###########################################################//
	// --------LAB 4 - point processing and bit-plane slicing-----//
	// ###########################################################//

	public BufferedImage log(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);

		int[] LUT = new int[256];
		int c = (int) (255.0 / Math.log(256.0));
		for (int k = 0; k <= 255; k++) {
			LUT[k] = (int) (Math.log(1 + k) * c);
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];

				ImageArray[x][y][1] = LUT[r];
				ImageArray[x][y][2] = LUT[g];
				ImageArray[x][y][3] = LUT[b];

			}
		}
		return convertToBimage(ImageArray);
	}

	public BufferedImage power(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg); // Convert the image to array

		int[] LUT = new int[256];
		double p = 20;// the higher p the darker image is
		for (int k = 0; k <= 255; k++) {
			LUT[k] = (int) (Math.pow(255, 1 - p) * Math.pow(k, p));
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];

				ImageArray[x][y][1] = LUT[r];
				ImageArray[x][y][2] = LUT[g];
				ImageArray[x][y][3] = LUT[b];
			}
		}
		return convertToBimage(ImageArray); // Convert the array to BufferedImage
	}

	public BufferedImage random(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg); // Convert the image to array
		Random randomGenerator = new Random();

		int[] LUT = new int[256];
		for (int k = 0; k <= 255; k++) {
			LUT[k] = randomGenerator.nextInt(255);
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];

				ImageArray[x][y][1] = LUT[r];
				ImageArray[x][y][2] = LUT[g];
				ImageArray[x][y][3] = LUT[b];
			}
		}
		return convertToBimage(ImageArray);
	}

	public BufferedImage slicing(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);

		int k = 0; // 0,1,2,3...7
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];

				ImageArray[x][y][1] = (r >> k) & 1;
				ImageArray[x][y][2] = (g >> k) & 1;
				ImageArray[x][y][3] = (b >> k) & 1;

				if (ImageArray[x][y][1] == 1) {
					ImageArray[x][y][1] = 255;
				}

				if (ImageArray[x][y][2] == 1) {
					ImageArray[x][y][2] = 255;
				}

				if (ImageArray[x][y][3] == 1) {
					ImageArray[x][y][3] = 255;
				}
			}
		}
		return convertToBimage(ImageArray);
	}

	// ###########################################################//
	// ----------LAB 5 - histogram & histogram equalisation-------//
	// ###########################################################//

	public BufferedImage histogram(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		int SIZE = 256;

		double[] histogramR = new double[SIZE];
		double[] histogramG = new double[SIZE];
		double[] histogramB = new double[SIZE];
		double[] normHistogramR = new double[SIZE];
		double[] normHistogramG = new double[SIZE];
		double[] normHistogramB = new double[SIZE];
		double[] cumulativeHistogramR = new double[SIZE];
		double[] cumulativeHistogramG = new double[SIZE];
		double[] cumulativeHistogramB = new double[SIZE];
		double[] applyR = new double[SIZE];
		double[] applyG = new double[SIZE];
		double[] applyB = new double[SIZE];

		double cumlativeR = 0;
		double cumlativeG = 0;
		double cumlativeB = 0;
		double pixelCount = 0;

		for (int i = 0; i < SIZE; i++) {
			histogramR[i] = 0;
			histogramG[i] = 0;
			histogramB[i] = 0;
			normHistogramR[i] = 0;
			normHistogramG[i] = 0;
			normHistogramB[i] = 0;
			cumulativeHistogramR[i] = 0;
			cumulativeHistogramG[i] = 0;
			cumulativeHistogramB[i] = 0;
			applyR[i] = 0;
			applyG[i] = 0;
			applyB[i] = 0;
		}

		// find histo values
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];
				histogramR[r]++;
				histogramG[g]++;
				histogramB[b]++;
				pixelCount++;
			}
		}

		// normalize
		for (int i = 0; i < SIZE; i++) {
			normHistogramR[i] = (histogramR[i] / pixelCount);
			normHistogramG[i] = (histogramG[i] / pixelCount);
			normHistogramB[i] = (histogramB[i] / pixelCount);
		}

		// cumlative
		for (int i = 0; i < SIZE; i++) {
			cumlativeR += normHistogramR[i];
			cumlativeG += normHistogramG[i];
			cumlativeB += normHistogramB[i];
			cumulativeHistogramR[i] = cumlativeR;
			cumulativeHistogramG[i] = cumlativeG;
			cumulativeHistogramB[i] = cumlativeB;
		}

		// multiply cumlative by 255
		for (int i = 0; i < SIZE; i++) {
			applyR[i] = Math.round(cumulativeHistogramR[i] * 255);
			applyG[i] = Math.round(cumulativeHistogramG[i] * 255);
			applyB[i] = Math.round(cumulativeHistogramB[i] * 255);
		}

		// apply to image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray[x][y][1] = (int) applyR[ImageArray[x][y][1]];
				ImageArray[x][y][2] = (int) applyG[ImageArray[x][y][2]];
				ImageArray[x][y][3] = (int) applyB[ImageArray[x][y][3]];
			}
		}
		return convertToBimage(ImageArray); // Convert the array to BufferedImage
	}

	// ###########################################################//
	// -------------------LAB 6 - linear filtering----------------//
	// ###########################################################//

	double[][][] castImage(int[][][] ImageArray, double[][][] ImageArray2, int width, int height) {

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = (double) ImageArray[x][y][1];
				ImageArray2[x][y][2] = (double) ImageArray[x][y][2];
				ImageArray2[x][y][3] = (double) ImageArray[x][y][3];
			}
		}
		return ImageArray2;
	}

	// Smoothing Image
	BufferedImage averaging(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		for (int y = 0; y < MASK.length; y++) {
			for (int x = 0; x < MASK.length; x++) {
				MASK[x][y] = 1.0 / 9.0;
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = r;
				ImageArray[x][y][2] = g;
				ImageArray[x][y][3] = b;
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage weightedAveraging(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];

		double[][] MASK = new double[3][3];

		MASK[0][0] = 1;
		MASK[0][1] = 2;
		MASK[0][2] = 1;

		MASK[1][0] = 2;
		MASK[1][1] = 4;
		MASK[1][2] = 2;

		MASK[2][0] = 1;
		MASK[2][1] = 2;
		MASK[2][2] = 1;

		for (int y = 0; y < MASK.length; y++) {
			for (int x = 0; x < MASK.length; x++) {
				MASK[x][y] = MASK[x][y] * (1.0 / 16.0);
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = (double) ImageArray[x][y][1];
				ImageArray2[x][y][2] = (double) ImageArray[x][y][2];
				ImageArray2[x][y][3] = (double) ImageArray[x][y][3];
			}
		}

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = r;
				ImageArray[x][y][2] = g;
				ImageArray[x][y][3] = b;
			}
		}
		return convertToBimage(ImageArray);
	}

	// Edge Detection
	BufferedImage laplacian4(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];

		double[][] MASK = new double[3][3];

		MASK[0][0] = 0;
		MASK[0][1] = -1;
		MASK[0][2] = 0;

		MASK[1][0] = -1;
		MASK[1][1] = 4;
		MASK[1][2] = -1;

		MASK[2][0] = 0;
		MASK[2][1] = -1;
		MASK[2][2] = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker(ImageArray[x][y][1]);
				ImageArray2[x][y][2] = boundChecker(ImageArray[x][y][2]);
				ImageArray2[x][y][3] = boundChecker(ImageArray[x][y][3]);
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker(r);
				ImageArray[x][y][2] = boundChecker(g);
				ImageArray[x][y][3] = boundChecker(b);
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage laplacian8(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = -1;
		MASK[0][1] = -1;
		MASK[0][2] = -1;

		MASK[1][0] = -1;
		MASK[1][1] = 8;
		MASK[1][2] = -1;

		MASK[2][0] = -1;
		MASK[2][1] = -1;
		MASK[2][2] = -1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker(ImageArray[x][y][1]);
				ImageArray2[x][y][2] = boundChecker(ImageArray[x][y][2]);
				ImageArray2[x][y][3] = boundChecker(ImageArray[x][y][3]);
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker(r);
				ImageArray[x][y][2] = boundChecker(g);
				ImageArray[x][y][3] = boundChecker(b);
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage laplacian4Enhancement(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = -0;
		MASK[0][1] = -1;
		MASK[0][2] = -1;

		MASK[1][0] = -1;
		MASK[1][1] = 5;
		MASK[1][2] = -1;

		MASK[2][0] = 0;
		MASK[2][1] = -1;
		MASK[2][2] = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker(ImageArray[x][y][1]);
				ImageArray2[x][y][2] = boundChecker(ImageArray[x][y][2]);
				ImageArray2[x][y][3] = boundChecker(ImageArray[x][y][3]);
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker(r);
				ImageArray[x][y][2] = boundChecker(g);
				ImageArray[x][y][3] = boundChecker(b);
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage laplacian8Enhancement(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = -1;
		MASK[0][1] = -1;
		MASK[0][2] = -1;

		MASK[1][0] = -1;
		MASK[1][1] = 9;
		MASK[1][2] = -1;

		MASK[2][0] = -1;
		MASK[2][1] = -1;
		MASK[2][2] = -1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker(ImageArray[x][y][1]);
				ImageArray2[x][y][2] = boundChecker(ImageArray[x][y][2]);
				ImageArray2[x][y][3] = boundChecker(ImageArray[x][y][3]);
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker(r);
				ImageArray[x][y][2] = boundChecker(g);
				ImageArray[x][y][3] = boundChecker(b);
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage roberts1(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = 0;
		MASK[0][1] = 0;
		MASK[0][2] = 0;

		MASK[1][0] = 0;
		MASK[1][1] = 0;
		MASK[1][2] = -1;

		MASK[2][0] = 0;
		MASK[2][1] = 1;
		MASK[2][2] = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker((int) Math.abs((ImageArray[x][y][1])));
				ImageArray2[x][y][2] = boundChecker((int) Math.abs((ImageArray[x][y][2])));
				ImageArray2[x][y][3] = boundChecker((int) Math.abs((ImageArray[x][y][3])));
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker((int) Math.abs(r));
				ImageArray[x][y][2] = boundChecker((int) Math.abs(g));
				ImageArray[x][y][3] = boundChecker((int) Math.abs(b));
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage roberts2(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = 0;
		MASK[0][1] = 0;
		MASK[0][2] = 0;

		MASK[1][0] = 0;
		MASK[1][1] = -1;
		MASK[1][2] = 0;

		MASK[2][0] = 0;
		MASK[2][1] = 0;
		MASK[2][2] = 1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker((int) Math.abs((ImageArray[x][y][1])));
				ImageArray2[x][y][2] = boundChecker((int) Math.abs((ImageArray[x][y][2])));
				ImageArray2[x][y][3] = boundChecker((int) Math.abs((ImageArray[x][y][3])));
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker((int) Math.abs(r));
				ImageArray[x][y][2] = boundChecker((int) Math.abs(g));
				ImageArray[x][y][3] = boundChecker((int) Math.abs(b));
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage sobelX(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = -1;
		MASK[0][1] = 0;
		MASK[0][2] = 1;

		MASK[1][0] = -2;
		MASK[1][1] = 0;
		MASK[1][2] = 2;

		MASK[2][0] = -1;
		MASK[2][1] = 0;
		MASK[2][2] = 1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker((int) Math.abs((ImageArray[x][y][1])));
				ImageArray2[x][y][2] = boundChecker((int) Math.abs((ImageArray[x][y][2])));
				ImageArray2[x][y][3] = boundChecker((int) Math.abs((ImageArray[x][y][3])));
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker((int) Math.abs(r));
				ImageArray[x][y][2] = boundChecker((int) Math.abs(g));
				ImageArray[x][y][3] = boundChecker((int) Math.abs(b));
			}
		}
		return convertToBimage(ImageArray);
	}

	BufferedImage sobelY(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		double[][][] ImageArray2 = new double[256][256][4];
		double[][] MASK = new double[3][3];

		MASK[0][0] = -1;
		MASK[0][1] = -2;
		MASK[0][2] = -1;

		MASK[1][0] = 0;
		MASK[1][1] = 0;
		MASK[1][2] = 0;

		MASK[2][0] = 1;
		MASK[2][1] = 2;
		MASK[2][2] = 1;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				ImageArray2[x][y][1] = boundChecker((int) Math.abs((ImageArray[x][y][1])));
				ImageArray2[x][y][2] = boundChecker((int) Math.abs((ImageArray[x][y][2])));
				ImageArray2[x][y][3] = boundChecker((int) Math.abs((ImageArray[x][y][3])));
			}
		}

		double[][][] image = castImage(ImageArray, ImageArray2, width, height);

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				int r = 0, g = 0, b = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						r = (int) (r + MASK[1 - s][1 - t] * image[x + s][y + t][1]);
						g = (int) (g + MASK[1 - s][1 - t] * image[x + s][y + t][2]);
						b = (int) (b + MASK[1 - s][1 - t] * image[x + s][y + t][3]);
					}
				}
				ImageArray[x][y][1] = boundChecker((int) Math.abs(r));
				ImageArray[x][y][2] = boundChecker((int) Math.abs(g));
				ImageArray[x][y][3] = boundChecker((int) Math.abs(b));
			}
		}
		return convertToBimage(ImageArray);
	}

	// ###########################################################//
	// --------------LAB 7 - order-statistics filtering-----------//
	// ###########################################################//

	public BufferedImage saltAndPepper(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();
		int[][][] ImageArray = convertToArray(timg);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				double randomSaltPepper = Math.random() * 1;

				if (randomSaltPepper < 0.05) {
					ImageArray[x][y][0] = 255;
					ImageArray[x][y][1] = 0;
					ImageArray[x][y][2] = 0;
					ImageArray[x][y][3] = 0;
				} else if (randomSaltPepper > 0.95) {
					ImageArray[x][y][0] = 255;
					ImageArray[x][y][1] = 255;
					ImageArray[x][y][2] = 255;
					ImageArray[x][y][3] = 255;
				}
			}
		}
		return convertToBimage(ImageArray);
	}

	public BufferedImage minFilter(BufferedImage timg) {
		int SIZE = 9;
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[] redWindow = new int[SIZE];
		int[] greenWindow = new int[SIZE];
		int[] blueWindow = new int[SIZE];

		int[][][] ImageArray1 = convertToArray(timg);
		int[][][] ImageArray2 = convertToArray(timg);

		int pixcelCount;

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				pixcelCount = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						redWindow[pixcelCount] = ImageArray1[x + s][y + t][1];
						greenWindow[pixcelCount] = ImageArray1[x + s][y + t][2];
						blueWindow[pixcelCount] = ImageArray1[x + s][y + t][3];
						pixcelCount++;
					}
				}
				Arrays.sort(redWindow);
				Arrays.sort(greenWindow);
				Arrays.sort(blueWindow);

				ImageArray2[x][y][1] = boundChecker(redWindow[0]);// 0 is the min if the array
				ImageArray2[x][y][2] = boundChecker(greenWindow[0]);
				ImageArray2[x][y][3] = boundChecker(blueWindow[0]);
			}
		}
		return convertToBimage(ImageArray2);
	}

	public BufferedImage maxFilter(BufferedImage timg) {
		int SIZE = 9;
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[] redWindow = new int[SIZE];
		int[] greenWindow = new int[SIZE];
		int[] blueWindow = new int[SIZE];

		int[][][] ImageArray1 = convertToArray(timg);
		int[][][] ImageArray2 = convertToArray(timg);

		int pixcelCount;

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				pixcelCount = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						redWindow[pixcelCount] = ImageArray1[x + s][y + t][1];
						greenWindow[pixcelCount] = ImageArray1[x + s][y + t][2];
						blueWindow[pixcelCount] = ImageArray1[x + s][y + t][3];
						pixcelCount++;
					}
				}
				Arrays.sort(redWindow);
				Arrays.sort(greenWindow);
				Arrays.sort(blueWindow);

				ImageArray2[x][y][1] = boundChecker(redWindow[8]);// 8 highest in the array
				ImageArray2[x][y][2] = boundChecker(greenWindow[8]);
				ImageArray2[x][y][3] = boundChecker(blueWindow[8]);
			}
		}

		return convertToBimage(ImageArray2);
	}

	public BufferedImage midPointFilter(BufferedImage timg) {
		int SIZE = 9;
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[] redWindow = new int[SIZE];
		int[] greenWindow = new int[SIZE];
		int[] blueWindow = new int[SIZE];

		int[][][] ImageArray1 = convertToArray(timg);
		int[][][] ImageArray2 = convertToArray(timg);

		int pixcelCount;

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				pixcelCount = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						redWindow[pixcelCount] = ImageArray1[x + s][y + t][1];
						greenWindow[pixcelCount] = ImageArray1[x + s][y + t][2];
						blueWindow[pixcelCount] = ImageArray1[x + s][y + t][3];
						pixcelCount++;
					}
				}
				Arrays.sort(redWindow);
				Arrays.sort(greenWindow);
				Arrays.sort(blueWindow);

				// get the mid and max then divide by 2
				ImageArray2[x][y][1] = boundChecker(redWindow[0] + redWindow[(8)]/2);
				ImageArray2[x][y][2] = boundChecker(greenWindow[0] + greenWindow[(8)]/2);
				ImageArray2[x][y][3] = boundChecker(blueWindow[0] + blueWindow[(8)]/2);
			}
		}
		return convertToBimage(ImageArray2);
	}

	public BufferedImage medianFilter(BufferedImage timg) {
		int SIZE = 9;
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[] redWindow = new int[SIZE];
		int[] greenWindow = new int[SIZE];
		int[] blueWindow = new int[SIZE];

		int[][][] ImageArray1 = convertToArray(timg);
		int[][][] ImageArray2 = convertToArray(timg);

		int pixcelCount;

		for (int y = 1; y < height - 1; y++) {
			for (int x = 1; x < width - 1; x++) {
				pixcelCount = 0;
				for (int s = -1; s <= 1; s++) {
					for (int t = -1; t <= 1; t++) {
						redWindow[pixcelCount] = ImageArray1[x + s][y + t][1];
						greenWindow[pixcelCount] = ImageArray1[x + s][y + t][2];
						blueWindow[pixcelCount] = ImageArray1[x + s][y + t][3];
						pixcelCount++;
					}
				}
				Arrays.sort(redWindow);
				Arrays.sort(greenWindow);
				Arrays.sort(blueWindow);

				// set to median of the array
				ImageArray2[x][y][1] = boundChecker(redWindow[4]);
				ImageArray2[x][y][2] = boundChecker(greenWindow[4]);
				ImageArray2[x][y][3] = boundChecker(blueWindow[4]);
			}
		}
		return convertToBimage(ImageArray2);
	}

	// ###########################################################//
	// ---------------------LAB 8 - thresholding------------------//
	// ###########################################################//

	public BufferedImage histogramMandSD(BufferedImage timg) {
		clearConsole();

		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);
		int SIZE = 256;
		double[] histogramR = new double[SIZE];
		double[] histogramG = new double[SIZE];
		double[] histogramB = new double[SIZE];

		double sumRed = 0, sumGreen = 0, sumBlue = 0;
		int pixelCount = 0;

		for (int i = 0; i < SIZE; i++) {
			histogramR[i] = 0;
			histogramG[i] = 0;
			histogramB[i] = 0;
		}

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int r = ImageArray[x][y][1];
				int g = ImageArray[x][y][2];
				int b = ImageArray[x][y][3];
				histogramR[r]++;
				histogramG[g]++;
				histogramB[b]++;
				pixelCount++;
			}
		}

		for (int i = 0; i < SIZE; i++) {
			sumRed += histogramR[i] * i;
			sumGreen += histogramG[i] * i;
			sumBlue += histogramB[i] * i;
		}

		double redMean = sumRed / pixelCount;
		double greenMean = sumGreen / pixelCount;
		double blueMean = sumBlue / pixelCount;

		// print mean
		System.out.println("red mean: " + redMean);
		System.out.println("green mean: " + greenMean);
		System.out.println("blue mean: " + blueMean);

		System.out.println("--------------------------------------------");

		double varRsum = 0;
		double varGsum = 0;
		double varBsum = 0;

		double rSD = 0;
		double gSD = 0;
		double bSD = 0;

		// standard deviation formula
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				varRsum += Math.pow(ImageArray[x][y][1] - redMean, 2) / pixelCount;
				varGsum += Math.pow(ImageArray[x][y][2] - greenMean, 2) / pixelCount;
				varBsum += Math.pow(ImageArray[x][y][3] - blueMean, 2) / pixelCount;
 				rSD = Math.sqrt(varRsum);
				gSD = Math.sqrt(varGsum);
				bSD = Math.sqrt(varBsum);
			}
		}

		System.out.println("red standard deviation: " + rSD);
		System.out.println("green standard deviation: " + gSD);
		System.out.println("blue standard deviation: " + bSD);

		return convertToBimage(ImageArray);
	}

	private void clearConsole() {
		for (int i = 0; i < 30; i++)
			System.out.println("");
	}

	public BufferedImage simpleThresholding(BufferedImage timg) {
		int width = timg.getWidth();
		int height = timg.getHeight();

		int[][][] ImageArray = convertToArray(timg);

		int threshold = 50;// change from 0-255

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int meanRGB = (ImageArray[x][y][1] + ImageArray[x][y][2] + ImageArray[x][y][3]) / 3;

				int greyscale = meanRGB;

				if (greyscale > threshold) {
					greyscale = 255;
				} else {
					greyscale = 0;
				}

				ImageArray[x][y][1] = greyscale;
				ImageArray[x][y][2] = greyscale;
				ImageArray[x][y][3] = greyscale;
			}
		}
		return convertToBimage(ImageArray);
	}

	private BufferedImage automatedThresh(BufferedImage timg) {
		int[][][] ImageArray = convertToArray(timg);
		int height = timg.getHeight();
		int width = timg.getWidth();

		int meanBackgroundR = 0, meanBackgroundG = 0, meanBackgroundB = 0;
		int meanObjectR = 0, meanObjectG = 0, meanObjectB = 0;
		int r = 0, g = 0, b = 0;

		int CORNER = 4;

		// get mean background
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				r = ImageArray[x][y][1];
				g = ImageArray[x][y][2];
				b = ImageArray[x][y][3];

				meanBackgroundR += r;
				meanBackgroundG += g;
				meanBackgroundB += b;

			}
		}

		// get mean object
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				r = ImageArray[x][y][1];
				g = ImageArray[x][y][2];
				b = ImageArray[x][y][3];

				meanObjectR += r;
				meanObjectG += g;
				meanObjectB += b;
			}
		}

		// find pixel count
		int pixelCount = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixelCount++;
			}
		}

		// background mean
		meanBackgroundR = meanBackgroundR / CORNER;
		meanBackgroundG = meanBackgroundG / CORNER;
		meanBackgroundB = meanBackgroundB / CORNER;

		// object mean
		meanObjectR = meanObjectR / ((pixelCount));
		meanObjectG = meanObjectG / ((pixelCount));
		meanObjectB = meanObjectB / ((pixelCount));

		// total mean
		int totalMeanRed = (meanBackgroundR + meanObjectR) / 2;
		int totalMeanGreen = (meanBackgroundG + meanObjectG) / 2;
		int totalMeanBlue = (meanBackgroundB + meanObjectB) / 2;

		// get RGB threshold
		int getRedThresh = findThresholdRGB(0, 0, meanObjectR, meanBackgroundR, ImageArray, r, 0, totalMeanRed, height,
				width, 1);
		int getGreenThresh = findThresholdRGB(0, 0, meanObjectG, meanBackgroundG, ImageArray, b, 0, totalMeanGreen,
				height, width, 1);
		int getBlueThresh = findThresholdRGB(0, 0, meanObjectB, meanBackgroundB, ImageArray, g, 0, totalMeanBlue,
				height, width, 1);

		// apply threshold to image
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				r = ImageArray[x][y][1];
				g = ImageArray[x][y][2];
				b = ImageArray[x][y][3];

				if (r >= getRedThresh) {
					r = 255;
				} else if (r < getRedThresh) {
					r = 0;
				}
				if (g >= getGreenThresh) {
					g = 255;
				} else if (g < getGreenThresh) {
					g = 0;
				}
				if (b >= getBlueThresh) {
					b = 255;
				} else if (b < getBlueThresh) {
					b = 0;
				}

				ImageArray[x][y][1] = boundChecker(r);
				ImageArray[x][y][2] = boundChecker(g);
				ImageArray[x][y][3] = boundChecker(b);
			}

		}
		return convertToBimage(ImageArray);

	}

	private static int findThresholdRGB(int countBackRGB, int countObjRGB, int meanObjectRGB, int meanBackgroundRGB,
			int ImageArray[][][], int RGB, int total0RGB, int totalMeanRGB, int height, int width, int total1RGB) {

		for (;;) {
			meanObjectRGB = 0;
			meanBackgroundRGB = 0;
			countObjRGB = 0;
			countBackRGB = 0;
			total0RGB = totalMeanRGB;
			int x = 0;
			while (x < height) {
				int y = 0;
				while (y < width) {
					RGB = ImageArray[x][y][1];
					if (RGB >= total0RGB) {
						meanObjectRGB += RGB;
						countObjRGB += 1;
					} else if (RGB < total0RGB) {
						meanBackgroundRGB += RGB;
						countBackRGB += 1;
					}
					y++;
				}
				x++;
			}
			if (meanBackgroundRGB > 0) {
				meanBackgroundRGB = meanBackgroundRGB / countBackRGB;
			}
			if (meanObjectRGB > 0) {
				meanObjectRGB = meanObjectRGB / countObjRGB;
			}

			totalMeanRGB = (meanBackgroundRGB + meanObjectRGB) / 2;

			if (Math.abs(totalMeanRGB - total0RGB) < total1RGB) {
				break;

			}
		}
		return totalMeanRGB;
	}

	// ###########################################################//
	// ----------------------Extra - ROI add on-------------------//
	// ###########################################################//

	private void mergeROI() {
		try {
			BufferedImage image = this.ROIJoin(this.undoImage.get(this.undoImage.size() - 1));
			Graphics big = image.getGraphics();
			big.drawImage(image, 0, 0, null);
			this.biFiltered = this.bi = image;
			this.repaint();
		} catch (Exception e) {
		}

	}

	private BufferedImage ROIJoin(BufferedImage timg) {
		int width1 = timg.getWidth();
		int height1 = timg.getHeight();

		int[][][] cameraman = convertToArray(timg);
		int[][][] MASK = new int[width1][height1][4];
		System.out.println("Press 1 for ROIadd, Press 2 for ROImulitply");
		Scanner sc = new Scanner(System.in);
		int scan = sc.nextInt();

		int rangeLength = 125;
		int rangeHeight = 125;
		int spaceBound = 50;
		if (scan == 1) {
			System.out.println("ROIadd");
			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					if (!(x >= spaceBound && x <= rangeLength && y >= spaceBound && y <= rangeHeight)) {
						MASK[x][y][0] = 255;
						MASK[x][y][1] = 0;
						MASK[x][y][2] = 0;
						MASK[x][y][3] = 0;
					} else {
						MASK[x][y][0] = 255;
						MASK[x][y][1] = 255;
						MASK[x][y][2] = 255;
						MASK[x][y][3] = 255;
					}
				}
			}

			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					cameraman[x][y][0] = cameraman[x][y][0] & MASK[x][y][0];
					cameraman[x][y][1] = cameraman[x][y][1] & MASK[x][y][1];
					cameraman[x][y][2] = cameraman[x][y][2] & MASK[x][y][2];
					cameraman[x][y][3] = cameraman[x][y][3] & MASK[x][y][3];
				}
			}
		} else if (scan == 2) {
			System.out.println("ROImultiply");
			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					if (!(x >= spaceBound && x <= rangeLength && y >= spaceBound && y <= rangeHeight)) {
						MASK[x][y][0] = 255;
						MASK[x][y][1] = 0;
						MASK[x][y][2] = 0;
						MASK[x][y][3] = 0;
					} else {
						MASK[x][y][0] = 1;
						MASK[x][y][1] = 1;
						MASK[x][y][2] = 1;
						MASK[x][y][3] = 1;
					}
				}
			}

			for (int y = 0; y < height1; y++) {
				for (int x = 0; x < width1; x++) {
					cameraman[x][y][0] = cameraman[x][y][0] * MASK[x][y][0];
					cameraman[x][y][1] = cameraman[x][y][1] * MASK[x][y][1];
					cameraman[x][y][2] = cameraman[x][y][2] * MASK[x][y][2];
					cameraman[x][y][3] = cameraman[x][y][3] * MASK[x][y][3];
				}
			}
		} else {
			System.err.println("Error");
		}
		return convertToBimage(cameraman);
	}

}