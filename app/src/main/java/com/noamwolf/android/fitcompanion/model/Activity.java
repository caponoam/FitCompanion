package com.noamwolf.android.fitcompanion.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Objects;
//import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;

//@JsonIgnoreProperties
public class Activity {
	public static final Predicate<Activity> BJJ_PREDICATE = new Predicate<Activity>() {
		@Override
		public boolean apply(Activity activity) {
			return activity.getActivityType() == 44; // 44 --> Martial Arts.
		}
	};
	static final Map<Integer, String> ACTIVITY_TYPE_MAPPER = ImmutableMap.<Integer, String>builder()
			.put(1, "Biking")
			.put(4, "unknown")
			.put(7, "Walking")
			.put(8, "Running")
			.put(17, "Spinning")
			.put(21, "Calisthenitcs")
			.put(25, "Elliptical")
			.put(35, "Hiking")
			.put(40, "Kayaking")
			.put(42, "Kickboxing")
			.put(44, "Martial Arts")
			.put(46, "Mixed martial arts")
			.put(52, "Rock climbing")
			.put(62, "Skating")
			.put(72, "Sleeping")
			.put(82, "Swimming")
			.put(85, "Ping pong")
			.put(88, "Treadmill")
			.put(89, "Volleyball")
			.put(100, "Yoga")
			.build();
	
	public static String dateFormat = "E, dd MMM yyyy HH:mm";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
	
    // This field seems to be the date/time the entry was entered.
	private long modifiedTimeMillis;
	private long endTimeMillis;
	private String description = "";
	private int activityType;
	private long startTimeMillis;
	private String id;
	private String name = "";
	private Application application;
	
	public Activity() {
		this(0, 0, "", 0, 0, "fake-id", "");
	}
	public Activity(long modifiedTimeMillis, long endTimeMillis, String description, int activityType,
			long startTimeMillis, String id, String name) {
		super();
		this.modifiedTimeMillis = modifiedTimeMillis;
		this.endTimeMillis = endTimeMillis;
		this.description = description;
		this.activityType = activityType;
		this.startTimeMillis = startTimeMillis;
		this.id = id;
		this.name = name;
	}
	
	public long getModifiedTimeMillis() {
		return modifiedTimeMillis;
	}
	public void setModifiedTimeMillis(long modifiedTimeMillis) {
		this.modifiedTimeMillis = modifiedTimeMillis;
	}
	public long getEndTimeMillis() {
		return endTimeMillis;
	}
	public void setEndTimeMillis(long endTimeMillis) {
		this.endTimeMillis = endTimeMillis;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getActivityType() {
		return activityType;
	}
	public void setActivityType(int activityType) {
		this.activityType = activityType;
	}
	public long getStartTimeMillis() {
		return startTimeMillis;
	}
	public void setStartTimeMillis(long startTimeMillis) {
		this.startTimeMillis = startTimeMillis;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Application getApplication() {
		return application;
	}
	public void setApplication(Application application) {
		this.application = application;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
	      return true;
	    }
	    if (other == null || !other.getClass().equals(this.getClass())) {
	      return false;
	    }
	    Activity activity = (Activity) other;
	    return Objects.equal(id, activity.id)
    		&& Objects.equal(activityType, activity.activityType) 
    		&& Objects.equal(modifiedTimeMillis, activity.modifiedTimeMillis)
    		&& Objects.equal(startTimeMillis, activity.startTimeMillis)
    		&& Objects.equal(endTimeMillis, activity.endTimeMillis);
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id, activityType, modifiedTimeMillis, startTimeMillis, 
				endTimeMillis);
	}

	@Override
	public String toString() {
		//return MoreObjects.toStringHelper(this)
		return Objects.toStringHelper(this)
			.add("Date", getActivityTimeStamp())
			.add("id", this.getId())
			.add("Name", name)
			.add("Type", getTypeById(this.getActivityType()))
			.add("Description", this.getDescription())
			.add("Duration", getFormattedDuration(this.getDurationMillis()))
			.toString();
	}

	/**
	 * Formats the millis provided as HH:MM:SS
	 */
	public static String getFormattedDuration(long millis) {
		// HH:MM:SS "%02d:%02d:%02d"
		// HH:MM "%02d:%02d"
		// HH "%02d"
		return String.format("%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis) -  
				TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
	}
	
	public static String getTypeById(int id) {
		return ACTIVITY_TYPE_MAPPER.getOrDefault(id, "" + id);
	}
	public long getDurationMillis() {
		return this.getEndTimeMillis() - this.getStartTimeMillis();
	}
	public int getActivityMonth() {
		long createdMillis = this.startTimeMillis;
		Calendar c = Calendar.getInstance(); 
		//Set time in milliseconds
		c.setTimeInMillis(createdMillis);
		return c.get(Calendar.MONTH);
	}
	public String getActivityTimeStamp() {
		long createdMillis = this.startTimeMillis;
		Calendar c = Calendar.getInstance(); 
		c.setTimeInMillis(createdMillis);
		return simpleDateFormat.format(c.getTime());
	}
	public String asJson() {
//		{
//			"id": "7cd84aeea320a9ab:activemode:calisthenics:1548341795071",
//				"name": "push",
//				"description": "",
//				"startTimeMillis": "1548341795139",
//				"endTimeMillis": "1548345424312",
//				"modifiedTimeMillis": "1548350100664",
//				"application": {
//			"packageName": "com.google.android.apps.fitness"
//		},
//			"activityType": 21
//		}
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}

