package com.noamwolf.android.fitcompanion.stats;

import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.noamwolf.android.fitcompanion.model.Activity;

public class ActivityStatsHelper {

	/**
	 * Looks for the pattern "[rolls]:" in the string and returns the value as an integer.
	 */
	public static int getRollsFromDescription(String description) {
		if (Strings.isNullOrEmpty(description)) {
			return 0;
		}
		Integer rolls = null;
		String pattern = "[rolls]:";
		Pattern p = Pattern.compile(pattern);
		String[] split = p.split(description.toLowerCase().trim());
		if (split.length == 2) {
			rolls = Ints.tryParse(split[1].trim());
		}
		return rolls == null ? 0 : rolls;
	}

	public static int getSubmissionsFromDescription(String description) {
		if (Strings.isNullOrEmpty(description)) {
			return 0;
		}
		Integer rolls = null;
		String pattern = "[subs]:";
		Pattern p = Pattern.compile(pattern);
		String[] split = p.split(description.toLowerCase().trim());
		if (split.length == 2) {
			rolls = Ints.tryParse(split[1].trim());
		}
		return rolls == null ? 0 : rolls;
	}

	public static int getTapsFromDescription(String description) {
		if (Strings.isNullOrEmpty(description)) {
			return 0;
		}
		Integer rolls = null;
		String pattern = "[taps]:";
		Pattern p = Pattern.compile(pattern);
		String[] split = p.split(description.toLowerCase().trim());
		if (split.length == 2) {
			rolls = Ints.tryParse(split[1].trim());
		}
		return rolls == null ? 0 : rolls;
	}
	
