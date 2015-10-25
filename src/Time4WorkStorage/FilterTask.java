package Time4WorkStorage;

import java.util.ArrayList;


public class FilterTask {
			
	//filters task description for matching task
	//single character will search for task with description starting with the character
	//multiple words will match all containing each word
	public ArrayList<Tasks> searchDescription(ArrayList<Tasks> myList, String searchString) {
		
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		String[] splitString = searchString.split("\\s+");
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
				if(myList.get(i).getDescription().substring(0,1).toUpperCase().equals(startWith.get(j).toUpperCase())) {
					resultList.add(myList.get(i));
					added = true;
					break;
				}
			}
			
			if(!added) {
				for(int j=0; j<words.size(); j++) {
					if(myList.get(i).getDescription().toUpperCase().contains(words.get(j).toUpperCase())) {
						resultList.add(myList.get(i));
					}
				}
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
	

	public ArrayList<Tasks> searchType(ArrayList<Tasks> myList, int searchType) {
		
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).getType() == searchType) {
				resultList.add(myList.get(i));
			}
		}
		
		return resultList;
	}
}
		