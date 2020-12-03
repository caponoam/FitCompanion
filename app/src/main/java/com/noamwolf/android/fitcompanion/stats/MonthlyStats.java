package com.noamwolf.android.fitcompanion.stats;

import com.google.common.collect.Maps;

import java.util.Map;

public class MonthlyStats {
	private int month;
	private String monthName;
	private int totalCount;
	private int rolls; //bjj only
	private StringBuilder details = new StringBuilder();
	private int activityTypeId;
	private long duration;

	private int countGi;
	private int countNoGi;
	private int countOpenMat;
	private int countPrivates;
	private int countKidsParents;
	private int countCoach;

	public int getCountCoach() {
		return countCoach;
	}

	public void setCountCoach(int countCoach) {
		this.countCoach = countCoach;
	}

	private Map<String, Integer> dayHistogram = Maps.newLinkedHashMap();

	public MonthlyStats() {}
	public MonthlyStats(Integer month, Integer activityTypeIdKey, int count) {
		this.month = month;
		this.setActivityTypeId(activityTypeIdKey);
		this.totalCount = count;
	}
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public String getMonthName() {
		return monthName;
	}
	public void setMonthName(String monthName) {
		this.monthName = monthName;
	}
	public int getTotalCount() {
		return totalCount;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getRolls() {
		return rolls;
	}
	public void setRolls(int rolls) {
		this.rolls = rolls;
	}
	public StringBuilder getDetails() {
		return details;
	}
	public void setDetails(StringBuilder details) {
		this.details = details;
	}
	public int getActivityTypeId() {
		return activityTypeId;
	}
	public void setActivityTypeId(int activityTypeId) {
		this.activityTypeId = activityTypeId;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public int getCountGi() {
		return countGi;
	}

	public void setCountGi(int countGi) {
		this.countGi = countGi;
	}
	public int getCountNoGi() {
		return countNoGi;
	}

	public void setCountNoGi(int countNoGi) {
		this.countNoGi = countNoGi;
	}

	public int getCountOpenMat() {
		return countOpenMat;
	}

	public void setCountOpenMat(int countOpenMat) {
		this.countOpenMat = countOpenMat;
	}

	public int getCountPrivates() {
		return countPrivates;
	}

	public void setCountPrivates(int countPrivates) {
		this.countPrivates = countPrivates;
	}

	public int getCountKidsParents() {
		return countKidsParents;
	}

	public void setCountKidsParents(int countKidsParents) {
		this.countKidsParents = countKidsParents;
	}

	public Map<String, Integer> getDayHistogram() {
		return dayHistogram;
	}

	public void setDayHistogram(Map<String, Integer> dayHistogram) {
		this.dayHistogram = dayHistogram;
	}
}
