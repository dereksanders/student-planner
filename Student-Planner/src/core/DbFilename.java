package core;

public class DbFilename {

	private static final String DB_EXTENSION = ".db";
	private static final String VERSION_EXTENSION = ".v";

	private String[] components;

	public DbFilename(String filename) {

		if (!filename.contains(".")) {

			// Only the db's name has been provided. Extensions must be added.
			filename += VERSION_EXTENSION + Main.CURRENT_VERSION + DB_EXTENSION;
		}

		String[] components = filename.split("\\.");

		if (isLegal(components)) {
			this.components = components;
		}
	}

	private static boolean isLegal(String[] components) {

		boolean isLegal = true;

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

					if (!isLegalVersion(dbVersion)) {

						throw new DbFilenameException("Invalid db version.");
					}
				}

				if (!components[2].equals(DB_EXTENSION.substring(1))) {

					throw new DbFilenameException("Invalid db extension.");

				}
			}

		} catch (DbFilenameException e) {

			isLegal = false;
		}

		return isLegal;
	}

	private static boolean isLegalVersion(String dbVersion) {

		boolean isLegal = true;

		try {

			Integer.parseInt(dbVersion);

		} catch (NumberFormatException e) {

			isLegal = false;
		}

		return isLegal;
	}

	public String getNamePrefix() {

		return this.components[0];
	}

	public int getVersion() {

		return Integer.parseInt(
				this.components[1].substring(VERSION_EXTENSION.length() - 1));
	}

	private String getFilename() {

		String filename = "";

		for (String c : this.components) {

			filename += (c + ".");
		}

		filename = filename.substring(0, filename.lastIndexOf("."));

		return filename;
	}

	@Override
	public String toString() {

		return this.getFilename();
	}
}
