import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Task;

public class TemporalPlanning {

    public static void main(String[] args) {
        // Create a Choco model
        Model model = new Model("Temporal Planning");
        
        int[] durations = {30, 60, 30, 120, 60};
        
        int desplazamiento = 30;
        
        int[][] parcelEarlyTimes = {
        		{9*60,13*60+30 }, //P1
        		{12*60,14*60+30}, //P2
        		{14*60,17*60 },   //P3
        		{9*60,17*60 },    //P4
        		{12*60,14*60+30 },//P5

        		
        };
        int[][] parcelLateTimes = {
        		{17*60, 20*60+30},  //P1
        		{16*60+30, 18*60+30},  //P2
        		{18*60,20*60+30 },  //P3
        		{18*60+30,20*60+30  },  //P4
        		{18*60+30,19*60+30  }, //P5       		
        };
        
        

        // Time variables
        IntVar[] startTimes = model.intVarArray("startTimes", 5, 12 * 60+desplazamiento, (20 * 60) - desplazamiento); 
        IntVar[] endTimes = model.intVarArray("endTimes", 5, 12 * 60+desplazamiento, (20 * 60) - desplazamiento);
        IntVar maxEndtime=model.max("max", endTimes);
        
        for (int i = 0; i < 5; i++) {
            
        	// Task [Start+Duration = end]
        	 model.arithm(endTimes[i],"=",startTimes[i],"+",durations[i]).post();
        	       
        	 // La tarea se encuentra entre los horarios que el cliente esta disponible
        	 model.or(
        			 model.and(model.arithm(startTimes[i],">=",parcelEarlyTimes[i][0]),
        					 model.arithm(endTimes[i],"<=",parcelEarlyTimes[i][1])),
        			 model.and(model.arithm(startTimes[i],">=",parcelLateTimes[i][0]),
        					 model.arithm(endTimes[i],"<=",parcelLateTimes[i][1]))      			 
        	        ).post();       	 
        	 
        	}
        
        // Evitar solapamiento entre tareas
        for (int i = 0; i < 5; i++) {
        	for (int j = 0; j < 5; j++) {
        		if(i!=j) {
        			 model.or(
        					 model.and(model.arithm(endTimes[i],"<=",startTimes[j],"-",desplazamiento),
        							 model.arithm(endTimes[i],"<",endTimes[j])
        							 ),
        					 
        					 model.and(model.arithm(startTimes[i],">=",endTimes[j],"+",desplazamiento),
        							 model.arithm(startTimes[i],">",startTimes[j])
        							 )
        					   			 
                	        ).post(); 
        		}
        	}
        }
        
       
        
        Solution solution = model.getSolver().findOptimalSolution(maxEndtime,false);
		if(solution != null)
		{
			for (int i = 0; i < 5; i++) {
			    int startTimeInMinutes = solution.getIntVal(startTimes[i]);
			    int endTimeInMinutes = solution.getIntVal(endTimes[i]);
	
			    int startHour = startTimeInMinutes / 60;
			    int startMinute = startTimeInMinutes % 60;
	
			    int endHour = endTimeInMinutes / 60;
			    int endMinute = endTimeInMinutes % 60;
	
			    System.out.println("Parcel " + (i + 1) + ": " +
			            "Start Time = " + String.format("%02d:%02d", startHour, startMinute) + ", " +
			            "End Time = " + String.format("%02d:%02d", endHour, endMinute));
			}
			int max = solution.getIntVal(maxEndtime)+desplazamiento;
		    int maxhour = max/60;
		    int maxMinute = max % 60;
			System.out.println(String.format("Fin de turno = %02d:%02d", maxhour, maxMinute));
		    
		}
		
    }

}
