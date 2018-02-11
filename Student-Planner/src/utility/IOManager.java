package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * The Class IOManager.
 * 
 * @author Derek Sanders
 */

public class IOManager {

	/** The max rows. */
	private static int MAX_ROWS = 200000;

	/**
	 * Write file.
	 *
	 * @param text
	 *            the text
	 * @param filename
	 *            the filename
	 */
	public static void writeFile(String text, String filename) {

		try {

			Files.write(Paths.get("./" + filename), text.getBytes());

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Append to file.
	 *
	 * @param text
	 *            the text
	 * @param filename
	 *            the filename
	 */
	public static void appendToFile(String text, String filename) {

		try {

			Files.write(Paths.get("./" + filename), text.getBytes(), StandardOpenOption.APPEND);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * Load file.
	 *
	 * @param filename
	 *            the filename
	 * @return the array of keys
	 */
	public static String[] loadFile(String filename) {

		String line;
		int lineCount = 0;
		String[] textArray = new String[MAX_ROWS];

		Charset charset = Charset.forName("UTF-8");
		Path file = Paths.get(filename);

		try (BufferedReader reader = Files.newBufferedReader(file, charset)) {

			do {

				line = reader.readLine();

				if (line != null) {

					textArray[lineCount] = line;
					lineCount++;
				}

			} while (line != null);

		} catch (IOException err) {

			err.printStackTrace();
			return null;
		}

		String[] rawData = new String[lineCount];

		int i = 0;

		while (i < lineCount) {

			rawData[i] = textArray[i];
			i++;
		}

		return rawData;
	}

	/**
	 * Creates the directory.
	 *
	 * @param directory
	 *            the directory
	 */
	public static void createDirectory(String directory) {
		if (!fileExists(directory)) {
			try {
				Files.createDirectory(Paths.get(directory));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Removes the directory.
	 *
	 * @param directory
	 *            the directory
	 */
	public static void removeDirectory(String directory) {
		deleteFile(directory);
	}

	/**
	 * Delete file.
	 *
	 * @param filename
	 *            the filename
	 */
	public static void deleteFile(String filename) {

		Path file = Paths.get(filename);

		if (fileExists(file.toString())) {

			try {
				Files.delete(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void deleteFiles(File[] files) {

		for (File f : files) {

			deleteFile(f.getPath());
		}
	}

	/**
	 * Save object.
	 *
	 * @param o
	 *            the o
	 * @param dir
	 *            the dir
	 */
	public static void saveObject(Object o, String dir) {

		// Create save directory if it does not already exist.
		if (!Files.exists(Paths.get(dir))) {
			try {
				Files.createDirectory(Paths.get(dir));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			File f = new File(dir + "//" + o.toString());
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save object.
	 *
	 * @param o
	 *            the o
	 * @param dir
	 *            the dir
	 * @param extension
	 *            the extension
	 */
	public static void saveObject(Object o, String dir, String extension) {

		// Create save directory if it does not already exist.
		if (!Files.exists(Paths.get(dir))) {
			try {
				Files.createDirectory(Paths.get(dir));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			File f = new File(dir + "//" + o.toString() + extension);
			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(o);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load object.
	 *
	 * @param saveFile
	 *            the save file
	 * @return the object
	 */
	public static Object loadObject(File saveFile) {

		Object o = null;

		try {
			FileInputStream fis = new FileInputStream(saveFile);
			ObjectInputStream ois = new ObjectInputStream(fis);
			o = ois.readObject();
			ois.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		return o;
	}

	/**
	 * Gets the last modified file.
	 *
	 * @param directory
	 *            the directory
	 * @return the last modified file
	 */
	public static File getLastModifiedFile(String directory) {

		File dir = new File(directory);
		File[] files = findAllFiles(dir);

		if (files.length == 0) {

			System.out.println("No files found in directory. Returning null.");
			return null;

		} else {

			File lastModified = files[0];

			if (files.length > 1) {
				for (int i = 1; i < files.length; i++) {
					if (files[i].lastModified() < lastModified.lastModified()) {

						lastModified = files[i];
					}
				}
			}
			return lastModified;
		}
	}

	/**
	 * Gets the last modified file with the specified extension.
	 *
	 * @param directory
	 *            the directory
	 * @param extension
	 *            the extension
	 * @return the last modified file
	 */
	public static File getLastModifiedFile(String directory, String extension) {

		File dir = new File(directory);
		File[] files = findAllFiles(dir, extension);

		if (files.length == 0) {

			System.out.println("No files found in directory. Returning null.");
			return null;

		} else {

			File lastModified = files[0];

			if (files.length > 1) {
				for (int i = 1; i < files.length; i++) {
					if (files[i].lastModified() < lastModified.lastModified()) {

						lastModified = files[i];
					}
				}
			}
			return lastModified;
		}
	}

	/**
	 * Find all files.
	 *
	 * @param dir
	 *            the dir
	 * @return the file[]
	 */
	private static File[] findAllFiles(File dir) {

		if (!dir.exists()) {
			createDirectory(dir.getPath());
		}

		File[] files = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.isFile();
			}
		});

		return files;
	}

	/**
	 * Find all files with the specified extension.
	 *
	 * @param dir
	 *            the dir
	 * @param extension
	 *            the extension
	 * @return the file[]
	 */
	private static File[] findAllFiles(File dir, String extension) {

		if (!dir.exists()) {
			createDirectory(dir.getPath());
		}

		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f) {

				String path = f.getPath();
				return path.endsWith(extension);
			}
		});

		return files;
	}

	public static File[] findFiles(File dir, String prefix, String suffix) {

		File[] found = null;

		if (dir.exists()) {

			found = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File f) {

					String path = f.getPath();
					return (path.startsWith(prefix) && path.endsWith(suffix));
				}
			});
		}

		return found;
	}

	public static File[] findFiles(File dir, String prefix) {

		File[] found = null;

		if (dir.exists()) {

			found = dir.listFiles(new FileFilter() {

				@Override
				public boolean accept(File f) {

					String path = f.getPath();
					return (path.startsWith(prefix));
				}
			});
		}

		return found;
	}

	/**
	 * File exists.
	 *
	 * @param path
	 *            the path
	 * @return true, if successful
	 */
	public static boolean fileExists(String path) {

		return new File(path).exists();
	}
}