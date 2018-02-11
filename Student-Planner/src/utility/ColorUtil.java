package utility;

import javafx.scene.paint.Color;

public class ColorUtil {

	/**
	 * Color to hex.
	 *
	 * @param color
	 *            the color
	 * @return the string
	 */
	public static String colorToHex(Color color) {
		String hex1;
		String hex2;

		hex1 = Integer.toHexString(color.hashCode()).toUpperCase();

		switch (hex1.length()) {
		case 2:
			hex2 = "000000";
			break;
		case 3:
			hex2 = String.format("00000%s", hex1.substring(0, 1));
			break;
		case 4:
			hex2 = String.format("0000%s", hex1.substring(0, 2));
			break;
		case 5:
			hex2 = String.format("000%s", hex1.substring(0, 3));
			break;
		case 6:
			hex2 = String.format("00%s", hex1.substring(0, 4));
			break;
		case 7:
			hex2 = String.format("0%s", hex1.substring(0, 5));
			break;
		default:
			hex2 = hex1.substring(0, 6);
		}
		return hex2;
	}

	public static Color randomColor() {

		int lowerInt = 0;
		int upperInt = 9;

		int lowerAlpha = 0;
		int upperAlpha = 5;

		int hexLength = 6;

		StringBuilder color = new StringBuilder(8);
		color.append("0x");

		for (int i = 0; i < hexLength; i++) {

			if (Math.random() < 0.5) {

				// choose an int
				int randInt = (int) (((upperInt - lowerInt) * Math.random()) + lowerInt);

				color.append(randInt);

			} else {

				// choose a letter - 97 is ASCII value for 'a'
				int randAlpha = ((int) (((upperAlpha - lowerAlpha) * Math.random()) + lowerAlpha)) + 97;

				color.append((char) randAlpha);
			}
		}

		return Color.web(color.toString());
	}
}
