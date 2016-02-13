/**
 * Computer Modelling, Project
 * Simulation of the Solar System
 *
 * Run with ARGUMENTS: ????output input????
 *
 * @author M. Kirsz
 * @author R. Pruciak
 * @data "02/2016"
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
	
	// Set up an array which stores forces
	// i-th- currentforce: [i][0]
	// i-th newforce: [i][1] 
	int currentForce = 0;
	int newForce = 1;
	Vector3D forceArray[][] = new Vector3D[numberOfParticles][2];

	for (int i = 0; i < numberOfParticles; i++ ) {
	    forceArray[i][currentForce] = new Vector3D();
	    forceArray[i][newForce] = new Vector3D();
	}

	// calculate initial forces
	Particle3D.updateForce(particleArray, forceArray, currentForce);
	
	/*
	 * Start of the Verlet algorithm
	 */
	
	//Prints the initial position to file
	int stepNumber = 1;
	output.printf(Particle3D.vmd(particleArray, stepNumber));
	stepNumber++;

	/*
	 * Loop over timesteps
	 */

	for (int i=0; i<numberOfSteps; i++) {

	    // Update the position using current velocity
	    Particle3D.leapPosition(stepSize, particleArray, forceArray);

	    // Force after time leap
	    Particle3D.updateForce(particleArray, forceArray, newForce);
  
	    // Update the velocity ready for the next position update
	    Particle3D.leapVelocity(stepSize, particleArray, forceArray);

	    // Update force
	    for (int j=0; j < numberOfParticles; j++) {
		forceArray[j][currentForce] = new Vector3D(forceArray[j][newForce]);
		forceArray[j][newForce] = new Vector3D();
	    }

	    // Increase the time
	    time = time + stepSize;

	    // Prints every k-th position to VMD file
	    if (i % printFrequency == 0) {	   
	    output.printf(Particle3D.vmd(particleArray, stepNumber));
	    }
	    stepNumber++;
	    // Prints current time and total energy to file
	    // output.printf("%10.5f %10.10f\n", t, Particle3D.totEnergy(Orbital, Central));
	    
	}
  
	// Close the output file
	output.close();

    }
}
