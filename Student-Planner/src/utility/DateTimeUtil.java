package utility;

import java.time.LocalDate;
import java.time.LocalTime;

public class DateTimeUtil {

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

	public static boolean isSameWeek(LocalDate date1, LocalDate date2) {

		if (getStartOfWeek(date1).equals(getStartOfWeek(date2))) {
			return true;
		} else {
			return false;
		}
	}

	public static LocalDate getStartOfWeek(LocalDate date) {

		int dayOfWeek = date.getDayOfWeek().getValue();

		return date.minusDays(dayOfWeek - 1);
	}

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

	public static int getMinutesBetween(LocalTime earliestStart,
			LocalTime latestEnd) {

		int start = earliestStart.toSecondOfDay() / 60;
		int end = latestEnd.toSecondOfDay() / 60;

		return end - start;
	}
}
