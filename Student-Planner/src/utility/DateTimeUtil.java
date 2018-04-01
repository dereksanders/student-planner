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
}
