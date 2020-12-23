package localsearch;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Tsp {
	//2D array storing all routes through the map of cities
	private double[][] graph;
	//String representing name of file that contains all cities
	private String fileName;
	//Random used to generate random permutations of cities (routes through the map)
	private Random random;
	//next route to apply local search to
	private List<Integer> nextStartRoute = new ArrayList<>();
	//global best route
	private List<Integer> shortestRouteFound = new ArrayList<>();
	
	/**
	 * Contructs Travelling Salesperson problem
	 * @param fileName String representing name of file that contains all cities
	 * @throws ArrayDimensionsException
	 * @throws FileNotFoundException
	 */
	public Tsp(String fileName) throws ArrayDimensionsException, FileNotFoundException{
		//Data set of cities and their coordinates
		this.fileName = fileName;
		//Initialise map of cities (2D array)
		this.graph = createGraph();
		//Check that the map of cities being given is n x n
		for(int i = 0;i < graph.length;i++) {
			if(graph.length != graph[0].length) {
				throw new ArrayDimensionsException("n x n Matrix required i.e. equal number of columns and rows.");
			}
		}
		//Initialise random variable for later use when generating random routes.
		random = new Random();
	}
	
	/**
	 *  Gets file name of CSV containing distances between all cities
	 * @return fileName String representing name of file that contains all cities
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Gets shortest route
	 * @return nextStartRoute Next route to apply local search to
	 */
	public List<Integer>  getNextStartRoute() {
		return this.nextStartRoute;
	}
	
	/**
	 * Sets next start route for local search
	 * @param nextStartRoute Next route to apply local search to
	 */
	public void setNextStartRoute(List<Integer> nextStartRoute) {
		this.nextStartRoute = nextStartRoute;
	}  
	
	/**
	 * Sets shortest route found
	 * @param shortestRouteFound Global best route
	 */
	public void setShortestRouteFound(List<Integer> shortestRouteFound) {
		this.shortestRouteFound = shortestRouteFound;
	}
	
	/**
	 * Gets shortest route found
	 * @return shortestRouteFound Global best route
	 */
	public List<Integer> getShortestRouteFound() {
		return this.shortestRouteFound;
	}
	
	/**
	 * Generates a random legal route through the graph/matrix
	 * @return route random legal route
	 */
	public List<Integer> generateRandomRoute() {
		List<Integer> route = new ArrayList<>();
		int routeSize = route.size();
		while(routeSize < this.graph.length) {
			int city = this.random.nextInt(this.graph.length) + 1;
			if(!route.contains(city)) {
				route.add(city);
				routeSize++;
			}
		}
		return route;
	}
	
	/**
	 * Find the shortest route in the neighbourhood of given a starting route
	 * Neighbourhood generated via 2-opt swap
	 * Finds local minimum of a neighbourhood
	 * @param route Starting route for local search
	 * @return shortestNeighbour shortest route in 2-opt-swap neighbour of of starting route
	 */
	public List<Integer> shortestInNeighbourhood(List<Integer> route) {
		/**
		 * Store the shortest local minimum found
		 *At the beginning the shortest neighbour is just the starting route, by default
		 */
		List<Integer> shortestNeighbour = new ArrayList<>(route);
		//A copy of the starting route so 2 opt swap can be applied without changing the parameter value
		List<Integer> current = new ArrayList<>(route);
		int neighbourCount = 0;
		/**
		 * Swap 1st and 2nd city, 1st and 3rd city, 1st and 4th city etc.
		 * Then swap 2nd and 1st city, 2nd and 3rd city, 2nd and 4th city etc.
		 */
		for(int i = 0;i < route.size();i++) {
			for(int j = i+1;j < route.size();j++) {
				int swap1 = route.get(i);
				int swap2 = route.get(j);
				current.remove(i);
				current.add(i,swap2);
				current.remove(j);
				current.add(j,swap1);
				double costOfRoute = getCostOfRoute(current);
				neighbourCount++;
				System.out.println("Neighbour " + neighbourCount + ": " + current.toString() + " = " + costOfRoute);
				if(costOfRoute < getCostOfRoute(shortestNeighbour)) {
					shortestNeighbour.clear();
					shortestNeighbour.addAll(current);
				}
				/**
				 *reset current to the original starting route 
				 *so 2opt swap can be applied using next city
				 */
				current = new ArrayList<>(route);
			}
		}
		//Return local minimum (shortest neighbour in neighbourhood)
		return shortestNeighbour;
	}
	
	/**
	 * Get cost of a single route
	 * @param routeList Single route
	 * @return cost Cost of single route given as a parameter
	 */
	public double getCostOfRoute(List<Integer> routeList) {
		//starting point/city
		int start = routeList.get(0)-1;
		//last city before you have to return to the starting point
		int finish = routeList.get(routeList.size()-1)-1;
		//cost of route
		double cost = 0;
		/**
		 * start FROM one city TO another
		 *because each city ranges from index 0..n it is easy to just iterate through 2D array of all the cities
		 */
		for(int i = 0;i < routeList.size()-1;i++) {
			int from = routeList.get(i)-1;
			int to = routeList.get(i+1)-1;
			//keep track of running cost of the route as you go along the route
			cost += this.graph[from][to];
		}
		//Finally, add the cost of returning back to the starting point/city
		cost += this.graph[finish][start];
		//return total cost of route
		return cost;
	}
	
	/**
	 * Run local search algorithm on TSP
	 * @param tsp TSP instance
	 * @param time termination condition(time(ms))
	 */
	public void run(Tsp tsp, int time) {
		//Local search on a random starting route
		tsp.setNextStartRoute(tsp.generateRandomRoute());
		//Set the shortest global route to the above random route
		tsp.setShortestRouteFound(tsp.getNextStartRoute());
		//Find the shortest neighbour in the neighbourhood of the random route
		long start = System.currentTimeMillis();
		while(System.currentTimeMillis() - start < time) {
			//Starting route to apply local search to
			List<Integer> startRoute =  tsp.getNextStartRoute();
			//Local search applied here to find local minimum
			System.out.println("Starting route for neighbourhood: " + startRoute.toString() + " = " + tsp.getCostOfRoute(startRoute));
			List<Integer> localMinimum = tsp.shortestInNeighbourhood(startRoute);
			double costOfLocalMinimum = tsp.getCostOfRoute(localMinimum);
			System.out.println("Shortest in neighbourhood: " + localMinimum.toString() + " = " + costOfLocalMinimum);
			//if the local minimum found is a shorter route than the starting point...
			if(costOfLocalMinimum < tsp.getCostOfRoute(startRoute)) {
				/**
				 * ...Then set the next starting point to the local minimum found
				 *local search will be applied to this new starting point
				 */
				tsp.setNextStartRoute(localMinimum);
				/**
				 * If the local minimum found is also a global minimum
				 * then set the global minimum to the newly found global minimum
				 */
				if(tsp.getCostOfRoute(localMinimum) < tsp.getCostOfRoute(tsp.getShortestRouteFound())) {
					tsp.setShortestRouteFound(localMinimum);
				}
			}
			/**
			 * If the localMinimum found is not shorter than the starting point
			 *Then just generate a new random starting point
			 *This prevents getting stuck in one specific neighbourhood
			 */
			else {
				tsp.setNextStartRoute(tsp.generateRandomRoute());
			}
			System.out.println("Best route so far: " + tsp.getShortestRouteFound()+ " = " + tsp.getCostOfRoute(tsp.getShortestRouteFound()));
		}
		//Finally, print the best global minimum found
		System.out.println("ANSWER: " + tsp.getShortestRouteFound() + " = " + tsp.getCostOfRoute(tsp.getShortestRouteFound()));
	}
	
	/**
	 * 
	 * @return routes 2D matrix containing dsitances between all cities
	 * @throws FileNotFoundException
	 */
	public double[][] createGraph() throws FileNotFoundException {
		//Read in the file
		String filename = getFileName();
		File file = new File(filename);
		Scanner inputStream = new Scanner(file);
		List<String> data = new ArrayList<>();
		//keep track of which line is being read in
		int lines = 0;
		//add each line(city and its x and y coordinates) to an ArrayList
		while(inputStream.hasNext()) {
			String value = inputStream.next();
			data.add(value);
			lines++;
		}
		inputStream.close();
		//3D Array that contains each city and its coordinates
		String[][][] values = new String[lines][3][1];
		/**
		 * this fills the 3D array such that the 3D array contains
		 *a bunch of arrays, each of which contains 2 arrays of length 1
		 *these 2 arrays contain the x coordinate and the y coordinate of a city
		 */
		for(int i = 0;i < data.size();i++) {
			String[] splitData= data.get(i).split(",");
			for(int j = 1;j<3;j++) {
				values[i][0][0] = splitData[1];
				values[i][1][0] = splitData[2];
			}
		}
		/**
		 * Fill the 2D routes matrix with the distances from one city and another
		 *E.g. routes[0][1] contains the distance from city 1 to city 2
		 */
		double[][] routes = new double[lines][lines];
		for(int i = 0;i < routes.length;i++) {
			for(int j = 0;j < routes.length;j++) {
				if(i == j) {
					
				} else {
					double city1x = Double.parseDouble(values[i][0][0]);
					double city1y = Double.parseDouble(values[i][1][0]);
					double city2x = Double.parseDouble(values[j][0][0]);
					double city2y = Double.parseDouble(values[j][1][0]);
					double distanceBtwnCities = Math.sqrt(Math.pow((city2y-city1y), 2) + Math.pow((city2x-city1x), 2));
					routes[i][j] = distanceBtwnCities;
				}
			}
		}
		//Return full, complete matrix read for use
		return routes;
	}
}
