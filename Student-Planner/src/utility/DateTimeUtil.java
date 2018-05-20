package utility;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * The Class DateTimeUtil.
 */
public class DateTimeUtil {

	/**
	 * Converts an integer representing a day of the week (1 = Monday, 7 =
	 * Sunday) to the String equivalent according to the ISO-8601 standard.
	 *
	 * Will return an empty String if the integer is outside of these bounds.
	 *
	 * @param dayAsInt
	 *            the day as int
	 * @return the string
	 */
	public static String intToDay(int dayAsInt) {

		String day = "";

		switch (dayAsInt) {

		case 1:
			day = "Monday";
			break;
		case 2:
			day = "Tuesday";
			break;
		case 3:
			day = "Wednesday";
			break;
		case 4:
			day = "Thursday";
			break;
		case 5:
			day = "Friday";
			break;
		case 6:
			day = "Saturday";
			break;
		case 7:
			day = "Sunday";
			break;
		}

		return day;
	}

	/**
	 * Checks if the two dates belong to the same week. Weeks begin on Monday
	 * according to the ISO-8601 standard.
	 *
	 * @param date1
	 *            the date 1
	 * @param date2
	 *            the date 2
	 * @return true, if is same week
	 */
	public static boolean isSameWeek(LocalDate date1, LocalDate date2) {

		if (getStartOfWeek(date1).equals(getStartOfWeek(date2))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the start of the week, i.e. the Monday.
	 *
	 * @param date
	 *            the date
	 * @return the start of the week
	 */
	public static LocalDate getStartOfWeek(LocalDate date) {

		int dayOfWeek = date.getDayOfWeek().getValue();

		return date.minusDays(dayOfWeek - 1);
	}

	/**
	 * Round to nearest half hour.
	 *
	 * @param time
	 *            the time
	 * @return the local time
	 */
	public static LocalTime roundToNearestHalfHour(LocalTime time) {

		LocalTime rounded = time;

		if (time.getMinute() < 15) {

			rounded = LocalTime.of(time.getHour(), 0);

		} else if (time.getMinute() > 45) {

			if (time.getHour() < 23) {

				rounded = LocalTime.of(time.getHour() + 1, 0);

			} else {

				rounded = LocalTime.of(time.getHour(), 30);
			}

		} else {

			rounded = LocalTime.of(time.getHour(), 30);
		}

		return rounded;
	}

	/**
	 * Round to next half hour.
	 *
	 * @param time
	 *            the time
	 * @return the local time
	 */
	public static LocalTime roundToNextHalfHour(LocalTime time) {

		LocalTime rounded = LocalTime.of(time.getHour(), time.getMinute());

		if (rounded.getMinute() <= 30) {

			rounded = LocalTime.of(time.getHour(), 30);

		} else {

			if (time.getHour() < 23) {

				rounded = LocalTime.of(time.getHour() + 1, 0);
			} else {

				rounded = LocalTime.of(time.getHour(), 59);
			}
		}

		return rounded;
	}

	/**
	 * Round to next half hour.
	 *
	 * @param time
	 *            the time
	 * @return the local time
	 */
	public static LocalTime roundToNextHalfHourThatIsAtLeastHalfAnHourFrom(
			LocalTime time) {

		LocalTime rounded = LocalTime.of(time.getHour(), time.getMinute());

		if (rounded.getMinute() == 0) {

			rounded = LocalTime.of(time.getHour(), 30);

		} else {

			if (time.getHour() < 23) {

				rounded = LocalTime.of(time.getHour() + 1, 0);
			} else {

				rounded = LocalTime.of(time.getHour(), 59);
			}
		}

		return rounded;
	}

	/**
	 * Round to previous half hour that is at least half an hour ago.
	 *
	 * @param time
	 *            the time
	 * @return the local time
	 */
	public static LocalTime roundToPrevHalfHourThatIsAtLeastHalfAnHourAgo(
			LocalTime time) {

		LocalTime rounded = LocalTime.of(time.getHour(), time.getMinute());

		if (rounded.getMinute() < 30) {

			if (time.getHour() > 0) {

				rounded = LocalTime.of(time.getHour() - 1, 30);

			} else {

				rounded = LocalTime.of(time.getHour(), 0);
			}

		} else {

			rounded = LocalTime.of(time.getHour(), 0);
		}

		return rounded;
	}

	/**
	 * Round to previous half hour.
	 *
	 * @param time
	 *            the time
	 * @return the local time
	 */
	public static LocalTime roundToPrevHalfHour(LocalTime time) {

		LocalTime rounded = LocalTime.of(time.getHour(), time.getMinute());

		if (rounded.getMinute() <= 30) {

			rounded = LocalTime.of(time.getHour(), 0);

		} else {

			rounded = LocalTime.of(time.getHour(), 30);
		}

		return rounded;
	}

	/**
	 * Gets the minutes between the two times provided.
	 *
	 * @param start
	 *            the start
	 * @param end
	 *            the end
	 * @return the minutes between
	 */
	public static int getMinutesBetween(LocalTime start, LocalTime end) {

		int startMinutes = start.toSecondOfDay() / 60;
		int endMinutes = end.toSecondOfDay() / 60;

		return endMinutes - startMinutes;
	}

	public static int getMonthsBetween(LocalDate start, LocalDate end) {

		int numMonths;

		int yearsApart = end.getYear() - start.getYear();
		int monthsApart = end.getMonthValue() - start.getMonthValue();

		numMonths = (yearsApart * 12) + monthsApart;

		return numMonths;
	}

	/**
	 * Checks if is valid local time.
	 *
	 * @param time
	 *            the time
	 * @return true, if is valid local time
	 */
	public static boolean isValidLocalTime(Object time) {

		boolean isValid = false;

		if (time instanceof LocalTime) {
			isValid = true;
		} else if (time instanceof String) {
			String[] components = ((String) time).split(":");
			if (components.length == 2) {
				try {
					LocalTime t = LocalTime.of(Integer.parseInt(components[0]),
							Integer.parseInt(components[1]));

					isValid = true;
				} catch (NumberFormatException e) {
				}
			}
		}

		return isValid;
	}

	/**
	 * Parses the local time.
	 * 
	 * Returns null if the local time is invalid.
	 *
	 * @param time
	 *            the time
	 * @return the local time
	 */
	public static LocalTime parseLocalTime(String time) {

		LocalTime parsed = null;

		if (isValidLocalTime(time)) {

			String[] components = ((String) time).split(":");
			parsed = LocalTime.of(Integer.parseInt(components[0]),
					Integer.parseInt(components[1]));
		}

		return parsed;
	}

	/**
	 * Local time as string.
	 *
	 * @param time
	 *            the time
	 * @return the string
	 */
	public static String localTimeAsString(LocalTime time) {

		String hour = "";
		if (time.getHour() < 10) {
			hour = "0" + time.getHour();
		} else {
			hour = "" + time.getHour();
		}

		String minute = "";
		if (time.getMinute() < 10) {
			minute = "0" + time.getMinute();
		} else {
			minute = "" + time.getMinute();
		}

		return hour + ":" + minute;
	}

	/**
	 * Generate times as strings.
	 *
	 * @return the string[]
	 */
	public static String[] generateTimesAsStrings(int timesPerHour) {

		LocalTime[] times = new LocalTime[24 * timesPerHour];
		String[] timesAsStrings = new String[times.length];

		for (int i = 0; i < times.length; i++) {

			for (int j = 0; j < 24; j++) {

				for (int k = 0; k < timesPerHour; k++) {

					times[i] = LocalTime.of(j, (60 / timesPerHour) * k);
					i++;
				}
			}
		}

		for (int i = 0; i < times.length; i++) {

			timesAsStrings[i] = DateTimeUtil.localTimeAsString(times[i]);
		}

		return timesAsStrings;
	}

	/**
	 * Short pretty date.
	 *
	 * @param date
	 *            the date
	 * @return the string
	 */
	public static String shortPrettyDate(LocalDate date) {

		String monthAbbreviation = getMonthShort(date.getMonthValue());
		String dateWithEnding = date.getDayOfMonth()
				+ getDateEnding(date.getDayOfMonth());

		return monthAbbreviation + " " + dateWithEnding;
	}

	/**
	 * Gets the date ending.
	 *
	 * @param dayOfMonth
	 *            the day of month
	 * @return the date ending
	 */
	private static String getDateEnding(int dayOfMonth) {

		String ending = "";

		String dayOfMonthAsString = Integer.toString(dayOfMonth);

		int lastDigit = Integer.parseInt(
				dayOfMonthAsString.substring(dayOfMonthAsString.length() - 1));

		switch (lastDigit) {

		case 1:
			ending = "st";
			break;
		case 2:
			ending = "nd";
			break;
		case 3:
			ending = "rd";
			break;
		default:
			ending = "th";
			break;
		}

		return ending;
	}

	/**
	 * Gets the month abbreviation.
	 *
	 * @param monthValue
	 *            the month value
	 * @return the month abbreviation
	 */
	private static String getMonthShort(int monthValue) {

		String abbreviation = "";

		switch (monthValue) {

		case 1:
			abbreviation = "Jan.";
			break;
		case 2:
			abbreviation = "Feb.";
			break;
		case 3:
			abbreviation = "Mar.";
			break;
		case 4:
			abbreviation = "Apr.";
			break;
		case 5:
			abbreviation = "May";
			break;
		case 6:
			abbreviation = "June";
			break;
		case 7:
			abbreviation = "July";
			break;
		case 8:
			abbreviation = "Aug.";
			break;
		case 9:
			abbreviation = "Sept.";
			break;
		case 10:
			abbreviation = "Oct.";
			break;
		case 11:
			abbreviation = "Nov.";
			break;
		case 12:
			abbreviation = "Dec.";
			break;
		}

		return abbreviation;
	}

	public static String getMonth(int monthValue) {

		String month = "";

		switch (monthValue) {

		case 1:
			month = "January";
			break;
		case 2:
			month = "February";
			break;
		case 3:
			month = "March";
			break;
		case 4:
			month = "April";
			break;
		case 5:
			month = "May";
			break;
		case 6:
			month = "June";
			break;
		case 7:
			month = "July";
			break;
		case 8:
			month = "August";
			break;
		case 9:
			month = "September";
			break;
		case 10:
			month = "October";
			break;
		case 11:
			month = "November";
			break;
		case 12:
			month = "December";
			break;
		}

		return month;
	}
}