	public boolean isPrivateSession(Activity session) {
		if (session != null) {
			String pattern = "private";
			String name = session.getName().toLowerCase();
			if (name.contains(pattern)) {
				return true;
			} else {
				String description = session.getDescription().toLowerCase();
				if (description.contains(pattern)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isCoachSession(Activity session) {
		if (session != null) {
			String pattern = "coach";
			String name = session.getName().toLowerCase();
			if (name.contains(pattern)) {
				return true;
			} else {
				String description = session.getDescription().toLowerCase();
				if (description.contains(pattern)) {
					return true;
				}
			}
		}
		return false;
	}

	public String createCsvForMonth(MonthlyStats stats) {
		StringBuilder sb = new StringBuilder();
		
		// Month
		sb.append(stats.getMonth() + ",");
		// Count
		sb.append(stats.getTotalCount() + ",");
		
		// Duration
		sb.append(Activity.getFormattedDuration(stats.getDuration()) + ",");
		
		// Rolls
		sb.append(stats.getRolls());
		return sb.toString();
	}

	public MonthlyStats calculateYearlyStats(Iterable<Activity> bjjSessions) {
		MonthlyStats stats = new MonthlyStats();
		Map<String, Integer> dayHistogram = Maps.newLinkedHashMap();
		int countGi = 0;
		int countNoGi = 0;
		int countOpenMat = 0;
		int countPrivates = 0;
		int countRolls = 0;
		int countKandP = 0;
		int countTotal = 0;
		int countCoach = 0;
		long totalDuration = 0;

		for (Activity session : bjjSessions) {
			countTotal++;
			totalDuration += session.getDurationMillis();
			// Day histogram
			String dayPart = session.getActivityTimeStamp().substring(0, 3);
			if (!dayHistogram.containsKey(dayPart)) {
				dayHistogram.put(dayPart, 0);
			}
			int tmpCount = dayHistogram.get(dayPart);
			dayHistogram.put(dayPart, ++tmpCount);

			// Gi/No-gi/OpenMat
			String name = session.getName().toLowerCase().trim();
			if (name.contains("no-gi")
					|| name.contains("nogi")
					|| name.contains("google")) {
				countNoGi++;
			} else if (name.contains("open")) {
				countOpenMat++;
			} else if (name.contains("families")
					|| name.contains("family")
					|| name.contains("kids")
					|| name.contains("parents")) {
				countKandP++;
			} else {
				countGi++;
			}

			// Parse "Coach"
			countCoach += isCoachSession(session) ? 1: 0;

			// Parse "Private"
			countPrivates += isPrivateSession(session) ? 1 : 0;

			// Parse "Rolls:{d}
			countRolls += getRollsFromDescription(session.getDescription());
		}
//		Day histogram
//		for (Entry<String, Integer> entry : dayHistogram.entrySet()) {
//			sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
//		}

		stats.setCountGi(countGi);
		stats.setCountNoGi(countNoGi);
		stats.setCountOpenMat(countOpenMat);
		stats.setCountPrivates(countPrivates);
		stats.setCountKidsParents(countKandP);
		stats.setCountCoach(countCoach);
		stats.setRolls(countRolls);
		stats.setTotalCount(countTotal);
		stats.setDuration(totalDuration);

		return stats;
	}
	
	/**
	 * Returns a string representing totals for the activities passed in.
	 * 1. Day Histogram
	 * 2. Gi & No gi breakdown.
	 * 3. Number of [rolls]:.
	 */
	public String printBjjStats(Iterable<Activity> bjjSessions) {
		StringBuilder sb = new StringBuilder();
		Map<String, Integer> dayHistogram = Maps.newLinkedHashMap();
		int countGi = 0;
		int countNoGi = 0;
		int countOpenMat = 0;
		int countPrivates = 0;
		int countRolls = 0;
		int countKandP = 0;
		int countTotal = 0;
		long totalDuration = 0;

		for (Activity session : bjjSessions) {
			countTotal++;
			totalDuration += session.getDurationMillis();
			// Day histogram
			String dayPart = session.getActivityTimeStamp().substring(0, 3);
			if (!dayHistogram.containsKey(dayPart)) {
				dayHistogram.put(dayPart, 0);
			}
			int tmpCount = dayHistogram.get(dayPart);
			dayHistogram.put(dayPart, ++tmpCount);

			// Gi/No-gi/OpenMat
			String name = session.getName().toLowerCase().trim();
			if (name.contains("no-gi")
					|| name.contains("nogi")
					|| name.contains("google")) {
				countNoGi++;
			} else if (name.contains("open")) {
				countOpenMat++;
			} else if (name.contains("families")
					|| name.contains("family")
					|| name.contains("kids")
					|| name.contains("parents")) {
				countKandP++;
			} else {
				countGi++;
			}

			// Parse "Private"
			countPrivates += isPrivateSession(session) ? 1 : 0;

			// Parse "Rolls:{d}
			countRolls += getRollsFromDescription(session.getDescription());
		}
		sb.append("Day histogram \n");
		sb.append("============= \n");
		for (Entry<String, Integer> entry : dayHistogram.entrySet()) {
			sb.append(entry.getKey() + ": " + entry.getValue() + "\n");
		}
		sb.append("\n");
		sb.append("Gi/No-gi breakdown \n");
		sb.append("================== \n");
		sb.append("No-gi: " + countNoGi + "\n");
		sb.append("Gi: " + countGi + "\n");
		sb.append("Open Mat: " + countOpenMat + "\n");
		sb.append("Kids & Parents: " + countKandP + "\n");
		sb.append("Privates: " + countPrivates + "\n");

		sb.append("\n");
		sb.append("Rolls \n");
		sb.append("===== \n");
		sb.append("Rolls: " + countRolls + "\n");

		sb.append("\n");
		sb.append("Sessions \n");
		sb.append("======== \n");
		sb.append("Sessions: " + countTotal + "\n");
		sb.append("Duration: " +  Activity.getFormattedDuration(totalDuration) + "\n");

		return sb.toString();
	}
	
	/**
	 * Aggregate yearly totals by activity type.
	 *
	 */
	public String printActivityMonthyStats(MonthlyStats stats, boolean withDetail) {
		StringBuilder sb = new StringBuilder();
		sb.append("Month: " + stats.getMonth()).append("\n");
		sb.append("Totals for " + Activity.getTypeById(stats.getActivityTypeId()) + " (" + stats.getActivityTypeId() + ")").append("\n");
		sb.append("Count: " + stats.getTotalCount()).append("\n");
		
		if (stats.getActivityTypeId() == 44) {
			sb.append("Rolls: " + stats.getRolls()).append("\n");
		}
		System.out.println("Total duration: " + Activity.getFormattedDuration(stats.getDuration()));
		if (withDetail) {
			sb.append("*** Detail ***").append("\n");
			sb.append(stats.getDetails().toString()).append("\n");
		}
		return sb.toString();
	}

	/**
	 * TODO(nwolf): remove - unused.
	 */
    public MonthlyStats calculateMonthlyStats(Integer activityTypeIdKey, Integer month, Iterable<Activity> sessions) {
		MonthlyStats monthlyStats = new MonthlyStats(month, activityTypeIdKey, 1);
		long duration = 0;
		int rolls = 0;
		int count = 0;
		int countNoGi = 0;
		int countOpenMat = 0;
		int countKandP = 0;
		int countGi = 0;
		int countPrivates = 0;
		int countCoach = 0;
		for (Activity session : sessions) {
		    if (session.getActivityType() != activityTypeIdKey) continue;
		    count++;
			duration += session.getDurationMillis();
			rolls += getRollsFromDescription(session.getDescription());
			monthlyStats.getDetails().append(session).append("\n");

			String dayPart = session.getActivityTimeStamp().substring(0, 3);
			if (!monthlyStats.getDayHistogram().containsKey(dayPart)) {
				monthlyStats.getDayHistogram().put(dayPart, 0);
			}
			int tmpCount = monthlyStats.getDayHistogram().get(dayPart);
			monthlyStats.getDayHistogram().put(dayPart, ++tmpCount);

			// Gi/No-gi/OpenMat
			String name = session.getName().toLowerCase().trim();
			if (name.contains("no-gi")
					|| name.contains("nogi")
					|| name.contains("google")) {
				countNoGi++;
			} else if (name.contains("open")) {
				countOpenMat++;
			} else if (name.contains("families")
					|| name.contains("family")
					|| name.contains("kids")
					|| name.contains("parents")) {
				countKandP++;
			} else {
				countGi++;
			}

			// Parse "Coach"
			countCoach += isCoachSession(session) ? 1 : 0;
			// Parse "Private"
			countPrivates += isPrivateSession(session) ? 1 : 0;
		}
		monthlyStats.setCountGi(countGi);
		monthlyStats.setCountNoGi(countNoGi);
		monthlyStats.setCountOpenMat(countOpenMat);
		monthlyStats.setCountPrivates(countPrivates);
		monthlyStats.setDuration(duration);
		monthlyStats.setRolls(rolls);
		monthlyStats.setTotalCount(count);
		monthlyStats.setCountCoach(countCoach);
		return monthlyStats;
	}
}
