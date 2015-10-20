package Time4WorkStorage;

import java.util.ArrayList;


public class FilterTask {
			
	//filters task description for matching task and returns all matches
	public ArrayList<Tasks> searchDescription(ArrayList<Tasks> myList, String searchString) {
		
		
		ArrayList<Tasks> resultList = new ArrayList<Tasks>();
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).getDescription().toUpperCase().contains(searchString.toUpperCase())) {
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
		