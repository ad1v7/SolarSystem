/**
 * Computer Modelling, Project
 * Simulation of the Solar System
 *
 * Run with ARGUMENTS:
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
	//	int numstep = Integer.parseInt(argv[2]);

	// Size of timestep
	//	double dt = Double.parseDouble(argv[3]);

	// Initial time
	double t = 0.0;

	// Opens the output file
	String outFile = argv[0];
        PrintWriter output = new PrintWriter(new FileWriter(outFile));

	// count number of particles in an input file and store it in nPar
	BufferedReader reader = new BufferedReader(new FileReader(argv[1]));
	int nPar = 0;
	while (reader.readLine() != null) nPar++;
	reader.close();

	// Attach a scanner to the input file
	BufferedReader inputFile = new BufferedReader(new FileReader(argv[1]));
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

	// calculate initial forces ((do a method???))

	for (int i=0; i < nPar; i++) {
	    for (int j=0; j < nPar; j++) {
		if (i != j) {
		  
		    parForce[i][0] = Vector3D.vecAdd(parForce[i][0], Particle3D.vecForce(allPar[i], allPar[j]));
		}
	    }

	}

	for (int i=0; i<nPar; i++) {
	    output.printf(" %s %s \n", allPar[i], parForce[i][0]);
	}
	

	/*
	 * Start of the Verlet algorithm
	 */
	/*
	output.printf("%s %f\n", Particle3D.unitVec(Central,Orbital), Particle3D.magForce(Central,Orbital));
	output.printf("%s %f\n", Particle3D.vecForce(Central,Orbital), Particle3D.vecForce(Central,Orbital).mag());
	output.printf("%s %f\n", Particle3D.pSep(Central,Orbital), Particle3D.pSep(Central,Orbital).mag());
	output.printf("%s %f\n", Particle3D.potEnergy(Central,Orbital), Particle3D.pSep(Central,Orbital).mag());

	*/
	// Prints the intial position to file
	//		output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY());

	// Prints initial time and total energy to file
	// output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));

	// Initial force vector
	/*
	double magForce = -Orbital.getMass()*Central.getMass() / Particle3D.pSep(Orbital, Central).magSq();
	Vector3D force = new Vector3D(Particle3D.pSep(Orbital, Central).scalMul(magForce));

	// // // // print total energy, k. energy, p. energy, force (vec + mag) on F_12 and F_21
	// change mass to >1
	*/
	/*
	 * Loop over timesteps
	 */

	/*
	for (int i=0;i<numstep;i++) {

	    // Update the position using current velocity
	    Orbital.leapPosition(dt,force);

	    // Force after time leap
	    double magForce_new = -Orbital.getMass()*Central.getMass() / Particle3D.pSep(Orbital, Central).magSq();
	    Vector3D force_new = new Vector3D(Particle3D.pSep(Orbital, Central).scalMul(magForce_new));
	   
	    // Update the velocity ready for the next position update
	    Orbital.leapVelocity(dt, Vector3D.vecAdd(force, force_new).scalDiv(2));

	    // Update force
	    magForce = magForce_new;
	    force = force_new;

	    // Increase the time
	    t = t + dt;

	    // Prints the current position to file
	    output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY() );

	    // Prints current time and total energy to file
	    // output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));
	}
*/
	// Close the output file
	output.close();
    }
}
