package Time4WorkStorage;

public class Levenshtein {
	 
    public int distance(String data, String search, int limit) {
    	data = data.toUpperCase();
    	search = search.toUpperCase();
    	System.out.println("checking data " + data + " with search "+ search);
        int [] costs = new int [search.length() + 1];
        
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        
        for (int i = 1; i <= data.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= search.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), data.charAt(i - 1) == search.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        
        if(costs[search.length()] > limit) {
        	System.out.println("Limit was " + limit + " distance was "+ costs[search.length()]);
        	return -1;
        } else {
        	System.out.println("match is close enough: "+ costs[search.length()]);
        	return costs[search.length()];        	
        }
    }
}