/**
 * Computer Modelling, Project
 * Simulation of the Solar System
 *
 * Run with ARGUMENTS: ????output input????
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @version "02/2016"
 *
 */

import java.io.*;
import java.util.Scanner;
import java.lang.*;

public class Nbody {

    public static void main (String[] argv) throws IOException {

	// Opens the input failing containing particles
	String particle_file = argv[0];

	// Count number of particles in an input file
	BufferedReader reader = new BufferedReader(new FileReader(particle_file));
	int numberOfParticles = 0;
	while (reader.readLine() != null) {
	    numberOfParticles++;
	}
	reader.close();

	// Attach a scanner to the input file
	BufferedReader p_file = new BufferedReader(new FileReader(particle_file));
	Scanner scan = new Scanner(p_file);

	// Create new array of Particles3D and copy particles from an input file
	Particle3D particleArray[] = new Particle3D[numberOfParticles];
	for (int i=0; scan.hasNext(); i++) {
	    particleArray[i] = Particle3D.pScanner(scan);
	}
	

	// Opens the second input file containing parameters: number of steps, size of timestep,
	// initial time, print frequency
	String param_file = argv[1];

	// Attach a scanner to the second input file
	BufferedReader par_file = new BufferedReader(new FileReader(param_file));
	Scanner scan2 = new Scanner(par_file);

	double numberOfSteps = scan2.nextDouble();
	double stepSize = scan2.nextDouble();
	double time = scan2.nextDouble();
	int printFrequency = scan2.nextInt();


	// Opens the output file
	String outFile = argv[2];
        PrintWriter output = new PrintWriter(new FileWriter(outFile));
	
	
	// Set up arrays which store forces
	Vector3D currentForceArray[] = new Vector3D[numberOfParticles];
	Vector3D newForceArray[] = new Vector3D[numberOfParticles];
	
	// set all forces to 0 to avoid later problems
	for (int i = 0; i < numberOfParticles; i++ ) {
	    currentForceArray[i] = new Vector3D();
	    newForceArray[i] = new Vector3D();
	}
	
	// calculate initial forces
	Particle3D.updateForce(particleArray, currentForceArray);
	
	/*
	 * Start of the Verlet algorithm
	 */
	
	//Prints the initial position to file
	int stepNumber = 1;
	output.printf(Particle3D.vmd(particleArray, stepNumber));
	stepNumber++;


	double currentAngle = 0;
	double initPosAngle = 0;
	double initVelAngle = 0;
	double orbitDirectionTest = 0;

	/*
	 * Loop over timesteps
	 */
	double minEnergy = Particle3D.sysEnergy(particleArray);
	double maxEnergy = Particle3D.sysEnergy(particleArray);
	double energy;

	// Arrays of doubles to store values of aphelions and periphelions for each body
	double aphelionArray[] = new double[numberOfParticles];
	double perihelionArray[] = new double[numberOfParticles];

	initPosAngle = Math.atan2(particleArray[0].getPosition().getY(), particleArray[0].getPosition().getX());
	initVelAngle = Math.atan2(particleArray[0].getVelocity().getY(), particleArray[0].getVelocity().getX());
	// make sure angle is in (0,2*PI) interval

	if (initPosAngle < 0) {
	    initPosAngle += 2*Math.PI;
	}
	else if (initVelAngle < 0) {
	    initVelAngle += 2*Math.PI;
	}
	// ehmmmm??? think again...
	orbitDirectionTest = initPosAngle + initVelAngle; // this is not good

	System.out.printf("%e\n", initPosAngle);

	for (int i=0; i<numberOfSteps; i++) {

	    // Update the position using current velocity
	    Particle3D.leapPosition(stepSize, particleArray, currentForceArray);

	    // Update force after time leap
	    Particle3D.updateForce(particleArray, newForceArray);
  
	    // Update the velocity ready for the next position update
	    Particle3D.leapVelocity(stepSize, particleArray, currentForceArray, newForceArray);

	    // Update force
	    for (int j=0; j < numberOfParticles; j++) {
		currentForceArray[j] = new Vector3D(newForceArray[j]);
		newForceArray[j] = new Vector3D();
	    }

	  
	    // Calculate aphelion and perihelion for each body in the simulation
	    /*
	     * Test for j=0
	     */
	    for (int j=1; j < numberOfParticles; j++) {
		double separation = Particle3D.pSep(particleArray[0], particleArray[j]).mag();
		if (aphelionArray[j] < separation) {aphelionArray[j] = separation; }
		if (perihelionArray[j] > separation) {perihelionArray[j] = separation; }
	    }

	    // Increase the time
	    time = time + stepSize;

	    // Prints every k-th position to VMD file
	    // calc min and max energy every k-th step to save on calc
	    if (i % printFrequency == 0) {	   
		output.printf(Particle3D.vmd(particleArray, stepNumber));

	    energy = Particle3D.sysEnergy(particleArray);
	    if (minEnergy > energy) { minEnergy = energy;  }
	    if (maxEnergy < energy) { maxEnergy = energy;  }	
	}

	currentAngle = Math.atan2(particleArray[2].getPosition().getY(), particleArray[2].getPosition().getX());
	// make sure angle is in (0,2*PI) interval
	if (currentAngle < 0) {
	    currentAngle += 2*Math.PI;
	}

 

	    stepNumber++;           


	}

	/*
	 Counting orbits: compute the angular position of the starting point
	 than monitor at every timestep, stop the calculation when back
	 at the start; use Math.atan2(y,x)
	 Need to determine angular direction first 
	 
	 METHODS: counterclockwise

	 Another idea is to test when x-coord is positive for change of sign in y
	 But this will count only full orbits

	 Perihelion/Aphelion - just test for min/max separattion between planet and the Sun

Need to print values of aphelion and perihelion for each body
	*/

	System.out.printf("\nEnergy fluctuation: %e\nThe ratio is %e\n\n", maxEnergy-minEnergy, (maxEnergy-minEnergy)/((minEnergy+maxEnergy)/2) );

	// Close the output file
	output.close();

    }
}
