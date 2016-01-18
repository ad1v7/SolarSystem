/**
 * Computer Modelling, Exercise 3.
 *
 * Run with ARGUMENTS: output input number_of_steps timestep
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @author "012221/2016"
 *
 */

// IO package for file writing
import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class Particle2DVerlet {

    public static void main (String[] argv) throws IOException {

	// Opens the output file
	String outFile = argv[0];
        PrintWriter output = new PrintWriter(new FileWriter(outFile));

	// Count number of particles in an input file
	BufferedReader inputFile = new BufferedReader(new FileReader(argv[1]));
	Scanner scan = new Scanner(inputFile);

	// Creates two particles read from the input file
	Particle3D Central = Particle3D.pScanner(scan);
	Particle3D Orbital = Particle3D.pScanner(scan);

	// Number of timesteps
	int numstep = Integer.parseInt(argv[2]);
	// Size of timestep
	double dt = Double.parseDouble(argv[3]);
	// Initial time
	double t = 0.0;

	/*
	 * Start of the Verlet algorithm
	 */

	output.printf("%s %f\n", Particle3D.unitVec(Central,Orbital), Particle3D.magForce(Central,Orbital));
	output.printf("%s %f\n", Particle3D.vecForce(Central,Orbital), Particle3D.vecForce(Central,Orbital).mag());
	output.printf("%s %f\n", Particle3D.pSep(Central,Orbital), Particle3D.pSep(Central,Orbital).mag());
	output.printf("%s %f\n", Particle3D.potEnergy(Central,Orbital), Particle3D.pSep(Central,Orbital).mag());
	// Prints the intial position to file
	//		output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY());

	// Prints initial time and total energy to file
	// output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));

	// Initial force vector
	double magForce = -Orbital.getMass()*Central.getMass() / Particle3D.pSep(Orbital, Central).magSq();
	Vector3D force = new Vector3D(Particle3D.pSep(Orbital, Central).scalMul(magForce));

	/*
	 * Loop over timesteps
	 */
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

	// Close the output file
	output.close();
    }
}
