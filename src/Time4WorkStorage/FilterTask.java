package Time4WorkStorage;

import java.util.ArrayList;


public class FilterTask {
			
	//searches task description for matching task and returns all matches, null if no match
	public ArrayList<Tasks> searchDescription(ArrayList<Tasks> myList, String searchString) {
		
		
		ArrayList<Tasks> resultList = null;
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).getDescription().toUpperCase().contains(searchString.toUpperCase())) {
				resultList.add(myList.get(i));
			}
		}
		
		return resultList;
	}
	
	
	public ArrayList<Tasks> searchCompleted(ArrayList<Tasks> myList, boolean completed) {
		
		
		ArrayList<Tasks> resultList = null;
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).isCompleted() == completed) {
				resultList.add(myList.get(i));
			}
		}
		
		return resultList;
	}
	
	public ArrayList<Tasks> searchType(ArrayList<Tasks> myList, int searchType) {
		
		
		ArrayList<Tasks> resultList = null;
		
		for(int i=0; i<myList.size(); i++) {
			if(myList.get(i).getType() == searchType) {
				resultList.add(myList.get(i));
			}
		}
		
		return resultList;
	}
}
		