package localsearch;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) throws ArrayDimensionsException, FileNotFoundException {
		//TSP instance
		Tsp tsp = new Tsp("cities90_11.csv");
		//parameters: tsp instance, termination condition(time(ms))
		tsp.run(tsp, 2000);
	}
}

