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

	// NEED TO GENERALIZE: INPUT PARAM FROM A FILE

	// Number of timesteps
	double numberOfSteps = 10000000;

	// Size of timestep
	double stepSize = 10;

	// Initial time
	double time = 0.0;

	// print every k-th step
	int printFrequency = 10000;

	// Opens the output file
	String outFile = "output.xyz";
        PrintWriter output = new PrintWriter(new FileWriter(outFile));
	
	// count number of particles in an input file
	BufferedReader reader = new BufferedReader(new FileReader("input.dat"));
	int numberOfParticles = 0;
	while (reader.readLine() != null) {
	    numberOfParticles++;
	}
	reader.close();
	
	// Attach a scanner to the input file
	BufferedReader inputFile = new BufferedReader(new FileReader("input.dat"));
	Scanner scan = new Scanner(inputFile);

	// Create new array of Particles3D and copy particles from an input file
	Particle3D particleArray[] = new Particle3D[numberOfParticles];
	for (int i=0; scan.hasNext(); i++) {
	    particleArray[i] = Particle3D.pScanner(scan);
	}
	
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
		
	//Prints the initial position to file
	int stepNumber = 1;
	output.printf(Particle3D.vmd(particleArray, stepNumber));
	stepNumber++;


	double newAngle[] = new double[numberOfParticles];
	double prevAngle[] = new double[numberOfParticles];
	double angleDiff[] = new double[numberOfParticles]; 

	double minEnergy = Particle3D.sysEnergy(particleArray);
	double maxEnergy = Particle3D.sysEnergy(particleArray);
	double energy;


	// determine clockwise/anitclockwise orbits
	boolean clockwise[] = new boolean[numberOfParticles];
	for (int j=0; j<numberOfParticles; j++) {
	if ( Vector3D.vecCross(particleArray[j].getPosition(),particleArray[j].getVelocity()).getZ() > 0) {
	    clockwise[j] = false;
	}
	else { clockwise[j] = true; }
	}

	/* 
	 * Start of the Verlet Algorithm
	 *
	 */

	for (int i=0; i<numberOfSteps; i++) {

	    // calc initial angles before the position updatde
	    for (int j=0; j<numberOfParticles; j++) {
		prevAngle[j] = Math.atan2(particleArray[j].getPosition().getY(), particleArray[j].getPosition().getX());
	    }

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


	    // count orbits
	    for (int j=0; j<numberOfParticles; j++) {
	    newAngle[j] = Math.atan2(particleArray[j].getPosition().getY(), particleArray[j].getPosition().getX());

		if (clockwise[j] == true) {

		    if (Math.signum(prevAngle[j]) > Math.signum(newAngle[j])) {
			angleDiff[j] += (Math.abs(newAngle[j]) + prevAngle[j]);
		    }

		    else if (Math.signum(prevAngle[j]) < Math.signum(newAngle[j])) {
			angleDiff[j] += (2*Math.PI -(newAngle[j] - prevAngle[j]));
		    }

		    else {
		    angleDiff[j] += Math.abs(newAngle[j]-prevAngle[j]);
		    }
		}

		// anticlockwise case
		else {		  
		    if (Math.signum(prevAngle[j]) < Math.signum(newAngle[j])) {
			angleDiff[j] += (newAngle[j] + Math.abs(prevAngle[j]));
		    }

		    else if (Math.signum(prevAngle[j]) > Math.signum(newAngle[j])) {
			angleDiff[j] += (2*Math.PI + (newAngle[j] - prevAngle[j]));
		    }

		    else {
		    angleDiff[j] += Math.abs(newAngle[j]-prevAngle[j]);
		    }
		}
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
	*/


	System.out.printf("\nEnergy fluctuation: %e\nThe ratio is %e\n\n", maxEnergy-minEnergy, Math.abs((maxEnergy-minEnergy)/((minEnergy+maxEnergy)/2)) );

	System.out.printf("Number of Venus orbits %f\n", angleDiff[2]/(2*Math.PI));

	// Close the output file
	output.close();

    }
}
