package Time4WorkStorage;

import java.util.ArrayList;

import Time4WorkStorage.Tasks.TaskType;

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
	

	private ArrayList<Tasks> searchType(ArrayList<Tasks> myList, int searchType) {
		
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).getType() == searchType) {
				resultList.add(myList.get(i));
			}
		}
		
		return resultList;
	}
}
		