/**
 * Computer Modelling, Project
 * Simulation of the Solar System
 *
 * Run with ARGUMENTS: output input
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @author "01/2016"
 *
 */

// IO package for file writing
import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class Nbody {

    public static void main (String[] argv) throws IOException {

	// NEED TO GENERALIZE: INPUT PARAM FROM A FILE

	// Number of timesteps
	double numstep = 10000;

	// Size of timestep
	double dt = 0.01;

	// Initial time
	double t = 0.0;

	// Opens the output file
	String outFile = "output2.dat";
        PrintWriter output = new PrintWriter(new FileWriter(outFile));
	
	// count number of particles in an input file and store it in nPar
	BufferedReader reader = new BufferedReader(new FileReader("input.dat"));
	int nPar = 0;
	while (reader.readLine() != null) nPar++;
	reader.close();
	
	// Attach a scanner to the input file
	BufferedReader inputFile = new BufferedReader(new FileReader("input.dat"));
	Scanner scan = new Scanner(inputFile);

	// Create new array of Particles3D and copy particles from an input file
	Particle3D allPar[] = new Particle3D[nPar];
	for (int i=0; scan.hasNext(); i++) {
	    allPar[i] = Particle3D.pScanner(scan);
	}
	

	// Set up array which stores i-th Particle
	// ith-force: [i][0] and force_new: [i][1] 
	// [row][col] <<-- delete that ;)
	Vector3D parForce[][] = new Vector3D[nPar][2];

	for (int i = 0; i < nPar; i++ ) {
	    parForce[i][0] = new Vector3D();
	    parForce[i][1] = new Vector3D();
	}

	// calculate initial forces
	Particle3D.upForce(allPar, parForce, 0);
	
	/*
	 * Start of the Verlet algorithm
	 */
	

	//Prints the intial position to file
	output.printf(Particle3D.vmd(allPar, 1));


	/*
	 * Loop over timesteps
	 */


	for (int i=0;i<numstep;i++) {

	    // Update the position using current velocity
	    Particle3D.leapPosition(dt, allPar, parForce);

	    // Force after time leap
	    Particle3D.upForce(allPar, parForce, 1);
  
	    // Update the velocity ready for the next position update
	    Particle3D.leapVelocity(dt, allPar, parForce);

	    // Update force
	    for (int j=0; j < nPar; j++) {
		parForce[j][0] = new Vector3D(parForce[j][1]);
		parForce[j][1] = new Vector3D();
	    }

	    // Increase the time
	    t = t + dt;

	    //Prints the current position to VMD file (need method)	   
	    output.printf(Particle3D.vmd(allPar, i+2));

	    // Prints current time and total energy to file
	    // output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));
	    
	}
  
	// Close the output file
	output.close();

    }
}
