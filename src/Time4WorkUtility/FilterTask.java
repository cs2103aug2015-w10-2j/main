package Time4WorkUtility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import Time4WorkData.Tasks.TaskType;
import Time4WorkData.*;

/* @@author A0125495Y */



public class FilterTask {
	
	private final int NEARMATCH_MAX = 3;
	private final String SPLITTER = "\\s";
	private static final int DeadlineType = TaskType.DeadlineType.getTaskType();
	private static final int DurationType = TaskType.DurationType.getTaskType();
	private static final int FloatingType = TaskType.FloatingType.getTaskType();
			
	//filters task description for matching task
	//single character will search for task with description starting with the character or matching single character description
	//multiple words will match all containing each word or near matches
	public ArrayList<Tasks> searchDescription(ArrayList<Tasks> myList, String searchString) {
		
		Levenshtein myLevenshtein = new Levenshtein();
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		if(searchString == null) {
			return myList;
		}
		
		String[] splitString = searchString.split(SPLITTER);
		ArrayList<String> startWith = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		
		for(int i=0; i<splitString.length; i++) {
			if(splitString[i].length() == 1) {
				startWith.add(splitString[i]);
			}
			else {
				words.add(splitString[i]);
			}
		}
		
		for(int i=0; i<myList.size(); i++) {
			
			boolean added = false;
			for(int j=0; j<startWith.size(); j++) {
				//check starting with for single character searching
				if(myList.get(i).getDescription().substring(0,1).toUpperCase().equals(startWith.get(j).toUpperCase())) {
					added = true;
					break;
				}
			}
			
			if(!added) {
				for(int j=0; j<words.size(); j++) {
					//checks exact word matches
					if(myList.get(i).getDescription().toUpperCase().contains(words.get(j).toUpperCase())) {
						added = true;
						break;
					} 
				}
			}
			
			String[] brokenDesc = myList.get(i).getDescription().split(SPLITTER);
			
			if(!added) {	
				outerloop:
				for(int j=0; j<brokenDesc.length; j++) {					
					for(int m=0; m<startWith.size(); m++) {
						//checks single character matches with single description
						if(brokenDesc[j].equalsIgnoreCase(startWith.get(m))) {
							added = true;
							break outerloop;
						}									
					}
				}
			}
			
			if(!added) {
				outerloop:
				for(int j=0; j<brokenDesc.length; j++) {	
					for(int k=0; k<words.size(); k++) {
						//checks near matches
						int nearMatchLimit = words.get(k).length()/2;
						if(nearMatchLimit > NEARMATCH_MAX) {
							nearMatchLimit = NEARMATCH_MAX;
						}
						int isCloseMatch = myLevenshtein.distance(brokenDesc[j].toUpperCase(), words.get(k).toUpperCase(), nearMatchLimit);
						if(isCloseMatch != -1) {
							added = true;
							break outerloop;
						} 
					}
				}
			}
			if(added) {
				resultList.add(myList.get(i));
			}			
		}		
		return resultList;
	}
	
	
	private ArrayList<Tasks> searchCompletion(ArrayList<Tasks> myList, boolean completed) {
				
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).isCompleted() == completed) {
				resultList.add(myList.get(i));
			}
		}		
		return resultList;
	}
	
	public ArrayList<Tasks> searchCompleted(ArrayList<Tasks> myList) {
		return searchCompletion(myList, true);
	}
	
	public ArrayList<Tasks> searchNotCompleted(ArrayList<Tasks> myList) {
		return searchCompletion(myList, false);
	}
	
	public ArrayList<Tasks> searchFloating(ArrayList<Tasks> myList) {
		return searchType(myList, FloatingType);
	}
	
	public ArrayList<Tasks> searchDeadline(ArrayList<Tasks> myList) {
		return searchType(myList, DeadlineType);
	}
	
	public ArrayList<Tasks> searchDuration(ArrayList<Tasks> myList) {
		return searchType(myList, DurationType);
	}
	

	public ArrayList<Tasks> searchDate(ArrayList<Tasks> myList, String date) {
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		for(int i=0; i<myList.size(); i++) {
			int taskType = myList.get(i).getType();
			if(taskType == DeadlineType) {
				DeadlineTask tempTask = (DeadlineTask) myList.get(i);
				if(tempTask.getDate().equals(date)) {
					resultList.add(myList.get(i));
				}
			} else if (taskType == DurationType) {
				DurationTask tempTask = (DurationTask) myList.get(i);
				if(tempTask.getStartDate().equals(date)) {
					resultList.add(myList.get(i));
				}
			} 		
		}		
		return resultList;
	}
	
	public ArrayList<Tasks> searchBeforeDate(ArrayList<Tasks> myList, String date) throws ParseException {
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		Date targetDate = stringToDate(date, 1);

		for(int i=0; i<myList.size(); i++) {
			
			int taskType = myList.get(i).getType();
			
			if(taskType == DeadlineType) {
				Date myDate = stringToDate(((DeadlineTask) myList.get(i)).getDate(),0);
				if(myDate.before(targetDate)) {
					resultList.add(myList.get(i));
				}
			} else if (taskType == DurationType) {
				Date myDate = stringToDate(((DurationTask) myList.get(i)).getStartDate(),0);
				if(myDate.before(targetDate)) {
					resultList.add(myList.get(i));
				}
			} 		
		}		
		return resultList;
	}
	
	public ArrayList<Tasks> searchOverDue(ArrayList<Tasks> myList) throws ParseException {
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		Calendar today = Calendar.getInstance();
		Date now = today.getTime();
		
		for(int i=0; i<myList.size(); i++) {			
			int taskType = myList.get(i).getType();
			if(taskType == DeadlineType) {
				Date myDate = stringToDate(((DeadlineTask) myList.get(i)).getDate(),0);
				if(myDate.before(now)) {
					resultList.add(myList.get(i));
				}
			} else if (taskType == DurationType) {
				Date myDate = stringToDate(((DurationTask) myList.get(i)).getStartDate(),0);
				if(myDate.before(now)) {
					resultList.add(myList.get(i));
				}
			} 		
		}		
		return resultList;
	}
	
	public ArrayList<Tasks> searchBetweenDates(ArrayList<Tasks> myList, String date1, String date2) throws ParseException {
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		ArrayList<Tasks> beforeDateB = new ArrayList<Tasks>();
		ArrayList<Tasks> afterDateA = new ArrayList<Tasks>();
		
		Date dateA = stringToDate(date1, -1);
		Date dateB = stringToDate(date2, 1);
		
		for(int i=0; i<myList.size(); i++) {
			
			int taskType = myList.get(i).getType();
			
			if(taskType == DeadlineType) {
				Date myDate = stringToDate(((DeadlineTask) myList.get(i)).getDate(),0);
				if(myDate.before(dateB)) {
					beforeDateB.add(myList.get(i));
				}
				if(myDate.after(dateA)) {
					afterDateA.add(myList.get(i));
				}
			} else if (taskType == DurationType) {
				Date myDate = stringToDate(((DurationTask) myList.get(i)).getStartDate(),0);
				if(myDate.before(dateB)) {
					beforeDateB.add(myList.get(i));
				}
				if(myDate.after(dateA)) {
					afterDateA.add(myList.get(i));
				}
			} 		
		}
		resultList = filterCommon(afterDateA, beforeDateB);
		
		return resultList;
	}
	

	private ArrayList<Tasks> searchType(ArrayList<Tasks> myList, int searchType) {
				
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).getType() == searchType) {
				resultList.add(myList.get(i));
			}
		}		
		return resultList;
	}
	
	private Date stringToDate(String date, int offset) throws ParseException {

		DateFormat format = new SimpleDateFormat("ddMMyy", Locale.ENGLISH);		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(format.parse(date));
		cal.add( Calendar.DATE, offset );	
		
		Date returnDate = cal.getTime();
		return returnDate;
	}
	
	private ArrayList<Tasks> filterCommon(ArrayList<Tasks> listA, ArrayList<Tasks> listB) {
		listA.retainAll(listB);
		return listA;
	}
	
}
		