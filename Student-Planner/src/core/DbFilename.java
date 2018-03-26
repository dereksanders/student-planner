package core;

/**
 * The Class DbFilename.
 * 
 * Database filenames will contain a name prefix (the name of the profile), a
 * version, and a database extension.
 */
public class DbFilename {

	private static final String DB_EXTENSION = ".db";
	private static final String VERSION_EXTENSION = ".v";

	private String[] components;

	/**
	 * Instantiates a new db filename.
	 *
	 * @param filename
	 *            the filename
	 */
	public DbFilename(String filename) {

		if (!filename.contains(".")) {

			// Only the db's name has been provided. Extensions must be added.
			filename += VERSION_EXTENSION + Main.CURRENT_VERSION + DB_EXTENSION;
		}

		String[] components = filename.split("\\.");

		if (isValid(components)) {
			this.components = components;
		}
	}

	/**
	 * Validates the components of the filename (the substrings separated by
	 * '.').
	 *
	 * @param components
	 *            the components of the filename
	 * @return true, if valid
	 */
	private static boolean isValid(String[] components) {

		boolean isValid = true;

		try {

			if (components.length != 3) {

				throw new DbFilenameException("Invalid file format.");

			} else {

				// Check version extension.
				if (components[1].charAt(0) != VERSION_EXTENSION.charAt(1)) {

					throw new DbFilenameException("Invalid file format.");

				} else {

					// Check version.
					String dbVersion = components[1].substring(1);

					if (!isValidVersion(dbVersion)) {

						throw new DbFilenameException("Invalid db version.");
					}
				}

				if (!components[2].equals(DB_EXTENSION.substring(1))) {

					throw new DbFilenameException("Invalid db extension.");

				}
			}

		} catch (DbFilenameException e) {

			isValid = false;
		}

		return isValid;
	}

	/**
	 * Validates the version.
	 *
	 * @param dbVersion
	 *            the db version
	 * @return true, if is legal version
	 */
	private static boolean isValidVersion(String dbVersion) {

		boolean isValid = true;

		try {

			Integer.parseInt(dbVersion);

		} catch (NumberFormatException e) {

			isValid = false;
		}

		return isValid;
	}

	/**
	 * Gets the name prefix.
	 *
	 * @return the name prefix
	 */
	public String getNamePrefix() {

		return this.components[0];
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public int getVersion() {

		return Integer.parseInt(
				this.components[1].substring(VERSION_EXTENSION.length() - 1));
	}

	/**
	 * Gets the filename (sum of filename components).
	 *
	 * @return the filename
	 */
	private String getFilename() {

		String filename = "";

		for (String c : this.components) {

			filename += (c + ".");
		}

		filename = filename.substring(0, filename.lastIndexOf("."));

		return filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return this.getFilename();
	}
}
