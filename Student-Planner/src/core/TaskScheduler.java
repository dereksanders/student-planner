package core;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import sqlite.SqliteWrapperException;

public class TaskScheduler {

	private volatile static TaskScheduler uniqueInstance;
	private static LocalDateTime last;

	public Timer update;

	private TaskScheduler(LocalDateTime first) {

		TaskScheduler.last = first;

		TimerTask updateCurrentTime = new TimerTask() {

			@Override
			public void run() {

				if (Main.active != null) {

					try {

						Main.active.update();

					} catch (SqliteWrapperException | SQLException e) {

						e.printStackTrace();
					}
				}

				TaskScheduler.last = LocalDateTime.now();
			}
		};

		update = new Timer("update");

		// Tick every second.
		update.scheduleAtFixedRate(updateCurrentTime, 0, 1000);
	}

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
