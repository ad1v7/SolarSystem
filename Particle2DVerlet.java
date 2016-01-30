/**
 * Computer Modelling, Exercise 3.
 *
 * Run with ARGUMENTS: output input number_of_steps timestep checkF checkO
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

public class Particle2DVerlet {

    public static void main (String[] argv) throws IOException {

	// Opens the output file
	String outFile = argv[0];
	String outFile2 = "checkF.dat";
	String outFile3 = "checkO.dat";
	String outFile4 = "totEfluct.dat";
        PrintWriter output = new PrintWriter(new FileWriter(outFile));
	PrintWriter check1 = new PrintWriter(new FileWriter(outFile2));
	PrintWriter check2 = new PrintWriter(new FileWriter(outFile3));
	PrintWriter fluct = new PrintWriter(new FileWriter(outFile4));

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

	// Prints the intial position to file
	output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY());

	// Initial force vector
	double magForce = Orbital.getMass()*Central.getMass() / Particle3D.pSep(Orbital, Central).magSq();
	Vector3D force = new Vector3D(Particle3D.pSep(Orbital,Central).scalMul(magForce).scalDiv(Particle3D.pSep(Orbital,Central).mag()));

	// Prints checks on forces
	// magForce(p1,p2) - force on p1 due to p2
	check1.printf("%f %s\n", magForce, force);
	check1.printf("%f %s\n", Particle3D.magForce(Orbital,Central), Particle3D.vecForce(Orbital,Central));
	check1.printf("%f %s\n\n", Particle3D.magForce(Central,Orbital), Particle3D.vecForce(Central,Orbital));
   
	/*
	 * Loop over timesteps
	 */
	for (int i=0;i<numstep;i++) {

	    // Update the position using current velocity
	    Orbital.leapPosition(dt,force);

	    // Force after time leap
	    double magForce_new = Orbital.getMass()*Central.getMass() / Particle3D.pSep(Orbital, Central).magSq();
	    Vector3D force_new = new Vector3D(Particle3D.pSep(Orbital,Central).scalMul(magForce_new).scalDiv(Particle3D.pSep(Orbital,Central).mag()));
	   
	    // Update the velocity ready for the next position update
	    Orbital.leapVelocity(dt, Vector3D.vecAdd(force, force_new).scalDiv(2));

	    // Update force
	    magForce = magForce_new;
	    force = force_new;

	    // Increase the time
	    t = t + dt;

	    // Prints the current position to file
	    output.printf("%s %s\n", Orbital.getPosition().getX(), Orbital.getPosition().getY() );
	  

	    // Prints checks on forces 
	    check1.printf("%.5f %s\n", magForce, force);
	    check1.printf("%f %s\n", Particle3D.magForce(Orbital,Central), Particle3D.vecForce(Orbital,Central));
	    check1.printf("%f %s\n\n", Particle3D.magForce(Central,Orbital), Particle3D.vecForce(Central,Orbital));

	    // Prints checks unitVec, pSep, kEnergy, potEnergy, totEnergy
	    check2.printf("%f %f ", Particle3D.unitVec(Central,Orbital).mag(), Particle3D.unitVec(Orbital,Central).mag());
	    check2.printf("%s %s ", Particle3D.unitVec(Central,Orbital), Particle3D.unitVec(Orbital,Central));
	    check2.printf("%s %s ", Particle3D.pSep(Central,Orbital), Particle3D.pSep(Orbital,Central));
	    check2.printf("%s %s ", Particle3D.kEnergy(Central), Particle3D.kEnergy(Orbital));
	    check2.printf("%s %s ", Particle3D.potEnergy(Central,Orbital), Particle3D.potEnergy(Orbital,Central));
	    check2.printf("%s %s\n\n", Particle3D.totEnergy(Orbital,Central), Particle3D.totEnergy(Central,Orbital));

	    // Prints time and energy
	    fluct.printf("%.15f %.15f\n", t,  Particle3D.totEnergy(Orbital,Central));


	}

	// Close the output files
	output.close();
	check1.close();
	check2.close();
	fluct.close();
    }
}
