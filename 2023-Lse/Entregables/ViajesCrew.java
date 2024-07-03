import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class ViajesCrew 
{
	public static void main(String[] args) 
	{		
		
		int[][] viajesCrew = {
                {1,0,0,1,0,0,1,0,0,1,0,0},
                {0,1,0,0,1,0,0,1,0,0,1,0},
                {0,0,1,0,0,1,0,0,1,0,0,1},
                {0,0,0,1,0,0,1,0,1,1,0,1},
                {1,0,0,0,0,1,0,0,0,1,1,0},
                {0,0,0,1,1,0,0,0,1,0,0,0},
                {0,0,0,0,0,0,1,1,0,1,1,1},
                {0,1,0,1,1,0,0,0,1,0,0,0},
                {0,0,0,0,1,0,0,1,0,0,1,0},
                {0,0,1,0,0,0,1,1,0,0,0,1},
                {0,0,1,0,0,0,1,1,0,0,0,1},
                {0,0,0,0,0,1,0,0,1,1,1,1}};
		
		int [] precioCrew= {2,3,4,6,7,5,7,8,9,9,8,9};
		
		int numCrew = viajesCrew.length;
		int numVuelos = viajesCrew[0].length;
		
		Model model = new Model(numCrew + "-Crew problem");
		
		IntVar[] crewPlan = new IntVar[numCrew];
		
		IntVar price;
		
		for(int q = 0; q < numCrew; q++)
		{
			int crew=q+1;
			crewPlan[q] = model.intVar("Crew_"+crew,0,1);
		}
		
		price = model.intVar("totalprice",0, Arrays.stream(precioCrew).sum());
		
		
		
		for(int i  = 0; i < numVuelos-1; i++)
		{
			model.scalar(crewPlan, viajesCrew[i], ">=", 1).post();

		}
		model.sum(crewPlan, ">=",3).post();
		model.scalar(crewPlan, precioCrew, "=",price ).post();
		
		
		
		
		Solution solution = model.getSolver().findOptimalSolution(price,false);
		if(solution != null)
		{
		    
		    for(int q = 0; q < numCrew; q++)
			{
				 System.out.println("Crew_"+(q+1)+": "+solution.getIntVal(crewPlan[q]));
			}
		    System.out.println("Total_Price: "+solution.getIntVal(price));
		}
		
	}
}
