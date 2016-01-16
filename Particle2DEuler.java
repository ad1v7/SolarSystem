/**
 * Computer Modelling, Exercise 3.
 *
 * Run with arguments: output input number_of_steps timestep
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @author "11/2015"
 *
 */

// IO package for file writing
import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class Particle2DEuler {

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
	 * Start of the symplectic Euler algorithm
	 */

	output.printf("%f\n", Particle3D.potEnergy(Central, Orbital));

	// Prints the initial time and position to file
	output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY());

	
	// Prints initial time and total energy to file
	// output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));
	
	/*
	 * Loop over timesteps
	 */

	for (int i=0;i<numstep;i++) {

	    // Update the position using current velocity
	    Orbital.leapPosition(dt);

	    // Update the force using current position
	    double magForce = -Orbital.getMass()*Central.getMass() / Particle3D.pSep(Orbital,Central).magSq();

	    Vector3D force = new Vector3D(Particle3D.pSep(Orbital, Central).scalMul(magForce));

	    // Update the velocity ready for the next position update
	    Orbital.leapVelocity(dt, force);

	    // Increase the time
	    t = t + dt;

	    // Prints the current position to file
	    output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY() );

	    //    Prints current time and total energy to file
	    // output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));
		}

	// Close the output file
	output.close();
    }
}
