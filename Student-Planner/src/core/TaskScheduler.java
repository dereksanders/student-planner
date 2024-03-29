package core;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import sqlite.SqliteWrapperException;

/**
 * The Class TaskScheduler.
 */
public class TaskScheduler {

	private volatile static TaskScheduler uniqueInstance;
	private static LocalDateTime last;

	public Timer update;

	/**
	 * Instantiates a new task scheduler.
	 *
	 * @param first
	 *            the first
	 */
	private TaskScheduler(LocalDateTime first) {

		TaskScheduler.last = first;

		TimerTask updateCurrentTime = new TimerTask() {

			@Override
			public void run() {

				LocalDateTime now = LocalDateTime.now();

				if (Main.active != null) {

					if (TaskScheduler.last.getMinute() != now.getMinute()) {

						System.out.println("New minute..");

						// Check for meetings & events upcoming within set
						// threshold.

						// Check for meetings & events that have now passed.
						try {
							Main.active.update();
						} catch (SqliteWrapperException | SQLException e) {
							e.printStackTrace();
						}
					}
				}

				TaskScheduler.last = now;
			}
		};

		update = new Timer("update");

		// Tick every second.
		update.scheduleAtFixedRate(updateCurrentTime, 0, 1000);
	}

	/**
	 * Gets the single instance of TaskScheduler.
	 *
	 * @param first
	 *            the first
	 * @return single instance of TaskScheduler
	 */
	public static TaskScheduler getInstance(LocalDateTime first) {
		if (uniqueInstance == null) {
			synchronized (TaskScheduler.class) {
				if (uniqueInstance == null) {
					uniqueInstance = new TaskScheduler(first);
				}
			}
		}
		return uniqueInstance;
	}
}
